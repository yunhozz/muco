package com.muco.musicservice.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muco.musicservice.application.MusicService;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.global.dto.request.SearchRequestDTO;
import com.muco.musicservice.global.dto.response.MusicListResponseDTO;
import com.muco.musicservice.global.dto.response.SearchResponseDTO;
import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import com.muco.musicservice.global.enums.SearchOrder;
import com.muco.musicservice.interfaces.dto.CreateMusicSimpleRequestDTO;
import com.muco.musicservice.interfaces.dto.ResponseDTO;
import com.muco.musicservice.interfaces.dto.UserInfoClientDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO createMusic(@Valid @RequestBody CreateMusicSimpleRequestDTO dto) throws JsonProcessingException {
        String userInfoRequestUri = UriComponentsBuilder
                .fromUriString("http://localhost:8000/api/users/{id}")
                .build()
                .expand(dto.getUserId())
                .encode().toString();

        ResponseEntity<String> response = new RestTemplate().getForEntity(userInfoRequestUri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO responseDTO = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        UserInfoClientDTO userInfo = objectMapper.convertValue(responseDTO.getData(), UserInfoClientDTO.class);

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

        return ResponseDTO.of("음악을 성공적으로 등록하였습니다.", musicId, Long.class);
    }

    /**
     * 키워드 검색
     */
    @GetMapping("/search/{category}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getMusicListByKeyword(@PathVariable(required = false) String category, @RequestParam String keyword) {
        SearchCategory searchCategory = SearchCategory.of(category);
        SearchResponseDTO dataByCategory = null;
        List<SearchResponseDTO> dataByCategories = new ArrayList<>();

        switch (searchCategory) {
            // TODO: 곡, 아티스트, 가사 검색 추가
            case TOTAL -> {
                List<MusicListResponseDTO> data = musicService.getMusicListByKeywordSearchOnTotalCategory(keyword);
                for (MusicListResponseDTO musicList : data) {
                    dataByCategories.add(musicList);
                }
                return ResponseDTO.of("키워드 통합검색 결과입니다.", dataByCategories, List.class);
            }
            default -> {
                return ResponseDTO.of("키워드 검색 결과가 존재하지 않습니다.");
            }
        }
    }

    /**
     * TODO: 카테고리 별 키워드 검색
     * 통합 검색: 각 조건 별 N건 조회
     * 뮤지션: 성별, 국적, 장르 별 조회
     * 곡: 장르 별 N건 조회
     * 가사: 정확도, 최신순, 가나다순 조회
     */
    @PostMapping("/search/{category}")
    public ResponseDTO getSearchResultsByKeywordAndCategory(@PathVariable String category, @Valid @RequestBody SearchRequestDTO dto, Pageable pageable) {
        SearchCategory searchCategory = SearchCategory.of(category);

        switch (searchCategory) {
            case MUSIC -> musicService.getMusicSearchPageBySearchConditions(dto, pageable);
        }

        return ResponseDTO.of("키워드 검색 결과입니다.");
    }

    @PostMapping("/chart")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getMusicChart(@RequestParam(required = false) Integer cursorRank, @PageableDefault(size = 20) Pageable pageable) {
        Slice<MusicChartQueryDTO> data = musicService.getMusicChartList(cursorRank, pageable);
        return ResponseDTO.of("음악 차트 조회 결과입니다.", data, Slice.class);
    }
}