package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ScoreRepository scoreRpository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

   /* @RequestMapping("/players")
    public List<Player> getPlayers(){

        return playerRepository.findAll();
    }*/

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication){

        Map<String,Object> dto = new HashMap<>();

        if(isGuest(authentication)){

            dto.put("player", null);
        }else{

            dto.put("player", playerRepository.findByUserName(authentication.getName()).playerDTO());
        }

        dto.put("games", gameRepository
                .findAll() //games
                .stream()
                .map(Game::gameDTO)
                .collect(Collectors.toList()));
        dto.put("leaderboard", playerRepository
                .findAll() //games
                .stream()
                .map(Player::playerStatisticsDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long gamePlayerId, Authentication authentication) {
        GamePlayer  gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        Player player = playerRepository.findByUserName(authentication.getName());
        ResponseEntity message;
        if(gamePlayer.getPlayer().getId() == player.getId() ){
            message = new ResponseEntity<>(gamePlayer.gameViewDTO(), HttpStatus.OK);
        }else{
            message = new ResponseEntity<>(map("unauthorized", "this not is your game"), HttpStatus.UNAUTHORIZED);
        }

        return message;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {

        ResponseEntity responseEntity;

        if (username.isEmpty() || password.isEmpty()) {

             responseEntity = new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }else if (playerRepository.findByUserName(username) !=  null) {

            responseEntity = new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }else{

            playerRepository.save(new Player(username, passwordEncoder.encode(password)));
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        }


        return responseEntity;
    }

    private Map<String, Object> map(String key, Object value){

        Map<String, Object> map = new HashMap<>();
        map.put(key, value);

        return map;
    }

   /* @RequestMapping("/gamePlayers")
    public List<GamePlayer> getGamePlayers(){

        return gamePlayerRepository.findAll();
    }*/

}

