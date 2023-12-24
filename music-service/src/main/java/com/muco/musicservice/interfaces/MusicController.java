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

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Long>> createMusic(@Valid @RequestBody CreateMusicSimpleRequestDTO dto) throws JsonProcessingException {
        String userInfoRequestUri = UriComponentsBuilder
                .fromUriString("http://localhost:8000/api/users/{id}")
                .build()
                .expand(dto.getUserId())
                .encode().toString();

        ResponseEntity<String> response = new RestTemplate().getForEntity(userInfoRequestUri, String.class);
        ResponseDTO<UserInfoClientDTO> responseDTO = new ObjectMapper()
                .readValue(response.getBody().toString(), new TypeReference<>() {});

        UserInfoClientDTO userInfo = responseDTO.getData();
        CreateMusicRequestDTO createMusicRequestDTO = CreateMusicRequestDTO.builder()
                .userId(Long.parseLong(userInfo.getId()))
                .email(userInfo.getEmail())
                .age(Integer.parseInt(userInfo.getAge()))
                .nickname(userInfo.getNickname())
                .userImageUrl(userInfo.getImageUrl())
                .genres(dto.getGenres())
                .lyrics(dto.getLyrics())
                .musicImageUrl(dto.getImageUrl())
                .build();

        Long musicId = musicService.registerMusic(createMusicRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.of("음악을 성공적으로 등록하였습니다.", musicId));
    }

    // TODO: 음원 단건, 리스트 조회
    @GetMapping("/{id}")
    public ResponseEntity<Void> getMusicInformation(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity<Slice<Void>> getMusicList() {
        return ResponseEntity.ok(null);
    }
}