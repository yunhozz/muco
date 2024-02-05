package com.muco.authservice.global.auth.oauth;

import com.muco.authservice.domain.persistence.entity.User;
import com.muco.authservice.domain.persistence.entity.UserProfile;
import com.muco.authservice.domain.persistence.repo.UserProfileRepository;
import com.muco.authservice.domain.persistence.repo.UserRepository;
import com.muco.authservice.global.auth.security.UserDetailsImpl;
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
import java.util.Random;

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
        User user = joinOrUpdateUser(provider); // 기존 로컬 유저인 경우 업데이트 진행, 신규 유저인 경우 회원가입 진행

        return UserDetailsImpl.ofSocial(user.getId(), user.getRoles(), attributes);
    }

    private User joinOrUpdateUser(OAuth2Provider provider) {
        return userProfileRepository.findWithUserByEmail(provider.getEmail())
                .map(userProfile -> {
                    User user = userProfile.getUser();
                    user.updateBySocialLogin(provider.getLoginType());
                    userProfile.updateBySocialLogin(provider.getName(), provider.getImageUrl());
                    return user;
                }).orElseGet(() -> {
                    User user = new User(provider.getLoginType());
                    UserProfile userProfile = UserProfile.createUserProfile(
                            user,
                            provider.getEmail(),
                            provider.getName(),
                            -1,
                            createRandomNickname(),
                            provider.getImageUrl()
                    );
                    userRepository.save(user);
                    userProfileRepository.save(userProfile);
                    return user;
                });
    }

    private String createRandomNickname() {
        StringBuilder randomNickname;
        do {
            randomNickname = new StringBuilder();
            Random rnd = new Random(System.currentTimeMillis());
            for (int i = 0; i < 8; i++) {
                int index = rnd.nextInt(3);
                switch (index) {
                    case 0 -> randomNickname.append((char) (rnd.nextInt(26) + 97)); // a~z
                    case 1 -> randomNickname.append((char) (rnd.nextInt(26) + 65)); // A~Z
                    case 2 -> randomNickname.append((rnd.nextInt(10))); // 0~9
                }
            }
        } while (userProfileRepository.existsByNickname(randomNickname.toString()));

        return randomNickname.toString();
    }
}