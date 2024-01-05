package com.muco.musicservice.global.dto.response;

import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MusicListResponseDTO implements SearchResponseDTO {

    private List<MusicSimpleQueryDTO> musicSimpleQueryDTOList;
    private int totalCount;
}