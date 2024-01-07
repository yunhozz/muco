package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MusicMusicianCustomRepository {

    Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable);
    List<MusicSimpleQueryDTO> getMusicSimpleListByKeywordAndCategory(String keyword, SearchCategory category);
}