package com.jtips.rummypoints.controller;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jtips.rummypoints.model.Admin;
import com.jtips.rummypoints.model.Player;
import com.jtips.rummypoints.model.Point;
import com.jtips.rummypoints.model.Round;
import com.jtips.rummypoints.model.SelectedPlayers;
import com.jtips.rummypoints.model.Table;
import com.jtips.rummypoints.repo.PlayerRepository;
import com.jtips.rummypoints.repo.TableRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class RummyPointsController {

	@Autowired
	private PlayerRepository playerRepo;

	@Autowired
	private TableRepository tableRepo;

	@PostMapping("/addPlayer")
	public String addPlayer(@ModelAttribute("player") Player player, Model model, HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		Admin admin = (Admin) session.getAttribute("admin");
		List<Player> players = playerRepo.findByAdmin(admin);
		if (players == null) {
			players = new ArrayList<>();
		}
		model.addAttribute("selectedPlayers", new SelectedPlayers());
		if (!players.contains(player)) {
			player.setAdmin(admin);
			player = playerRepo.save(player);
			players.add(player);
		} else {
			model.addAttribute("DUPLICATE_PLAYER", "Player already present");
		}
		model.addAttribute("players", players);
		return "AddPlayer";
	}

	@GetMapping("/home")
	public String home(Model model, HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		model.addAttribute("selectedPlayers", new SelectedPlayers());
		List<Player> players = playerRepo.findByAdmin((Admin) session.getAttribute("admin"));
		model.addAttribute("players", players);
		return "AddPlayer";
	}

	@PostMapping("/showTable")
	public String showTable(@RequestParam("tableId") int tableId, Model model) {
		Table table = tableRepo.findById(tableId).get();
		List<Player> selectedPlayersObj = table.getPlayers();
		List<String> selectedPlayerNames = selectedPlayersObj.stream().map(a -> a.getName())
				.collect(Collectors.toList());
		model.addAttribute("selectedPlayerNames", selectedPlayerNames);
		model.addAttribute("rounds", table.getRounds());
		model.addAttribute("table", table);

		return "PointsTable";
	}

	@PostMapping("/startTable")
	public String startTable(@ModelAttribute("selectedPlayers") SelectedPlayers selectedPlayers, Model model,
			HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		Admin admin = (Admin) session.getAttribute("admin");
		List<Integer> selectedPlayerIds = selectedPlayers.getSelected();
		List<String> selectedPlayerNames = selectedPlayerIds.stream().map(a -> getName(a)).collect(Collectors.toList());
		List<Player> selectedPlayersObj = selectedPlayerIds.stream().map(a -> {
			Player p = new Player();
			p.setId(a);
			p.setName(getName(a));
			return p;
		}).collect(Collectors.toList());
		List<Round> rounds = new ArrayList<>();
		Table table = new Table();
		table.setAdmin(admin);
		table.setPlayers(selectedPlayersObj);
		table.setRounds(rounds);
		table.setStartedDate(new Date());
		Table saved = tableRepo.save(table);
		model.addAttribute("selectedPlayerNames", selectedPlayerNames);
		model.addAttribute("rounds", rounds);
		model.addAttribute("table", saved);

		return "PointsTable";
	}

	@GetMapping("/backToTable")
	public String backToPointsTable(Model model, @RequestParam("tableId") int id) {
		Table t = tableRepo.findById(id).get();
		model.addAttribute("selectedPlayerNames",
				t.getPlayers().stream().map(a -> a.getName()).collect(Collectors.toList()));
		model.addAttribute("rounds", t.getRounds());
		model.addAttribute("table", t);
		return "PointsTable";
	}


	@PostMapping("/addPoints")
	public String addPoints(HttpServletRequest req, Model model) {
		Round r = new Round();
		int tableId = Integer.parseInt(req.getParameter("tableId"));
		Optional<Table> t = tableRepo.findById(tableId);
		Table tt = t.get();
		List<Point> points = new ArrayList<>();
		for (Player p : tt.getPlayers()) {
			int pnts = Integer.parseInt(req.getParameter(p.getName()));
			if (pnts == 0) {
				r.setWinner(p.getName());
			}
			Point point = new Point();
			point.setName(p.getName());
			point.setPoints(pnts);
			points.add(point);
		}
		r.setPoints(points);
		List<Round> rs = tt.getRounds();
		rs.add(r);
		tt.setRounds(rs);

		Table ttt = tableRepo.save(tt);

		model.addAttribute("rounds", ttt.getRounds());
		model.addAttribute("selectedPlayerNames",
				ttt.getPlayers().stream().map(a -> a.getName()).collect(Collectors.toList()));
		model.addAttribute("table", ttt);
		return "PointsTable";
	}

	private String getName(int id) {
		List<Player> players = (List<Player>) playerRepo.findAll();
		for (Player p : players) {
			if (p.getId() == id) {
				return p.getName();
			}
		}
		return null;
	}

	@GetMapping("/settle")
	public String settle(Model model, @RequestParam("tableId") int tableId) {
		Table table = tableRepo.findById(tableId).get();
		List<Round> rounds = table.getRounds();
		if (!rounds.isEmpty()) {
			model.addAttribute("result", settle(table));
			model.addAttribute("selectedPlayerNames",
					table.getPlayers().stream().map(a -> a.getName()).collect(Collectors.toList()));
			model.addAttribute("tableId", tableId);
			model.addAttribute("table", table);
			return "Result";
		} else {
			model.addAttribute("rounds", table.getRounds());
			model.addAttribute("tableId", tableId);
			model.addAttribute("selectedPlayerNames",
					table.getPlayers().stream().map(a -> a.getName()).collect(Collectors.toList()));
			model.addAttribute("table", table);
			model.addAttribute("GAME_NOT_STARTEED", "Game yet to start");
			return "PointsTable";
		}
	}

	public Map<String, Integer> settle(Table table) {
		final Map<String, Integer> totalGain = new LinkedHashMap<String, Integer>();
		final Map<String, Integer> totalLoss = new LinkedHashMap<String, Integer>();
		for (String p : table.getPlayers().stream().map(a -> a.getName()).collect(Collectors.toList())) {
			totalLoss.put(p, 0);
			totalGain.put(p, 0);
		}
		for (Round row : table.getRounds()) {
			int rowTotal = 0;
			List<Point> pointsMap = row.getPoints();
			for (final Point cell : pointsMap) {
				final int points = (int) cell.getPoints();
				final int cnt = totalLoss.get(cell.getName());
				totalLoss.put(cell.getName(), cnt + points);
				rowTotal += points;
			}
			final int cnt2 = totalGain.get(row.getWinner());
			totalGain.put(row.getWinner(), cnt2 + rowTotal);
		}
		final Set<String> keys = totalGain.keySet();
		Map<String, Integer> res = new LinkedHashMap<>();
		for (final String key : keys) {
			final int gain = totalGain.get(key);
			final int lose = totalLoss.get(key);
			final int out = gain - lose;
			if (res.containsKey(key)) {
				final int cu = res.get(key);
				res.put(key, out + cu);
			} else {
				res.put(key, out);
			}
		}
		return res;
	}

}
