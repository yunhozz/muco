package com.muco.musicservice.domain.interfaces.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muco.musicservice.domain.interfaces.dto.UserInfoClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaHandler {

    @KafkaListener(topics = {"user-information"}, groupId = "servicesConsumerGroupId")
    public void receiveUserInfo(ConsumerRecord<String, Object> record) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoClientDTO userInfoClientDTO = objectMapper.readValue(record.value().toString(), new TypeReference<>() {});
        log.info("[USER INFORMATION] " + userInfoClientDTO);
    }
}
