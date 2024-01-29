package goojeans.harulog.user.service;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //TODO: 로그인 시 DB 접근 1 select
        Users user = repository.findUsersByEmail(email).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getUserRole().getRole())
                .build();
    }

    public void updateRefreshToken(Users user) {
        repository.updateRefreshToken(user.getRefreshToken(), user.getNickname());
    }
}
