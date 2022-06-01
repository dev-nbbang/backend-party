package com.dev.nbbang.party.domain.party.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PARTICIPANT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PARTY_ID")
    private Party party;

    @Column(name = "PARTICIPANT_ID")
    private String participantId;

    @Column(name = "PARTICIPANT_YMD")
    private LocalDateTime participantYmd;

    @Column(name = "OTT_ID")
    private Long ottId;

    @Builder
    public Participant(Long id, Party party, String participantId, LocalDateTime participantYmd, Long ottId) {
        this.id = id;
        this.party = party;
        this.participantId = participantId;
        this.participantYmd = participantYmd;
        this.ottId = ottId;
    }
}
