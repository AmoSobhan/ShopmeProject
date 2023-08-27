package com.shopme.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainContoller {

	@GetMapping("")
	public String viewHomePage(){
		return "index";
	}
}
