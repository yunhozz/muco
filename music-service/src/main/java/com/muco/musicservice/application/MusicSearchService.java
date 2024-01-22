package com.muco.musicservice.application;

import com.muco.musicservice.global.dto.request.SearchRequestDTO;
import com.muco.musicservice.global.dto.response.SearchResponseDTO;
import com.muco.musicservice.global.dto.response.SearchResultResponseDTO;
import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import com.muco.musicservice.global.enums.SearchCondition;
import com.muco.musicservice.persistence.repository.MusicMusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicSearchService {

    private final MusicMusicianRepository musicMusicianRepository;

    @Transactional(readOnly = true)
    public Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable) {
        return musicMusicianRepository.getMusicChartList(cursorRank, pageable);
    }

    @Transactional(readOnly = true)
    public List<SearchResultResponseDTO> getSearchResultsByKeywordAndCategories(String keyword) {
        return new ArrayList<>() {{
            add(new SearchResultResponseDTO(
                    SearchCategory.MUSIC,
                    musicMusicianRepository.getMusicSimpleListByKeyword(keyword)
            ));
            add(new SearchResultResponseDTO(
                    SearchCategory.MUSICIAN,
                    musicMusicianRepository.getMusicianSimpleListByKeyword(keyword)
            ));
            add(new SearchResultResponseDTO(
                    SearchCategory.PLAYLIST,
                    musicMusicianRepository.getPlaylistSimpleListByKeyword(keyword)
            ));
        }};
    }

    @Transactional(readOnly = true)
    public Page<? extends SearchResponseDTO> getSearchPageByCategoryAndCondition(SearchRequestDTO dto, Pageable pageable) {
        String keyword = dto.keyword();
        SearchCondition condition = dto.searchCondition();
        switch (dto.category()) {
            case MUSIC -> {
                return musicMusicianRepository
                        .getMusicSearchPageByKeywordAndConditions(keyword, condition, pageable);
            }
            case MUSICIAN -> {
                return musicMusicianRepository
                        .getMusicianSearchPageByKeywordAndConditions(keyword, condition, pageable);
            }
            case PLAYLIST -> {
                return musicMusicianRepository
                        .getPlaylistSearchPageByKeywordAndConditions(keyword, condition, pageable);
            }
            default -> {
                return null;
            }
        }
    }
}