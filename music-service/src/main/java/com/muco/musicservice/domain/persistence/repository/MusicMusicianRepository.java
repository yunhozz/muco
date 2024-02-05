package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.domain.persistence.entity.MusicMusician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicMusicianRepository extends JpaRepository<MusicMusician, Long>, MusicMusicianCustomRepository {
}