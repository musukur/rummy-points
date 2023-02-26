package com.jtips.rummypoints.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jtips.rummypoints.model.Admin;
import com.jtips.rummypoints.model.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

	List<Player> findByAdmin(Admin admin);

}
