package com.dev.nbbang.party.domain.ott.dto.response;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class OttInformationResponse {
    private Long ottId;
    private String ottName;
    private Integer ottHeadcount;
    private Long ottPrice;
    private String ottImage;

    @Builder
    public OttInformationResponse(Long ottId, String ottName, Integer ottHeadcount, Long ottPrice, String ottImage) {
        this.ottId = ottId;
        this.ottName = ottName;
        this.ottHeadcount = ottHeadcount;
        this.ottPrice = ottPrice;
        this.ottImage = ottImage;
    }

    public static OttInformationResponse create(OttDTO ott) {
        return OttInformationResponse.builder()
                .ottId(ott.getOttId())
                .ottName(ott.getOttName())
                .ottHeadcount(ott.getOttHeadcount())
                .ottPrice(ott.getOttPrice())
                .ottImage(ott.getOttImage()).build();
    }

    public static List<OttInformationResponse> createList(List<OttDTO> ottList) {
        List<OttInformationResponse> response = new ArrayList<>();
        for (OttDTO ott : ottList) {
            response.add(OttInformationResponse.builder()
                    .ottId(ott.getOttId())
                    .ottName(ott.getOttName())
                    .ottHeadcount(ott.getOttHeadcount())
                    .ottPrice(ott.getOttPrice())
                    .ottImage(ott.getOttImage()).build());
        }

        return response;
    }
}
