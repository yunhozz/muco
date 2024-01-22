package com.muco.musicservice.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muco.musicservice.application.MusicService;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.interfaces.dto.CreateMusicSimpleRequestDTO;
import com.muco.musicservice.interfaces.dto.ResponseDTO;
import com.muco.musicservice.interfaces.dto.UserInfoClientDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/music/manage")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO createMusic(@Valid @RequestBody CreateMusicSimpleRequestDTO dto) throws JsonProcessingException {
        String userInfoRequestUri = UriComponentsBuilder
                .fromUriString("http://localhost:8000/api/users/{id}")
                .build()
                .expand(dto.userId())
                .encode().toString();

        ResponseEntity<String> response = new RestTemplate().getForEntity(userInfoRequestUri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO responseDTO = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        UserInfoClientDTO userInfo = objectMapper.convertValue(responseDTO.data(), UserInfoClientDTO.class);

        CreateMusicRequestDTO createMusicRequestDTO = CreateMusicRequestDTO.builder()
                .userId(Long.parseLong(userInfo.id()))
                .email(userInfo.email())
                .age(Integer.parseInt(userInfo.age()))
                .nickname(userInfo.nickname())
                .userImageUrl(userInfo.imageUrl())
                .genres(dto.genres())
                .lyrics(dto.lyrics())
                .musicImageUrl(dto.imageUrl())
                .build();

        Long musicId = musicService.registerMusic(createMusicRequestDTO);

        return ResponseDTO.of("음악을 성공적으로 등록하였습니다.", musicId, Long.class);
    }
}