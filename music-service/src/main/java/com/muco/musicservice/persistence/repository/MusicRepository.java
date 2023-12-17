package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.persistence.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {
}