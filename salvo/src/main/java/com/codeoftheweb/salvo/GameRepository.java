package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByCreationDate(LocalDateTime creationDate);
}