package com.jtips.rummypoints.repo;

import org.springframework.data.repository.CrudRepository;

import com.jtips.rummypoints.model.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

}
