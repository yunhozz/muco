package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicianSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.PlaylistSearchQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import com.muco.musicservice.global.enums.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MusicMusicianCustomRepository {

    Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable);
    List<MusicSimpleQueryDTO> getMusicSimpleListByKeywordAndCategory(String keyword, SearchCategory category);
    Page<MusicSearchQueryDTO> getMusicSearchPageByKeywordAndConditions(String keyword, SearchCondition condition, Pageable pageable);
    Page<MusicianSearchQueryDTO> getMusicianSearchPageByKeywordAndConditions(String keyword, SearchCondition condition, Pageable pageable);
    Page<PlaylistSearchQueryDTO> getPlaylistSearchPageByKeywordAndConditions(String keyword, SearchCondition condition, Pageable pageable);
}