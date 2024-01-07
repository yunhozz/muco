package com.muco.musicservice.global.dto.response;

import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MusicListResponseDTO implements SearchResponseDTO {

    private SearchCategory category;
    private int totalCount;
    private List<MusicSimpleQueryDTO> musicSimpleQueryDTOList = new ArrayList<>();
}