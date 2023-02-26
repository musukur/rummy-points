package com.jtips.rummypoints.controller;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jtips.rummypoints.model.Admin;
import com.jtips.rummypoints.model.Table;
import com.jtips.rummypoints.repo.AdminRepository;
import com.jtips.rummypoints.repo.TableRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

	@Autowired
	private AdminRepository repo;

	@Autowired
	private TableRepository tableRepo;

	@GetMapping("/login")
	public String endGame(Model model) {

		return "Login";
	}

	@PostMapping("/verifyLogin")
	public String authenticate(HttpServletRequest req, @ModelAttribute("admin") Admin admin, Model model) {
		Admin ad = repo.findById(admin.getUsername()).get();
		if (admin.getPassword().equals(ad.getPassword())) {
			HttpSession session = req.getSession(true);
			session.setAttribute("admin", admin);
			return welcome(model, admin);
		} else {
			model.addAttribute("INVALID_CREDENTIALS", "Invalid Credentials");
			return "Login";
		}
	}

	private String welcome(Model model, Admin admin) {
		List<Table> unfinishedTables = tableRepo.findByIsCompletedAndAdmin(false, admin);
		for (Table table : unfinishedTables) {
			Date d = table.getStartedDate();
			table.setDayName(table.getTableId() + " --> " + DayOfWeek.of(d.getDay() == 0 ? 7 : d.getDay()).name()
					+ " : " + d.getDate() + "/" + d.getMonth() + "/" + d.getYear() + " " + d.getHours() + ":"
					+ d.getMinutes() + ":" + d.getSeconds());
		}
		model.addAttribute("unfinishedTables", unfinishedTables);
		return "Welcome";
	}

	@GetMapping("/endGame")
	public String endGame(Model model, @RequestParam("tableId") int id, HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		Admin admin = (Admin) session.getAttribute("admin");
		Table t = tableRepo.findById(id).get();
		t.setCompleted(true);
		tableRepo.save(t);
		return welcome(model, admin);
	}
}
