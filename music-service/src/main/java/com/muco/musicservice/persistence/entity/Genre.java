package com.muco.musicservice.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Genre {

    private String type;

    @Column(columnDefinition = "TINYINT(1)")
    private int priority;
}