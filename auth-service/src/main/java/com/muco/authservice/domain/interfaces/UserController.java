package com.muco.authservice.domain.interfaces;

import com.muco.authservice.domain.application.UserService;
import com.muco.authservice.domain.interfaces.dto.ResponseDTO;
import com.muco.authservice.domain.persistence.query.UserInfoQueryDTO;
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

    private final UserService userService;

    private static final String EMAIL_COOKIE_NAME = "username";
    private static final int EMAIL_COOKIE_MAX_AGE = 3600;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO joinByEmail(@Valid @RequestBody SignUpRequestDTO dto, HttpServletResponse response) {
        SignUpResponseDTO data = userService.joinByEmail(dto);
        CookieUtils.addCookie(response, EMAIL_COOKIE_NAME, CookieUtils.serialize(data.getEmail()), EMAIL_COOKIE_MAX_AGE);
        return ResponseDTO.of("이메일 가입이 완료되었습니다.", data, SignUpResponseDTO.class);
    }

    @PatchMapping("/verification")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO giveUserRoleByEmailVerifying(
            @Valid @RequestBody CodeRequestDTO dto,
            HttpServletRequest request, HttpServletResponse response
    ) {
        Cookie cookie = CookieUtils.getCookie(request, EMAIL_COOKIE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("유저 이메일에 대한 쿠키 정보가 존재하지 않습니다."));
        UserResponseDTO data = userService.verifyByCode(CookieUtils.deserialize(cookie, String.class), dto.getCode());
        CookieUtils.deleteCookie(request, response, EMAIL_COOKIE_NAME);

        return ResponseDTO.of("인증에 성공하였습니다.", data, UserResponseDTO.class);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getUserInfoById(@PathVariable String id) {
        UserInfoQueryDTO data = userService.findUserInformationById(Long.parseLong(id));
        return ResponseDTO.of("사용자 조회에 성공하였습니다.", data, UserInfoQueryDTO.class);
    }
}