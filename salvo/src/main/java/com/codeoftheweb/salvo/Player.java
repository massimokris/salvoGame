package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Score> scores;

    public void addGamePlayer(GamePlayer gamePlayer){

        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public Player() { }

    public Player(String user, String password) {

        this.userName = user;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setFirstName(String userName) {
        this.userName = userName;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<Score> getScores() {
        return scores;
    }


    public Score getGameScore (Game game){

        return scores.stream().filter(score -> score.getGame().getId() == game.getId()).findAny().orElse(null);
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public void addScore (Score score){

        score.setPlayer(this);
        scores.add(score);
    }


    public Map<String, Object> playerDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("email", this.getUserName());
        return dto;
    }

    public Map<String, Object> playerStatisticsDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("email", this.getUserName());
        double total = this.getScores().stream().mapToDouble(Score::getScore).sum();
        double won = this.getScores().stream().filter(score -> score.getScore() == 3).count();
        double lost = this.getScores().stream().filter(score -> score.getScore() == 0).count();
        double tied = this.getScores().stream().filter(score -> score.getScore() == 1).count();
        dto.put("score", total);
        dto.put("won", won);
        dto.put("lost", lost);
        dto.put("tied", tied);
        return dto;
    }
}

