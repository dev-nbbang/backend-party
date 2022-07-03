package com.dev.nbbang.party.global.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotifyRequest {
    private String notifySender;
    private String notifyReceiver;
    private String notifyDetail;
    private LocalDateTime notifyYmd;
    private String notifyType;
    private long notifyTypeId;

    @Builder
    public NotifyRequest(String notifySender, String notifyReceiver, String notifyDetail, LocalDateTime notifyYmd, String notifyType, long notifyTypeId) {
        this.notifySender = notifySender;
        this.notifyReceiver = notifyReceiver;
        this.notifyDetail = notifyDetail;
        this.notifyYmd = notifyYmd;
        this.notifyType = notifyType;
        this.notifyTypeId = notifyTypeId;
    }
}
