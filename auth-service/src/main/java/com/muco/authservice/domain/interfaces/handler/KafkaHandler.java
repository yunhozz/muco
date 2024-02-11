package com.muco.authservice.domain.interfaces.handler;

import com.muco.authservice.domain.application.UserService;
import com.muco.authservice.global.dto.query.UserInfoQueryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserService userService;

    public UserInfoQueryDTO sendUserInfoByUserId(Long userId) {
        UserInfoQueryDTO userInfoQueryDTO = userService.findUserInformationById(userId);
        kafkaTemplate.send("user-information", userInfoQueryDTO);
        return userInfoQueryDTO;
    }
}
