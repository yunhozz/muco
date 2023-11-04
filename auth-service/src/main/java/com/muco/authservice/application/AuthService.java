package com.muco.authservice.application;

import com.muco.authservice.global.auth.jwt.JwtProvider;
import com.muco.authservice.global.auth.security.UserDetailsImpl;
import com.muco.authservice.global.dto.req.SignInRequestDTO;
import com.muco.authservice.global.dto.res.TokenResponseDTO;
import com.muco.authservice.global.enums.Role;
import com.muco.authservice.global.util.RedisUtils;
import com.muco.authservice.persistence.entity.User;
import com.muco.authservice.persistence.entity.UserPassword;
import com.muco.authservice.persistence.repo.UserPasswordRepository;
import com.muco.authservice.persistence.repo.UserProfileRepository;
import com.muco.authservice.persistence.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPasswordRepository userPasswordRepository;

    private final JwtProvider jwtProvider;
    private final RedisUtils redisUtils;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public TokenResponseDTO login(SignInRequestDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일의 유저를 찾을 수 없습니다. Email = " + email));
        UserPassword userPassword = userPasswordRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("해당 유저의 패스워드가 존재하지 않습니다. User Email = " + email));

        validatePassword(password, userPassword); // 비밀번호 검증

        // 로그인 성공 시 오류 횟수 초기화
        if (userPassword.isRetryCountMoreThan(0)) {
            userPasswordRepository.resetRetryCountById(userPassword.getId());
        }

        return generateJwtToken(String.valueOf(user.getId()), email, user.getRoles());
    }

    @Transactional(readOnly = true)
    public Optional<TokenResponseDTO> refreshJwtTokens(Long userId) {
        String email = getEmailByUserId(userId);
        if (redisUtils.getValue(email).isPresent()) {
            String refreshToken = redisUtils.getValue(email).get();
            UserDetailsImpl userDetails = getUserDetails(refreshToken);

            return Optional.of(generateJwtToken(userDetails.getUsername(), email, userDetails.getRoles()));

        } else {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public void logout(Long userId, String token) {
        String email = getEmailByUserId(userId);
        redisUtils.getValue(email).ifPresent(refreshToken -> {
            redisUtils.deleteValue(email);
            redisUtils.saveValue(token, "LOGOUT", Duration.ofMinutes(10)); // 10분간 로그아웃 토큰 저장
        });
    }

    private TokenResponseDTO generateJwtToken(String userId, String email, Set<Role> roles) {
        TokenResponseDTO tokenResponseDTO = jwtProvider.createJwtTokenDTO(userId, roles);
        redisUtils.saveValue(email, tokenResponseDTO.getRefreshToken(), Duration.ofMillis(tokenResponseDTO.getRtkValidTime()));
        return tokenResponseDTO;
    }

    private UserDetailsImpl getUserDetails(String token) {
        Authentication authentication = jwtProvider.getAuthentication(token);
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    private String getEmailByUserId(Long userId) {
        return userProfileRepository.findEmailByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 id의 유저의 이메일을 찾을 수 없습니다. ID = " + userId));
    }

    private void validatePassword(String password, UserPassword userPassword) {
        if (!encoder.matches(password, userPassword.getPassword())) {
            userPasswordRepository.addRetryCountById(userPassword.getId()); // 오류 카운트 + 1
            if (userPassword.isRetryCountMoreThan(5)) {
                //TODO: 5회 이상 비밀번호 오류 시 해당 계정 로그인 제한
                throw new RuntimeException("3회 이상 비밀번호를 잘못 입력하셨습니다.");
            }
            throw new RuntimeException("패스워드를 잘못 입력하셨습니다. Retry Count = " + userPassword.getRetryCount());
        }
    }
}