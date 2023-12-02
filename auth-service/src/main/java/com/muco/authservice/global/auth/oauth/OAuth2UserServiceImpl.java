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
        final User[] users = joinOrUpdateUser(provider); // 기존 로컬 유저인 경우 업데이트 진행, 신규 유저인 경우 회원가입 진행

        return UserDetailsImpl.ofSocial(users[0].getId(), users[0].getRoles(), attributes);
    }

    private User[] joinOrUpdateUser(OAuth2Provider provider) {
        final User[] users = {null};
        userProfileRepository.findWithUserByEmail(provider.getEmail()).ifPresentOrElse(userProfile -> {
            User user = userProfile.getUser();
            user.updateBySocialLogin(provider.getLoginType());
            userProfile.updateBySocialLogin(provider.getName(), provider.getImageUrl());
            users[0] = user;
        }, () -> {
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
            users[0] = user;
        });

        return users;
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
        } while (userProfileRepository.existsUserProfileByNickname(randomNickname.toString()));

        return randomNickname.toString();
    }
}