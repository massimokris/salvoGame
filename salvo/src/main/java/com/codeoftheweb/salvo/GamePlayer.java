package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Salvo> salvos = new HashSet<>();

    public void addShip(Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo){
        salvo.setGamePlayer(this);
        salvos.add(salvo);
    }

    public GamePlayer() {}

    public GamePlayer(Player player, Game game, LocalDateTime LocalDateTime){

        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getJoinDate() { return joinDate; }

    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }

    public Score getScoreGamePlayer (){
        return this.player.getGameScore(this.game);
    }

    public Map<String, Object> gamePlayerDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gamePlayerId", this.id);
        dto.put("joinDate", this.joinDate);
        dto.put("player", this.player.playerDTO());
        if(this.getScoreGamePlayer() != null){
            dto.put("scores", this.getScoreGamePlayer().getScore());
        }else{

            dto.put("scores", this.getScoreGamePlayer());
        }

        return dto;
    }

    public Map<String, Object> gameViewDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gameId", this.getGame().getId());
        dto.put("creationDate", this.getGame().getCreationDate());
        dto.put("gamePlayers", this.getGame().getGamePlayers().stream().map(GamePlayer::gamePlayerDTO));
        dto.put("Ships", this.ships.stream().map(Ship::shipDTO));
        dto.put("Salvos", this.game.getGamePlayers().stream().flatMap(sgp -> sgp.getSalvos().stream().map(Salvo::salvoDTO)));
        return dto;
    }

     public String getGameState(){
        String gameState = null;
        Optional<GamePlayer> opponentGamePlayer = this.getGame().getGamePlayers().stream().filter(gp -> gp.getId() != this.getId()).findFirst();

        if(!opponentGamePlayer.isPresent()){
            gameState = "Wait opponent";
        }else{
            if(this.getShips().isEmpty()) {
                gameState = "Place ships";
            }else if (opponentGamePlayer.get().getShips().isEmpty()) {
                gameState = "Wait opponent ships";
            }else {
                int myTurn = this.getSalvos().stream().mapToInt(Salvo::getTurn).max().orElse(0);
                int opponentTurn = opponentGamePlayer.get().getSalvos().stream().mapToInt(Salvo::getTurn).max().orElse(0);

                if (this.getId() < opponentGamePlayer.get().getId() && myTurn == opponentTurn) {
                    gameState = "Place salvos";
                } else if (this.getId() < opponentGamePlayer.get().getId() && myTurn > opponentTurn) {
                    gameState = "Wait opponent salvos";
                } else if (this.getId() > opponentGamePlayer.get().getId() && myTurn < opponentTurn) {
                    gameState = "Place slavos";
                } else if (this.getId() > opponentGamePlayer.get().getId() && myTurn == opponentTurn) {
                    gameState = "Wait opponent salvos";
                }

                Salvo mySalvo = this.getSalvos().stream().filter(turn -> turn.getTurn() == myTurn).findFirst().orElse(null);
                Salvo opponentSalvo = opponentGamePlayer.get().getSalvos().stream().filter(turn -> turn.getTurn() == opponentTurn).findFirst().orElse(null);

                if (myTurn == opponentTurn && mySalvo.getSinks().size() == 5 && mySalvo.getSinks().size() > opponentSalvo.getSinks().size()) {
                    gameState = "Win";
                } else if (myTurn == opponentTurn && opponentSalvo.getSinks().size() == 5 && opponentSalvo.getSinks().size() > mySalvo.getSinks().size()) {
                    gameState = "Lose";
                } else if (myTurn == opponentTurn && mySalvo.getSinks().size() == 5 && opponentSalvo.getSinks().size() == 5){
                    gameState = "Draw";
                }
            }
        }

        return gameState;
    }
}
