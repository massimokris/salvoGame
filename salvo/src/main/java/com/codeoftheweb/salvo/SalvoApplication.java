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
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
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

			Ship shipOne = new Ship(ShipType.UNO, new ArrayList<>(Arrays.asList("h1","h2")));
			Ship shipTwo = new Ship(ShipType.DOS, new ArrayList<>(Arrays.asList("g1","g2")));
			Ship shipThree = new Ship(ShipType.TRES, new ArrayList<>(Arrays.asList("b1","b2")));
			Ship shipFour = new Ship(ShipType.TRES, new ArrayList<>(Arrays.asList("u1","u2")));
			Ship shipFive = new Ship(ShipType.UNO, new ArrayList<>(Arrays.asList("f1","f2")));
			Ship shipSix = new Ship(ShipType.DOS, new ArrayList<>(Arrays.asList("p1","p2")));

			gamePlayerOne.addShip(shipOne);
			gamePlayerTwo.addShip(shipTwo);
			gamePlayerThree.addShip(shipThree);
			gamePlayerFour.addShip(shipFour);
			gamePlayerFive.addShip(shipFive);
			gamePlayerSix.addShip(shipSix);

			gamePlayerRepository.save(gamePlayerOne);
			gamePlayerRepository.save(gamePlayerTwo);
			gamePlayerRepository.save(gamePlayerThree);
			gamePlayerRepository.save(gamePlayerFour);
			gamePlayerRepository.save(gamePlayerFive);
			gamePlayerRepository.save(gamePlayerSix);
		};
	}
}
