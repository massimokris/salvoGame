package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    List<String> locations;

    public Salvo() {}

    public Salvo(int turn, List<String> locations){

        this.turn = turn;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    private GamePlayer getEnemy(){
        return this.getGamePlayer().getGame().getGamePlayers()
                .stream().filter(gamePlayer -> gamePlayer.getId() != this.getGamePlayer().getId()).findFirst().orElse(null);
    }

    private List<String> getHits(){
        List<String> hits = new ArrayList<>();
        if(this.getEnemy() != null){
            hits = this.getLocations().stream().filter(location -> this.getEnemy().getShips().stream().anyMatch(ship -> ship.getLocations().contains(location)))
                    .collect(Collectors.toList());
        }
        return hits;
    }

    public  List<Map<String, Object>> getSinks(){
        List<String> allLocations=new ArrayList<>();
        List<Map<String, Object>> sinks = new ArrayList<>();
        this.getGamePlayer().getSalvos().stream().filter(salvo -> salvo.getTurn() <= this.getTurn()).forEach(salvo -> allLocations.addAll(salvo.getLocations()));
        if(this.getEnemy() != null) {
            sinks = this.getEnemy().getShips().stream().filter(ship -> allLocations.containsAll(ship.getLocations())).map(Ship::shipDTO).collect(Collectors.toList());
        }
        return  sinks;
    }

    public Map<String, Object> salvoDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("salvoId", this.id);
        dto.put("turn", this.turn);
        dto.put("playerId", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.locations);
        dto.put("hits", this.getHits());
        dto.put("sinks", this.getSinks());
        return dto;
    }
}

