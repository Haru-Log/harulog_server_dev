package goojeans.harulog.user.util;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final Long accessExpiration;
    private final Long refreshExpiration;
    private final UserRepository userRepository;

    private static final String REFRESH_TOKEN_SUB = "RefreshToken";
    private static final String GRANT_TYPE = "Bearer ";

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.access.expiration}") Long accessExpiration,
                            @Value("${jwt.refresh.expiration}") Long refreshExpiration, UserRepository repository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.userRepository = repository;

    }

    // AccessToken 생성 메서드
    public String generateAccessToken(Authentication authentication) {

        // Authentication 으로부터 authority 나열 문자열 가져오기
        String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Authentication 객체에서 유저 닉네임 정보 받아오기
        JwtUserDetail userInfo = (JwtUserDetail) authentication.getPrincipal();

        return GRANT_TYPE + Jwts.builder()
                .subject(authentication.getName())
                .claim("auth", authority)
                .claim("nickname", userInfo.getNickname())
                .signWith(key)
                .expiration(Date.from(Instant.now().plusMillis(accessExpiration)))
                .compact();

    }

    // RefreshToken 생성 메서드
    public String generateRefreshToken() {

        return Jwts.builder()
                .subject(REFRESH_TOKEN_SUB)
                .expiration(Date.from(Instant.now().plusMillis(refreshExpiration)))
                .signWith(key)
                .compact();

    }

    // Access token 으로부터 Authentication 꺼내는 메서드
    public Authentication getAuthentication(Claims claims) {

        if (claims.get("auth") == null) {
            throw new BusinessException(ResponseCode.USER_UNAUTHORIZED);
        }

        // Authority 가져오기
        Collection<? extends GrantedAuthority> auth = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        //TODO: 유효성 체크 시 DB 접근 1 select
        Users user = userRepository.findUsersByNickname(claims.get("nickname").toString()).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(user.getId())
                .username(user.getUserName())
                .password(user.getPassword())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getUserRole())
                .imageUrl(user.getImageUrl())
                .introduction(user.getIntroduction())
                .createdAt(user.getCreatedAt())
                .authorities(auth)
                .build();

        return new UsernamePasswordAuthenticationToken(jwtUserDetail, "", auth);

    }

    public Authentication createAuthentication(Users user) {
        List<GrantedAuthority> auth = List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().getRole()));

        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(user.getId())
                .username(user.getUserName())
                .password(user.getPassword())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getUserRole())
                .imageUrl(user.getImageUrl())
                .introduction(user.getIntroduction())
                .createdAt(user.getCreatedAt())
                .authorities(auth)
                .build();

        return new UsernamePasswordAuthenticationToken(jwtUserDetail, "", auth);

    }

    // 토큰 검증 메서드
    public boolean validate(String token) {

        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;

    }

    // 토큰 만료기간 검증 메서드
    public boolean isExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return false;
        } catch (ExpiredJwtException e){
            return true;
        }
    }

    // Access Token 헤더에서 추출 메서드
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(token ->
                        token.startsWith(GRANT_TYPE))
                .map(token ->
                        token.replace(GRANT_TYPE, ""));
    }

    // Refresh Token 쿠키에서 추출 메서드
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie ->
                        cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue);
    }

    // Claim 가져오는 메서드
    public Optional<Claims> getClaim(String accessToken) {
        try {
            return Optional.of(Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload());
        } catch (ExpiredJwtException e){
            return Optional.of(e.getClaims());
        } catch (JwtException e) {
            log.error(e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException e){
            throw new BusinessException(ResponseCode.USER_UNAUTHORIZED);
        }
    }
}
