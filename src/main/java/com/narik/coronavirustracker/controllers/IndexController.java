package com.narik.coronavirustracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.narik.coronavirustracker.model.CoronavirusStatus;
import com.narik.coronavirustracker.services.CoronavirusDataService;

@Controller
public class IndexController {
	
	@Autowired
	CoronavirusDataService service ; 
	
	@GetMapping("/")
	public String index(Model model) {
		
		List<CoronavirusStatus> stats = service.getAllStats() ;
		
		int totalCases = stats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
		
		model.addAttribute("stats",stats);
		model.addAttribute("totalCases", totalCases);
		
		return "index" ; 
	}
}
