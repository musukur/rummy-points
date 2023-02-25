package com.jtips.rummypoints.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
@jakarta.persistence.Table(name = "RummyTable")
public class Table {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int tableId;
	@ManyToMany
	private List<Player> players;
	@OneToMany(cascade = CascadeType.ALL)
	private List<Round> rounds;

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public void setRounds(List<Round> rounds) {
		this.rounds = rounds;
	}

}
