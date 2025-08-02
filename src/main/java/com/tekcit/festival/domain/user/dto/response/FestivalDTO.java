package com.tekcit.festival.domain.user.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FestivalDto {
    private String id;
    private String fname;
    private String fdfrom;
    private String fdto;
    private String poster;
    private String fcltynm;
    private String area;
    private String genrename;
    private String openrun;
    private String fstate;
    private Long hostId;
}