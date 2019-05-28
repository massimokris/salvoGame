package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    public Map<String, Object> gamePlayerDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gamePlayerId", this.id);
        dto.put("joinDate", this.joinDate);
        dto.put("player", this.player.playerDTO());

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
}
