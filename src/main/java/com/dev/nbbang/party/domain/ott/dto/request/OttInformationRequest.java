package com.dev.nbbang.party.domain.ott.dto.request;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OttInformationRequest {
    private String ottName;
    private Integer ottHeadcount;
    private Long ottPrice;
    private String ottImage;

    @Builder
    public OttInformationRequest(String ottName, Integer ottHeadcount, Long ottPrice, String ottImage) {
        this.ottName = ottName;
        this.ottHeadcount = ottHeadcount;
        this.ottPrice = ottPrice;
        this.ottImage = ottImage;
    }

    public static Ott toEntity(OttInformationRequest request) {
        return Ott.builder()
                .ottName(request.getOttName())
                .ottHeadcount(request.getOttHeadcount())
                .ottPrice(request.getOttPrice())
                .ottImage(request.getOttImage())
                .build();
    }
}
