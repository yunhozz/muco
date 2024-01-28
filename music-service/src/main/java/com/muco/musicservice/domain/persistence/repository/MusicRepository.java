package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.domain.persistence.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {
}