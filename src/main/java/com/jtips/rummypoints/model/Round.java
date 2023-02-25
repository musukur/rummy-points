package com.jtips.rummypoints.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@jakarta.persistence.Table(name = "rounds")
public class Round {
	private String winner;
	@ElementCollection
	@Embedded
	private List<Point> points;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int roundNum;

	public Round() {
		points = new ArrayList<>();
	}

	public int getRoundNum() {
		return roundNum;
	}

	public void setRoundNum(int roundNum) {
		this.roundNum = roundNum;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

}
