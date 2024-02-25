package com.muco.authservice.domain.interfaces;

import com.muco.authservice.domain.application.UserService;
import com.muco.authservice.domain.interfaces.dto.ResponseDTO;
import com.muco.authservice.domain.interfaces.handler.KafkaHandler;
import com.muco.authservice.global.dto.query.UserInfoQueryDTO;
import com.muco.authservice.global.dto.req.CodeRequestDTO;
import com.muco.authservice.global.dto.req.SignUpRequestDTO;
import com.muco.authservice.global.dto.res.SignUpResponseDTO;
import com.muco.authservice.global.dto.res.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KafkaHandler kafkaHandler;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO joinByEmail(@Valid @RequestBody SignUpRequestDTO dto) {
        SignUpResponseDTO data = userService.joinByEmail(dto);
        return ResponseDTO.of("이메일 가입이 완료되었습니다.", data, SignUpResponseDTO.class);
    }

    @PatchMapping("/verification")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO giveUserRoleByEmailVerifying(@Valid @RequestBody CodeRequestDTO dto, HttpServletRequest request) {
        UserResponseDTO data = userService.verifyByEmailCode(request.getHeader("sub"), dto.getCode());
        /* Call Logout API */
        WebClient webClient = WebClient.create();
        webClient.delete()
                .uri(URI.create("http://localhost:8000/api/auth/sign-out"))
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .toBodilessEntity()
                .subscribe();

        return ResponseDTO.of("인증에 성공하였습니다. 다시 로그인을 진행해주세요.", data, UserResponseDTO.class);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getUserInfoById(@PathVariable String id) {
        UserInfoQueryDTO data = kafkaHandler.sendUserInfoByUserId(Long.parseLong(id));
        return ResponseDTO.of("사용자 조회에 성공하였습니다.", data, UserInfoQueryDTO.class);
    }

    @GetMapping
    public ResponseDTO getUserInfoListByIds(@RequestParam List<String> ids) {
        List<Long> userIds = ids.stream()
                .map(Long::valueOf)
                .toList();
        List<UserInfoQueryDTO> results = userService.findUserInformationListByUserId(userIds);
        return ResponseDTO.of("사용자 정보 리스트입니다.", results, List.class);
    }
}