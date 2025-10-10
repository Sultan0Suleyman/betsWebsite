package com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches;

import lombok.Data;

@Data
public class UpdateMatchStatusDTO {
    private Long matchId;
    private String status;
    private String linemakersName;
}
