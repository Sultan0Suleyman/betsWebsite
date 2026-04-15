package com.sobolbetbackend.backendprojektbk1.controller.Linemaker;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches.*;
import com.sobolbetbackend.backendprojektbk1.service.linemakerServices.UnpublishedMatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/linemaker")
public class UnpublishedMatchesController {
    private final UnpublishedMatchesService unpublishedMatchesService;

    @Autowired
    public UnpublishedMatchesController(UnpublishedMatchesService unpublishedMatchesService) {
        this.unpublishedMatchesService = unpublishedMatchesService;
    }

    @GetMapping("/unpublished-matches")
    public ResponseEntity<List<LinemakerMatchInfoDTO>> getUnpublishedMatches() {
        return ResponseEntity.ok(unpublishedMatchesService.getUnpublishedMatches());
    }

    @GetMapping("/me")
    public ResponseEntity<LinemakersNameSurnameDTO> getCurrentUser(@RequestParam String username) {
        return ResponseEntity.ok(unpublishedMatchesService.getLinemakersNameSurname(username));
    }

    @PatchMapping("/match-status")
    public ResponseEntity<?> updateMatchStatus(@RequestBody UpdateMatchStatusDTO dto) {
        try {
            unpublishedMatchesService.updateMatchStatus(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update match status");
        }
    }

    @DeleteMapping("/delete-match/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long id) {
        try {
            unpublishedMatchesService.deleteMatch(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Match with id " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete match");
        }
    }

    @GetMapping("unpublished-match-odds-details/{id}")
    public ResponseEntity<UnpublishedMatchOddsDetailsDTO> getUnpublishedMatchOddsDetails(@PathVariable Long id) {
        return ResponseEntity.ok(unpublishedMatchesService.getUnpublishedMatchOddsDetails(id));
    }

    @PostMapping("/set-odds")
    public ResponseEntity<?> setOdds(@RequestBody SetOddsRequestDTO request) {
        try {
            unpublishedMatchesService.saveOdds(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to set odds");
        }
    }

    @PostMapping("publish-match/{id}")
    public ResponseEntity<?> publishMatch(@PathVariable Long id){
        try {
            unpublishedMatchesService.publishMatch(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to publish match");
        }
    }

    @PostMapping("unpublish-match/{id}")
    public ResponseEntity<?> unPublishMatch(@PathVariable Long id){
        try {
            unpublishedMatchesService.unPublishMatch(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unpublish match");
        }
    }
}
