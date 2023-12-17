package com.muco.authservice.interfaces;

import com.muco.authservice.application.UserService;
import com.muco.authservice.global.dto.req.CodeRequestDTO;
import com.muco.authservice.global.dto.req.SignUpRequestDTO;
import com.muco.authservice.global.dto.res.SignUpResponseDTO;
import com.muco.authservice.global.dto.res.UserResponseDTO;
import com.muco.authservice.global.util.CookieUtils;
import com.muco.authservice.persistence.query.UserInfoQueryDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final static String EMAIL_COOKIE_NAME = "username";
    private final static int EMAIL_COOKIE_MAX_AGE = 3600;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponseDTO> joinByEmail(
            @Valid @RequestBody SignUpRequestDTO dto,
            HttpServletResponse response
    ) {
        SignUpResponseDTO signUpResponseDTO = userService.joinByEmail(dto);
        CookieUtils.addCookie(response, EMAIL_COOKIE_NAME, CookieUtils.serialize(signUpResponseDTO.getEmail()), EMAIL_COOKIE_MAX_AGE);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(signUpResponseDTO);
    }

    @PatchMapping("/verification")
    public ResponseEntity<UserResponseDTO> giveUserRoleByEmailVerifying(
            @Valid @RequestBody CodeRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie cookie = CookieUtils.getCookie(request, EMAIL_COOKIE_NAME)
                .orElseThrow(() -> new IllegalArgumentException("유저 이메일에 대한 쿠키 정보가 존재하지 않습니다."));
        UserResponseDTO userResponseDTO = userService.verifyByCode(CookieUtils.deserialize(cookie, String.class), dto.getCode());
        CookieUtils.deleteCookie(request, response, EMAIL_COOKIE_NAME);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoQueryDTO> getUserInfoById(@PathVariable String id) {
        UserInfoQueryDTO userInfoQueryDTO = userService.findUserInformationById(Long.parseLong(id));
        return ResponseEntity.ok(userInfoQueryDTO);
    }
}