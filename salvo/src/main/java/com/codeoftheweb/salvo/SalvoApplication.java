package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.Month;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
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

			gamePlayerRepository.save(new GamePlayer(playerOne, gameOne));
			gamePlayerRepository.save(new GamePlayer(playerTwo, gameOne));
			gamePlayerRepository.save(new GamePlayer(playerThree, gameTwo));
			gamePlayerRepository.save(new GamePlayer(playerFive, gameThree));
			gamePlayerRepository.save(new GamePlayer(playerFour, gameTwo));
			gamePlayerRepository.save(new GamePlayer(playerSix, gameThree));
		};
	}
}
