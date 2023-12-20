package com.muco.musicservice.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muco.musicservice.application.MusicService;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.global.dto.request.CreateMusicSimpleRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    public ResponseEntity<Long> createMusic(@Valid @RequestBody CreateMusicSimpleRequestDTO dto) throws JsonProcessingException {
        String userInfoRequestUri = UriComponentsBuilder
                .fromUriString("http://localhost:8000/api/users/{id}")
                .build()
                .expand(dto.getUserId())
                .encode().toString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> obj = restTemplate.getForEntity(userInfoRequestUri, Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> userInfo = objectMapper.readValue(obj.toString(), new TypeReference<>() {});
        CreateMusicRequestDTO createMusicRequestDTO = CreateMusicRequestDTO.builder()
                .userId(Long.parseLong(userInfo.get("id")))
                .email(userInfo.get("email"))
                .age(Integer.parseInt(userInfo.get("age")))
                .nickname(userInfo.get("nickname"))
                .userImageUrl(userInfo.get("imageUrl"))
                .genres(dto.getGenres())
                .lyrics(dto.getLyrics())
                .musicImageUrl(dto.getImageUrl())
                .build();

        Long musicId = musicService.registerMusic(createMusicRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(musicId);
    }

    // TODO: 음원 단건, 리스트 조회
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMusicInformation(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity<Slice<Object>> getMusicList() {
        return ResponseEntity.ok(null);
    }
}