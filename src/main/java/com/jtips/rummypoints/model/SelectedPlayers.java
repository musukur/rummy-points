package com.jtips.rummypoints.model;

import java.util.ArrayList;
import java.util.List;

public class SelectedPlayers {
	private List<Integer> selected;

	public SelectedPlayers() {
		selected = new ArrayList<>();
	}

	public List<Integer> getSelected() {
		return selected;
	}

	public void setSelected(List<Integer> selected) {
		this.selected = selected;
	}

}
