package com.sobolbetbackend.backendprojektbk1.service.linemakerServices;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches.LinemakerMatchInfoDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineMatchesService {
    private final GameRepo gameRepo;

    @Autowired
    public LineMatchesService(GameRepo gameRepo) {
        this.gameRepo = gameRepo;
    }

    public List<LinemakerMatchInfoDTO> getLineMatches(){
        List<Game> lineMatches = gameRepo.findByIsGamePosted(true);
        List<LinemakerMatchInfoDTO> linemakerMatchInfoDTOS = new ArrayList<>();
        for (Game game : lineMatches) {
            // Check for null before calling getName()
            String countryName = game.getCountry() != null ? game.getCountry().getName() : null;

            linemakerMatchInfoDTOS.add(new LinemakerMatchInfoDTO(
                    game.getId().toString(),
                    game.getSport().getName_en(),
                    countryName,  // Can be null
                    game.getLeague().getName(),
                    game.getTeamHome().getName_en(),
                    game.getTeamAway().getName_en(),
                    game.getDateOfMatch().toString(),
                    game.getStatus().toString(),
                    game.getLinemakersName()
            ));
        }
        return linemakerMatchInfoDTOS;
    }
}
