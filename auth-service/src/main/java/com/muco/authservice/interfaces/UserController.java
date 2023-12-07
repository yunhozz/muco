package com.muco.authservice.interfaces;

import com.muco.authservice.application.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public ResponseEntity<Object> giveUserRoleByEmailVerifying(
            @Valid @RequestBody CodeRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie cookie = CookieUtils.getCookie(request, EMAIL_COOKIE_NAME)
                .orElseThrow();
        UserResponseDTO userResponseDTO = userService.verifyByCode(CookieUtils.deserialize(cookie, String.class), dto.getCode());
        CookieUtils.deleteCookie(request, response, EMAIL_COOKIE_NAME);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponseDTO);
    }
}