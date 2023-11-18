package com.muco.authservice.global.auth.oauth;

import com.muco.authservice.global.auth.security.UserDetailsImpl;
import com.muco.authservice.persistence.entity.User;
import com.muco.authservice.persistence.entity.UserProfile;
import com.muco.authservice.persistence.repo.UserProfileRepository;
import com.muco.authservice.persistence.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        ClientRegistration registration = userRequest.getClientRegistration();

        String registrationId = registration.getRegistrationId();
        String userNameAttributeName = registration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Provider provider = OAuth2Provider.of(registrationId, userNameAttributeName, attributes);
        final User[] users = {null};

        /* 기존 로컬 유저인 경우 업데이트 진행, 신규 유저인 경우 회원가입 진행 */
        userProfileRepository.findWithUserByEmail(provider.getEmail()).ifPresentOrElse(userProfile -> {
            User user = userProfile.getUser();
            user.updateBySocialLogin(provider.getLoginType());
            userProfile.updateBySocialLogin(provider.getName(), provider.getImageUrl());
            users[0] = user;
        }, () -> {
            // TODO: 소셜 로그인 가입 후 나이, 닉네임 설정
            User user = new User(provider.getLoginType());
            UserProfile userProfile = UserProfile.createUserProfile(
                    user,
                    provider.getEmail(),
                    provider.getName(),
                    -1,
                    "Random Nickname",
                    provider.getImageUrl()
            );
            userRepository.save(user);
            userProfileRepository.save(userProfile);
            users[0] = user;
        });

        return UserDetailsImpl.ofSocial(users[0].getId(), users[0].getRoles(), attributes);
    }
}