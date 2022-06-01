package com.dev.nbbang.party.domain.ott.dto;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class OttDTO {
    private Long ottId;
    private String ottName;
    private Integer ottHeadcount;
    private Long ottPrice;
    private String ottImage;

    @Builder
    public OttDTO(Long ottId, String ottName, Integer ottHeadcount, Long ottPrice, String ottImage) {
        this.ottId = ottId;
        this.ottName = ottName;
        this.ottHeadcount = ottHeadcount;
        this.ottPrice = ottPrice;
        this.ottImage = ottImage;
    }

    public static OttDTO create(Ott ott) {
        return OttDTO.builder()
                .ottId(ott.getOttId())
                .ottName(ott.getOttName())
                .ottHeadcount(ott.getOttHeadcount())
                .ottPrice(ott.getOttPrice())
                .ottImage(ott.getOttImage())
                .build();
    }

    public static Ott toEntity(OttDTO ott) {
        return Ott.builder()
                .ottId(ott.getOttId())
                .ottName(ott.getOttName())
                .ottHeadcount(ott.getOttHeadcount())
                .ottPrice(ott.getOttPrice())
                .ottImage(ott.getOttImage())
                .build();
    }

    public static List<OttDTO> createList(List<Ott> findOttList) {
        List<OttDTO> ottList = new ArrayList<>();
        for (Ott ott : findOttList) {
            ottList.add(OttDTO.builder()
                    .ottId(ott.getOttId())
                    .ottName(ott.getOttName())
                    .ottHeadcount(ott.getOttHeadcount())
                    .ottPrice(ott.getOttPrice())
                    .ottImage(ott.getOttImage())
                    .build());
        }

        return ottList;
    }
}
