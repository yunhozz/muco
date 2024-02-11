package com.muco.authservice.domain.interfaces;

import com.muco.authservice.domain.application.UserService;
import com.muco.authservice.domain.interfaces.dto.ResponseDTO;
import com.muco.authservice.domain.interfaces.handler.KafkaHandler;
import com.muco.authservice.global.dto.query.UserInfoQueryDTO;
import com.muco.authservice.global.dto.req.CodeRequestDTO;
import com.muco.authservice.global.dto.req.SignUpRequestDTO;
import com.muco.authservice.global.dto.res.SignUpResponseDTO;
import com.muco.authservice.global.dto.res.UserResponseDTO;
import com.muco.authservice.global.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final String VERIFYING_EMAIL = "verifying_email";
    private static final String VERIFYING_CODE = "verifying_code";
    private static final int VERIFYING_MAX_AGE = 3600;

    private final UserService userService;
    private final KafkaHandler kafkaHandler;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO joinByEmail(@Valid @RequestBody SignUpRequestDTO dto, HttpServletResponse response) {
        SignUpResponseDTO data = userService.joinByEmail(dto);
        CookieUtils.addCookie(response, VERIFYING_EMAIL, CookieUtils.serialize(data.getEmail()), VERIFYING_MAX_AGE);
        CookieUtils.addCookie(response, VERIFYING_CODE, CookieUtils.serialize(data.getCode()), VERIFYING_MAX_AGE);

        return ResponseDTO.of("이메일 가입이 완료되었습니다.", data, SignUpResponseDTO.class);
    }

    @PatchMapping("/verification")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO giveUserRoleByEmailVerifying(
            @Valid @RequestBody CodeRequestDTO dto,
            HttpServletRequest request, HttpServletResponse response
    ) {
        Cookie emailCookie = CookieUtils.getCookie(request, VERIFYING_EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("유저 이메일에 대한 쿠키 정보가 존재하지 않습니다."));
        Cookie codeCookie = CookieUtils.getCookie(request, VERIFYING_CODE)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 존재하지 않습니다."));

        String email = CookieUtils.deserialize(emailCookie, String.class);
        String code = CookieUtils.deserialize(codeCookie, String.class);
        UserResponseDTO data = userService.verifyByCode(email, code, dto.getCode());

        CookieUtils.deleteCookie(request, response, VERIFYING_EMAIL);
        CookieUtils.deleteCookie(request, response, VERIFYING_CODE);

        return ResponseDTO.of("인증에 성공하였습니다.", data, UserResponseDTO.class);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getUserInfoById(@PathVariable String id) {
        UserInfoQueryDTO data = kafkaHandler.sendUserInfoByUserId(Long.parseLong(id));
        return ResponseDTO.of("사용자 조회에 성공하였습니다.", data, UserInfoQueryDTO.class);
    }
}