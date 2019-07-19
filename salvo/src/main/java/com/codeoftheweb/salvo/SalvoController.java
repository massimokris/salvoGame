package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
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

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGames(Authentication authentication){

        ResponseEntity responseEntity;

        if(isGuest(authentication)){

            responseEntity = new ResponseEntity<>("Miss player", HttpStatus.FORBIDDEN);
        }else{

            Player player = playerRepository.findByUserName((authentication.getName()));
            Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(player, newGame, LocalDateTime.now()));
            responseEntity = new ResponseEntity<>(map("gamePlayerId", newGamePlayer.getId()), HttpStatus.CREATED);
        }

        return responseEntity;
    }

    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication authentication){

        ResponseEntity responseEntity;
        Player player = playerRepository.findByUserName((authentication.getName()));
        Game game = gameRepository.findById(gameId);
        GamePlayer enemy = game.getGamePlayers().stream().findFirst().orElse(null);

        if(isGuest(authentication)){

            responseEntity = new ResponseEntity<>("Miss player", HttpStatus.FORBIDDEN);
        }else if(game == null) {

            responseEntity = new ResponseEntity<>("Game doesn't exist", HttpStatus.FORBIDDEN);
        }else if(player.getUserName() == enemy.getPlayer().getUserName()) {

            responseEntity = new ResponseEntity<>("same player", HttpStatus.FORBIDDEN);
        }else if(game.gamePlayers.size() > 1){

            responseEntity = new ResponseEntity<>("a lot players", HttpStatus.FORBIDDEN);
        }else{

            //Player player = playerRepository.findByUserName((authentication.getName()));
            //Game game = gameRepository.findById(gameId);
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(player, game, LocalDateTime.now()));
            responseEntity = new ResponseEntity<>(map("gamePlayerId", newGamePlayer.getId()), HttpStatus.CREATED);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {

        ResponseEntity<Map<String, Object>> responseEntity;
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if(isGuest(authentication)){

            responseEntity = new ResponseEntity<>(map("Error", "miss player"), HttpStatus.UNAUTHORIZED);
        }else{

            if(gamePlayer == null){

                responseEntity = new ResponseEntity<>(map("error", "miss gameplayer"), HttpStatus.NOT_FOUND);
            }else{

                if(player.getId() != gamePlayer.getPlayer().getId()){

                    responseEntity = new ResponseEntity<>(map("error", "different player"), HttpStatus.NOT_FOUND);
                }else{

                    if(gamePlayer.getShips().size() > 0){

                        responseEntity = new ResponseEntity<>(map("error", "placed ships"), HttpStatus.NOT_FOUND);
                    }else{

                        if(ships.size() != 5){

                            responseEntity = new ResponseEntity<>(map("error", "more/less ships"), HttpStatus.FORBIDDEN);
                        }else{

                            ships.stream().forEach(gamePlayer::addShip);
                            gamePlayerRepository.save(gamePlayer);
                            responseEntity = new ResponseEntity<>(map("success", "ships placed"), HttpStatus.CREATED);
                        }
                    }
                }
            }
        }
        return responseEntity;
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalos(@PathVariable Long gamePlayerId, @RequestBody List<String> shots, Authentication authentication) {

        ResponseEntity<Map<String, Object>> responseEntity;
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if(isGuest(authentication)){

            responseEntity = new ResponseEntity<>(map("Error", "miss player"), HttpStatus.UNAUTHORIZED);
        }else{

            if(gamePlayer == null){

                responseEntity = new ResponseEntity<>(map("error", "miss gameplayer"), HttpStatus.NOT_FOUND);
            }else{

                if(player.getId() != gamePlayer.getPlayer().getId()){

                    responseEntity = new ResponseEntity<>(map("error", "different player"), HttpStatus.NOT_FOUND);
                }else{

                    if(shots.size() != 5){

                        responseEntity = new ResponseEntity<>(map("error", "a lot salvos"), HttpStatus.NOT_FOUND);
                    }else{

                        int turn = gamePlayer.getSalvos().size() + 1;
                        Salvo salvo = new Salvo(turn, shots);
                        gamePlayer.addSalvo(salvo);
                        gamePlayerRepository.save(gamePlayer);



                        responseEntity = new ResponseEntity<>(map("success", "salvos"), HttpStatus.CREATED);
                    }
                }
            }
        }
        return responseEntity;
    }

    private Map<String, Object> map(String key, Object value){

        Map<String, Object> map = new HashMap<>();
        map.put(key, value);

        return map;
    }
}

