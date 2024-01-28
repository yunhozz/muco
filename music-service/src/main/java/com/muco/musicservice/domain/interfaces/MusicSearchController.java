package com.muco.musicservice.domain.interfaces;

import com.muco.musicservice.domain.application.MusicSearchService;
import com.muco.musicservice.global.dto.request.SearchRequestDTO;
import com.muco.musicservice.global.dto.response.SearchResponseDTO;
import com.muco.musicservice.global.dto.response.SearchResultResponseDTO;
import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import com.muco.musicservice.global.enums.SearchCondition;
import com.muco.musicservice.domain.interfaces.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/music/search")
@RequiredArgsConstructor
public class MusicSearchController {

    private final MusicSearchService musicSearchService;

    /**
     * 키워드 검색
     */
    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getMusicListByKeyword(
            @PathVariable(required = false) String category,
            @RequestParam String keyword,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        SearchCategory searchCategory = SearchCategory.of(category);
        switch (searchCategory) {
            case TOTAL -> {
                List<SearchResultResponseDTO> results = musicSearchService.getSearchResultsByKeywordAndCategories(keyword);
                return ResponseDTO.of("키워드 통합검색 결과입니다.", results, List.class);
            }
            case MUSIC -> {
                SearchRequestDTO searchRequestDTO = new SearchRequestDTO(keyword, SearchCategory.MUSIC, SearchCondition.LATEST);
                Page<? extends SearchResponseDTO> results = musicSearchService.getSearchPageByCategoryAndCondition(searchRequestDTO, pageable);
                return ResponseDTO.of("검색 조건에 따른 음원 키워드 조회 결과입니다.", results, Page.class);
            }
            case MUSICIAN -> {
                SearchRequestDTO searchRequestDTO = new SearchRequestDTO(keyword, SearchCategory.MUSICIAN, SearchCondition.LATEST);
                Page<? extends SearchResponseDTO> results = musicSearchService.getSearchPageByCategoryAndCondition(searchRequestDTO, pageable);
                return ResponseDTO.of("검색 조건에 따른 아티스트 키워드 조회 결과입니다.", results, Page.class);
            }
            case PLAYLIST -> {
                SearchRequestDTO searchRequestDTO = new SearchRequestDTO(keyword, SearchCategory.PLAYLIST, SearchCondition.LATEST);
                Page<? extends SearchResponseDTO> results = musicSearchService.getSearchPageByCategoryAndCondition(searchRequestDTO, pageable);
                return ResponseDTO.of("검색 조건에 따른 재생목록 키워드 조회 결과입니다.", results, Page.class);
            }
            default -> {
                return ResponseDTO.of("키워드 검색 결과가 존재하지 않습니다.");
            }
        }
    }

    /**
     * 카테고리 별 키워드 조건 검색
     */
    @PostMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getSearchResultsByKeywordAndCategory(
            @PathVariable String category,
            @Valid @RequestBody SearchRequestDTO dto,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        SearchCategory searchCategory = SearchCategory.of(category);
        Page<? extends SearchResponseDTO> results = musicSearchService.getSearchPageByCategoryAndCondition(dto, pageable);
        return ResponseDTO.of("키워드 검색 결과입니다.", results, Page.class);
    }

    @PostMapping("/chart")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getMusicChart(@RequestParam(required = false) Integer cursorRank, @PageableDefault(size = 20) Pageable pageable) {
        Slice<MusicChartQueryDTO> data = musicSearchService.getMusicChartList(cursorRank, pageable);
        return ResponseDTO.of("음악 차트 조회 결과입니다.", data, Slice.class);
    }
}