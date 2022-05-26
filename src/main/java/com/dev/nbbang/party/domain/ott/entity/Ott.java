package com.dev.nbbang.party.domain.ott.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "OTT")
@Getter
@NoArgsConstructor
public class Ott {
    @Id
    @Column(name = "OTT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ottId;

    @Column(name = "OTT_NAME")
    private String ottName;

    @Column(name = "OTT_HEADCOUNT")
    private Integer ottHeadcount;

    @Column(name = "OTT_PRICE")
    private Long ottPrice;

    @Column(name = "OTT_IMAGE")
    private String ottImage;

    @Builder
    public Ott(Long ottId, String ottName, Integer ottHeadcount, Long ottPrice, String ottImage) {
        this.ottId = ottId;
        this.ottName = ottName;
        this.ottHeadcount = ottHeadcount;
        this.ottPrice = ottPrice;
        this.ottImage = ottImage;
    }

    // Ott 플랫폼 정보 수정
    public void updateOtt(String ottName, Integer ottHeadcount, Long ottPrice, String ottImage) {
        this.ottName = ottName;
        this.ottHeadcount = ottHeadcount;
        this.ottPrice = ottPrice;
        this.ottImage = ottImage;
    }
}
