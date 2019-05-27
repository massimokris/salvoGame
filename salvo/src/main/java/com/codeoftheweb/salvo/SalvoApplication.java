package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository) {
		return (args) -> {
			// save a couple of customers
			Player playerOne = new Player("Jack");
			Player playerTwo = new Player("Chloe");
			Player playerThree = new Player("Kim");
			Player playerFour = new Player("David");
			Player playerFive = new Player("Michelle");
			Player playerSix = new Player("Massimo");

			playerRepository.save(playerOne);
			playerRepository.save(playerTwo);
			playerRepository.save(playerThree);
			playerRepository.save(playerFour);
			playerRepository.save(playerFive);
			playerRepository.save(playerSix);

			Game gameOne = new Game(LocalDateTime.now());
			Game gameTwo = new Game(LocalDateTime.now().plusHours(1));
			Game gameThree = new Game(LocalDateTime.now().plusHours(2));

			gameRepository.save(gameOne);
			gameRepository.save(gameTwo);
			gameRepository.save(gameThree);

			GamePlayer gamePlayerOne = new GamePlayer(playerOne, gameOne, LocalDateTime.now());
			GamePlayer gamePlayerTwo = new GamePlayer(playerTwo, gameOne, LocalDateTime.now());
			GamePlayer gamePlayerThree = new GamePlayer(playerThree, gameTwo, LocalDateTime.now());
			GamePlayer gamePlayerFour = new GamePlayer(playerFive, gameThree, LocalDateTime.now());
			GamePlayer gamePlayerFive = new GamePlayer(playerFour, gameTwo, LocalDateTime.now());
			GamePlayer gamePlayerSix = new GamePlayer(playerSix, gameThree, LocalDateTime.now());

			Ship shipOne = new Ship(ShipType.CARRIER, new ArrayList<>(Arrays.asList("a10","b10","c10","d10","e10")));
			Ship shipTwo = new Ship(ShipType.PATROL_BOAT, new ArrayList<>(Arrays.asList("j1","j2")));
			Ship shipThree = new Ship(ShipType.BATTLESHIP, new ArrayList<>(Arrays.asList("b3","c3","d3","e3")));
			Ship shipFour = new Ship(ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("f1","f2","f3")));
			Ship shipFive = new Ship(ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("f3","g3","h3")));
			Ship shipSix = new Ship(ShipType.DESTROYER, new ArrayList<>(Arrays.asList("g7","g8","g9")));

			Salvo salvoOne = new Salvo(1, new ArrayList<>(Arrays.asList("a10","b5")));
			Salvo salvoTwo = new Salvo(2, new ArrayList<>(Arrays.asList("b5","c3")));
			Salvo salvoThree = new Salvo(3, new ArrayList<>(Arrays.asList("h3","h4")));
			Salvo salvoFour = new Salvo(4, new ArrayList<>(Arrays.asList("e3","d8")));
			Salvo salvoFive = new Salvo(5, new ArrayList<>(Arrays.asList("a3","h10")));
			Salvo salvoSix = new Salvo(6, new ArrayList<>(Arrays.asList("b1","g4")));

			gamePlayerOne.addShip(shipOne);
			gamePlayerTwo.addShip(shipTwo);
			gamePlayerThree.addShip(shipThree);
			gamePlayerFour.addShip(shipFour);
			gamePlayerFive.addShip(shipFive);
			gamePlayerSix.addShip(shipSix);

			gamePlayerOne.addSalvo(salvoOne);
			gamePlayerTwo.addSalvo(salvoTwo);
			gamePlayerThree.addSalvo(salvoThree);
			gamePlayerFour.addSalvo(salvoFour);
			gamePlayerFive.addSalvo(salvoFive);
			gamePlayerSix.addSalvo(salvoSix);

			gamePlayerRepository.save(gamePlayerOne);
			gamePlayerRepository.save(gamePlayerTwo);
			gamePlayerRepository.save(gamePlayerThree);
			gamePlayerRepository.save(gamePlayerFour);
			gamePlayerRepository.save(gamePlayerFive);
			gamePlayerRepository.save(gamePlayerSix);
		};
	}
}
