package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Autowired
	PasswordEncoder passwordEncoder;
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers

			Player playerOne = new Player("Jack", passwordEncoder.encode("1005map"));
			Player playerTwo = new Player("Chloe", passwordEncoder.encode("map*"));
			Player playerThree = new Player("Kim", passwordEncoder.encode("1005"));
			Player playerFour = new Player("David", passwordEncoder.encode("map"));
			Player playerFive = new Player("Massimo", passwordEncoder.encode("13167894"));
			Player playerSix = new Player("Massimo", passwordEncoder.encode("massimo"));

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

			Score scoreOne = new Score(playerOne, gameOne, 3, LocalDateTime.now());
			Score scoreTwo = new Score(playerOne, gameOne, 3, LocalDateTime.now());
			Score scoreThree = new Score(playerThree, gameOne, 1, LocalDateTime.now());
			Score scoreFour = new Score(playerFour, gameOne, 3, LocalDateTime.now());
			Score scoreFive = new Score(playerFive, gameOne, 1, LocalDateTime.now());
			Score scoreSix = new Score(playerSix, gameOne, 0, LocalDateTime.now());

			scoreRepository.save(scoreOne);
			scoreRepository.save(scoreTwo);
			scoreRepository.save(scoreThree);
			scoreRepository.save(scoreFour);
			scoreRepository.save(scoreFive);
			scoreRepository.save(scoreSix);

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

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}


	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userName-> {
			Player player = playerRepository.findByUserName(userName);
			if (player != null) {
				if (player.getUserName().equals("massimo")) {
					return new User(player.getUserName(), player.getPassword(),
							AuthorityUtils.createAuthorityList("ADMIN"));
				}else {
					return new User(player.getUserName(), player.getPassword(),
							AuthorityUtils.createAuthorityList("USER"));
				}
			} else {
				throw new UsernameNotFoundException("Unknown user: " + userName);
			}
		}).passwordEncoder(passwordEncoder());
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		//evita autorizacion para css js etc
		String[] resources = new String[]{

				"/include/**", "/css/**", "/img/**", "/js/**", "/layer/**", "/icons/**"
		};

		http.authorizeRequests()
				.antMatchers(resources).permitAll()
                .antMatchers("/rest/**").hasAnyAuthority("ADMIN")
				.antMatchers("/web/games.html").permitAll()
				.antMatchers("/web/game.html").hasAnyAuthority("USER", "ADMIN")
				.antMatchers("/api/game_view/**").hasAnyAuthority("USER", "ADMIN");
		http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login").permitAll();

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}

	}
}