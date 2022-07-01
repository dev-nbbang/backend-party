package com.dev.nbbang.party.domain.party.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MatchingRequest {
    private String NOTIFY_SENDER;
    private String NOTIFY_RECEIVER;
    private String NOTIFY_DETAIL;
    private LocalDateTime NOTIFY_YMD;
    private String NOTIFY_TYPE;
    private long NOTIFY_TYPE_ID;

    @Builder
    public MatchingRequest(String NOTIFY_SENDER, String NOTIFY_RECEIVER, String NOTIFY_DETAIL, LocalDateTime NOTIFY_YMD, String NOTIFY_TYPE, long NOTIFY_TYPE_ID) {
        this.NOTIFY_SENDER = NOTIFY_SENDER;
        this.NOTIFY_RECEIVER = NOTIFY_RECEIVER;
        this.NOTIFY_DETAIL = NOTIFY_DETAIL;
        this.NOTIFY_YMD = NOTIFY_YMD;
        this.NOTIFY_TYPE = NOTIFY_TYPE;
        this.NOTIFY_TYPE_ID = NOTIFY_TYPE_ID;
    }
}
