package com.sobolbetbackend.backendprojektbk1.controller.Linemaker;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches.LinemakerMatchInfoDTO;
import com.sobolbetbackend.backendprojektbk1.service.linemakerServices.LineMatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/linemaker")
public class LineMatchesController {
    private final LineMatchesService lineMatchesService;

    @Autowired
    public LineMatchesController(LineMatchesService lineMatchesService) {
        this.lineMatchesService = lineMatchesService;
    }

    @GetMapping("/line-matches")
    public ResponseEntity<List<LinemakerMatchInfoDTO>> getLineMatches() {
        return ResponseEntity.ok(lineMatchesService.getLineMatches());
    }
}
