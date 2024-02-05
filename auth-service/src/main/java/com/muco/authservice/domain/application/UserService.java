package com.muco.authservice.domain.application;

import com.muco.authservice.domain.application.exception.EmailDuplicateException;
import com.muco.authservice.domain.application.exception.EmailVerifyFailException;
import com.muco.authservice.domain.application.exception.MailSendFailException;
import com.muco.authservice.domain.application.exception.PasswordDifferentException;
import com.muco.authservice.domain.application.exception.UserNotFoundException;
import com.muco.authservice.domain.application.exception.VerifyingCodeNotFoundException;
import com.muco.authservice.domain.persistence.entity.User;
import com.muco.authservice.domain.persistence.entity.UserPassword;
import com.muco.authservice.domain.persistence.entity.UserProfile;
import com.muco.authservice.domain.persistence.query.UserInfoQueryDTO;
import com.muco.authservice.domain.persistence.repo.UserPasswordRepository;
import com.muco.authservice.domain.persistence.repo.UserProfileRepository;
import com.muco.authservice.domain.persistence.repo.UserRepository;
import com.muco.authservice.global.dto.req.SignUpRequestDTO;
import com.muco.authservice.global.dto.res.SignUpResponseDTO;
import com.muco.authservice.global.dto.res.UserResponseDTO;
import com.muco.authservice.global.enums.LoginType;
import com.muco.authservice.global.util.RedisUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPasswordRepository userPasswordRepository;

    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder encoder;

    @Transactional(noRollbackFor = MailSendFailException.class)
    public SignUpResponseDTO joinByEmail(SignUpRequestDTO dto) {
        String email = dto.getEmail();
        String password1 = dto.getPassword1();
        String password2 = dto.getPassword2();

        if (userProfileRepository.existsByEmail(email)) {
            throw new EmailDuplicateException("중복되는 이메일이 존재합니다. Email = " + email);
        }
        if (!password1.equals(password2)) {
            throw new PasswordDifferentException("비밀번호를 다시 한번 확인해주세요.");
        }

        User user = new User(LoginType.LOCAL);
        UserProfile userProfile = UserProfile.createUserProfile(
                user,
                email,
                dto.getName(),
                dto.getAge(),
                dto.getNickname(),
                dto.getImageUrl()
        );
        UserPassword userPassword = new UserPassword(user, encoder.encode(password2));

        userRepository.save(user);
        userProfileRepository.save(userProfile);
        userPasswordRepository.save(userPassword);

        /* 회원가입 인증 메일 발송 */
        MimeMessage message = mailSender.createMimeMessage();
        String code = createCode();
        String text = makeVerifyingText(code);

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, false, "UTF-8");
            messageHelper.setTo(email);
            messageHelper.setSubject("[MUCO] 회원가입 인증 메일입니다.");
            messageHelper.setText(text, true);
            mailSender.send(message);

            RedisUtils.saveValue(email, code, Duration.ofHours(1)); // redis 에 인증 코드 저장

        } catch (MailException | MessagingException e) {
            throw new MailSendFailException("메일 전송에 실패하였습니다. 원인 : " + e.getLocalizedMessage());
        }

        return new SignUpResponseDTO(user.getId(), email);
    }

    @Transactional
    public UserResponseDTO verifyByCode(String email, String code) {
        String verifyCode = RedisUtils.getValue(email)
                .orElseThrow(() -> new VerifyingCodeNotFoundException("코드 유효 시간이 지났습니다. 다시 발급해주세요."));
        if (!code.equals(verifyCode)) {
            throw new EmailVerifyFailException("입력하신 인증 코드가 일치하지 않습니다. 다시 입력해주세요.");
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 이메일의 유저를 찾을 수 없습니다. Email = " + email));
        user.addUserByEmailVerify();

        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserInfoQueryDTO findUserInformationById(Long id) {
        return userProfileRepository.findUserInfoById(id)
                .orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다. id = " + id));
    }

    private String createCode() {
        StringBuilder code = new StringBuilder();
        Random rnd = new Random(System.currentTimeMillis());

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤
            switch (index) {
                case 0 -> code.append((char) (rnd.nextInt(26) + 97)); // a~z
                case 1 -> code.append((char) (rnd.nextInt(26) + 65)); // A~Z
                case 2 -> code.append((rnd.nextInt(10))); // 0~9
            }
        }

        return code.toString();
    }

    private static String makeVerifyingText(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body>" +
                "<div style='margin:100px;'>" +
                "<h1> 안녕하세요! MUCO 입니다. </h1>" +
                "<br>" +
                "<p>아래 코드를 회원가입 인증 페이지에서 입력해주세요<p>" +
                "<br>" +
                "<p>감사합니다!<p>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana';>" +
                "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>" +
                "<div style='font-size:130%'>" +
                "CODE : <strong>" + code + "</strong><div><br/> " +
                "</div>" +
                "</body>" +
                "</html>";
    }
}