package com.wittymonkey.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wittymonkey.entity.Hotel;
import com.wittymonkey.service.IHotelService;
import com.wittymonkey.util.IDCardValidate;
import com.wittymonkey.util.ValidateCodeServlet;
import com.wittymonkey.vo.IDCardInfo;

@Controller
public class LoginController {

	@Autowired
	private IHotelService hotelService;
	@RequestMapping(name="toLogin", method = RequestMethod.GET)
	public String hotel(HttpServletRequest request){

		return "login";
	}
	
	@RequestMapping(value="index", method=RequestMethod.POST)
	public String index(HttpServletRequest request){
		//int userId = Integer.parseInt(request.getParameter("username"));
		//String password = request.getParameter("password");
		return "index";
	}
	
	@RequestMapping(value="test", method=RequestMethod.GET)
	public void test(HttpServletRequest request){
		String id = request.getParameter("id");
		IDCardInfo info = IDCardValidate.getIDCardInfo(request.getSession().getServletContext(), id);
		request.getSession().setAttribute("card", info);
		request.getSession().setAttribute("id", id);
	}
}
