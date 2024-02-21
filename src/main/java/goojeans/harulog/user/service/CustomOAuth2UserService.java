package goojeans.harulog.user.service;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.category.repository.CategoryRepository;
import goojeans.harulog.user.domain.dto.CustomOAuth2User;
import goojeans.harulog.user.domain.dto.KakaoOAuthAttribute;
import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserGoalRepository;
import goojeans.harulog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserGoalRepository userGoalRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("oauth service activate");

        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        KakaoOAuthAttribute extractAttributes = KakaoOAuthAttribute.of(userNameAttributeName, attributes);

        Users createdUser = getUser(extractAttributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getUserRole().getRole())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser
        );
    }

    private Users getUser(KakaoOAuthAttribute attributes) {
        Users findUser = userRepository.findUsersBySocialId(attributes.getOauth2UserInfo().getId()).orElse(null);

        if(findUser == null) {
            Users user = saveUser(attributes);

            List<Category> allCategory = categoryRepository.findAll();
            allCategory.forEach(category ->
                    userGoalRepository.save(UserGoal.of(user, category, 0)));

            return user;
        }
        return findUser;
    }


    private Users saveUser(KakaoOAuthAttribute attributes) {
        Users createdUser = attributes.toEntity(attributes.getOauth2UserInfo());
        return userRepository.save(createdUser);
    }
}
