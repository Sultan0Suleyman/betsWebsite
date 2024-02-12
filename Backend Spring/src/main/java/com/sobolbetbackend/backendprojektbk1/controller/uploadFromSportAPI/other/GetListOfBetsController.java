package com.sobolbetbackend.backendprojektbk1.controller.uploadFromSportAPI.other;

import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.GameBetsResponseDTO;
import com.sobolbetbackend.backendprojektbk1.service.betServices.GetListOfBetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/list")
public class GetListOfBetsController {
    private final GetListOfBetsService getListOfBetsService;

    @Autowired
    public GetListOfBetsController(GetListOfBetsService getListOfBetsService) {
        this.getListOfBetsService = getListOfBetsService;
    }

    @GetMapping("/sports")
    public ResponseEntity<List<String>> getSports(){
        return ResponseEntity.ok(getListOfBetsService.getSports());
    }

    @GetMapping("/countries/{sport}")
    public ResponseEntity<List<String>> getCountries(@PathVariable String sport){
        return ResponseEntity.ok(getListOfBetsService.getCountries(sport));
    }

    @GetMapping("/leagues/{sport}/{country}")
    public ResponseEntity<List<String>> getLeagues(@PathVariable String sport, @PathVariable String country){
        List<String> leagueList = getListOfBetsService.getLeagues(sport,country);
        if(leagueList == null){
            return ResponseEntity.ok().body(null);
        }
        return ResponseEntity.ok(leagueList);
    }

    @GetMapping("/games/{sport}/{country}/{league}")
    public ResponseEntity<List<GameBetsResponseDTO>> getGames(@PathVariable String sport,
                                                              @PathVariable String country, @PathVariable String league){
        return ResponseEntity.ok(getListOfBetsService.getGames(sport,country,league));
    }

    @GetMapping("/gameBets/{gameId}")
    public ResponseEntity<GameBetsResponseDTO> getGameBets(@PathVariable String gameId){
        return ResponseEntity.ok(getListOfBetsService.getGameBets(gameId));
    }

}
