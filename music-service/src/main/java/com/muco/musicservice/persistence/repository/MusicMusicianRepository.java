package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.persistence.entity.MusicMusician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicMusicianRepository extends JpaRepository<MusicMusician, Long>, MusicMusicianCustomRepository {
}