package com.wittymonkey.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wittymonkey.entity.Area;
import com.wittymonkey.entity.City;
import com.wittymonkey.entity.Hotel;
import com.wittymonkey.entity.LeaveType;
import com.wittymonkey.entity.MaterielType;
import com.wittymonkey.entity.Province;
import com.wittymonkey.entity.Role;
import com.wittymonkey.entity.Setting;
import com.wittymonkey.entity.User;
import com.wittymonkey.service.IAreaService;
import com.wittymonkey.service.ICityService;
import com.wittymonkey.service.IHotelService;
import com.wittymonkey.service.ILeaveTypeService;
import com.wittymonkey.service.IMaterielTypeService;
import com.wittymonkey.service.IMenuService;
import com.wittymonkey.service.IProvinceService;
import com.wittymonkey.service.IRoleService;
import com.wittymonkey.service.ISettingService;
import com.wittymonkey.service.IUserService;
import com.wittymonkey.util.SendEmail;
import com.wittymonkey.util.ValidateCodeServlet;

@Controller
public class LoginController {

	@Autowired
	private IHotelService hotelService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISettingService settingService;

	@Autowired
	private IMenuService menuService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IMaterielTypeService materielTypeService;

	@Autowired
	private ILeaveTypeService leaveTypeService;

	@Autowired
	private IProvinceService provinceService;

	@Autowired
	private ICityService cityService;

	@Autowired
	private IAreaService areaService;

	@RequestMapping(value = "toLogin", method = RequestMethod.GET)
	public String toLogin(HttpServletRequest request) {

		return "login";
	}

	@RequestMapping(value="toRegist", method=RequestMethod.GET)
	public String toRegist(HttpServletRequest request){
		return toRegistHotel(request);
	}
	
	@RequestMapping(value = "toRegistHotel", method = RequestMethod.GET)
	public String toRegistHotel(HttpServletRequest request) {
		List<Province> provinces = provinceService.getAll();
		String provinceCode = (String) request.getSession().getAttribute("registHotelProvinceCode");
		String cityCode = (String) request.getSession().getAttribute("registHotelCityCode");
		List<City> cities;
		List<Area> areas;
		if (provinceCode != null) {
			cities = cityService.getAllByProvince(Integer.parseInt(provinceCode));
		} else {
			cities = cityService.getAllByProvince(provinces.get(0));
		}
		if (cityCode != null) {
			areas = areaService.getAllByCity(Integer.parseInt(cityCode));
		} else {
			areas = areaService.getAllByCity(cities.get(0));
		}
		request.getSession().setAttribute("provinces", provinces);
		request.getSession().setAttribute("cities", cities);
		request.getSession().setAttribute("areas", areas);
		return "regist_hotel";
	}
	
	@RequestMapping(value = "toRegistUser", method = RequestMethod.POST)
	public String toRegistUser(HttpServletRequest request) {
		String hotelName = request.getParameter("hotelName");
		String legalName = request.getParameter("legalName");
		String legalIdCard = request.getParameter("legalIdCard");
		String licenseNo = request.getParameter("licenseNo");
		String provinceCode = request.getParameter("provinceCode");
		String cityCode = request.getParameter("cityCode");
		String areaCode = request.getParameter("areaCode");
		String placeDetail = request.getParameter("placeDetail");
		Hotel hotel = new Hotel();
		hotel.setName(hotelName);
		hotel.setLegalName(legalName);
		hotel.setLegalIdCard(legalIdCard);
		hotel.setLicenseNo(licenseNo);
		if (areaCode == null) {
			if (cityCode == null) {
				hotel.setPlaceCode(Integer.parseInt(provinceCode));
			} else {
				hotel.setPlaceCode(Integer.parseInt(cityCode));
			}
		} else {
			hotel.setPlaceCode(Integer.parseInt(areaCode));
		}
		hotel.setPlaceDetail(placeDetail);
		request.getSession().setAttribute("registHotel", hotel);
		request.getSession().setAttribute("registHotelProvinceCode", provinceCode);
		request.getSession().setAttribute("registHotelCityCode", cityCode);
		request.getSession().setAttribute("registHotelAreaCode", areaCode);
		return "regist_user";
	}

	/**
	 * 
	 * @param request
	 * @return JsonString: {"status", code}
	 *         <table border="1" cellspacing="0">
	 *         <tr>
	 *         <th>代码</th>
	 *         <th>说明</th>
	 *         </tr>
	 *         <tr>
	 *         <td>200</td>
	 *         <td>登陆成功</td>
	 *         </tr>
	 *         <tr>
	 *         <td>400</td>
	 *         <td>没有填写用户名</td>
	 *         </tr>
	 *         <tr>
	 *         <td>410</td>
	 *         <td>没有填写密码</td>
	 *         </tr>
	 *         <tr>
	 *         <td>420</td>
	 *         <td>没有填写验证码</td>
	 *         </tr>
	 *         <tr>
	 *         <td>421</td>
	 *         <td>验证码不正确</td>
	 *         </tr>
	 *         <tr>
	 *         <td>430</td>
	 *         <td>用户名或密码不正确</td>
	 *         </tr>
	 *         </table>
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public String login(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String loginName = request.getParameter("loginName");
		String password = request.getParameter("password");
		String code = request.getParameter("code");
		if (loginName == null || loginName.equals("")) {
			json.put("status", 400);
			return json.toJSONString();
		} else if (password == null || password.equals("")) {
			json.put("status", 410);
			return json.toJSONString();
		} else if (code == null || code.trim().equals("")) {
			json.put("status", 420);
			return json.toJSONString();
		} else if (!ValidateCodeServlet.validate(request, code)) {
			json.put("status", 421);
			return json.toJSONString();
		} else {
			User user = new User();
			user.setLoginName(loginName);
			user.setPassword(password);
			if (!userService.validateLogin(user)) {
				json.put("status", 430);
			} else {
				User loginUser = userService.getUserByLoginName(loginName);
				request.getSession().setAttribute("loginUser", loginUser);
				json.put("status", 200);
				json.put("url", "index.do");
			}
		}
		return json.toJSONString();
	}

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(HttpServletRequest request) {
		return "index";
	}

	@RequestMapping(value = "getValidateCode", method = RequestMethod.GET)
	@ResponseBody
	public String getValidateCode(HttpServletRequest request) {
		String email = request.getParameter("email");
		String code = SendEmail.sendValidateCode(email);
		request.getSession().setAttribute("registCode", code);
		JSONObject json = new JSONObject();
		json.put("status", "success");
		return json.toJSONString();
	}

	@RequestMapping(value = "validateEmailCode", method = RequestMethod.GET)
	@ResponseBody
	public String validateEmailCode(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String myCode = request.getParameter("code");
		json.put("status", validateInpEmailCode(request));
		return json.toJSONString();

	}

	@RequestMapping(value = "validatePicCode", method = RequestMethod.GET)
	@ResponseBody
	public String validatePicCode(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String myCode = request.getParameter("code");
		json.put("status", validateInpPicCode(request, myCode));
		return json.toJSONString();
	}

	/**
	 * 注册
	 * 
	 * @param request
	 * @return JsonString: {"status", code}
	 *         <table border="1" cellspacing="0">
	 *         <tr>
	 *         <th>代码</th>
	 *         <th>说明</th>
	 *         </tr>
	 *         <tr>
	 *         <td>200</td>
	 *         <td>注册成功</td>
	 *         </tr>
	 *         <tr>
	 *         <td>400</td>
	 *         <td>没有填写用户名</td>
	 *         </tr>
	 *         <tr>
	 *         <td>401</td>
	 *         <td>用户名已存在</td>
	 *         </tr>
	 *         <tr>
	 *         <td>410</td>
	 *         <td>密码小于6位</td>
	 *         </tr>
	 *         <tr>
	 *         <td>411</td>
	 *         <td>两次密码不一致</td>
	 *         </tr>
	 *         <tr>
	 *         <td>420</td>
	 *         <td>邮箱格式不正确</td>
	 *         </tr>
	 *         <tr>
	 *         <td>430</td>
	 *         <td>没有获取验证码</td>
	 *         </tr>
	 *         <tr>
	 *         <td>431</td>
	 *         <td>验证码错误</td>
	 *         </tr>
	 *         </table>
	 */
	@RequestMapping(value = "regist.do", method = RequestMethod.POST)
	@ResponseBody
	public String regist(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String loginName = request.getParameter("loginName");
		String password = request.getParameter("password");
		String repassword = request.getParameter("repassword");
		String email = request.getParameter("email");
		String myCode = request.getParameter("code");
		if (loginName == null || loginName.trim().equals("")) {
			json.put("status", 400);
			return json.toJSONString();
		} else if (isLoginNameExist(loginName)) {
			json.put("status", 401);
			return json.toJSONString();
		} else if (password == null || password.trim().equals("") || password.length() < 6) {
			json.put("status", 410);
			return json.toJSONString();
		} else if (!password.equals(repassword)) {
			json.put("status", 411);
			return json.toJSONString();
		} else if (!validateEmail(email)) {
			json.put("status", 420);
			return json.toJSONString();
		} else if (validateInpEmailCode(request) == 400) {
			json.put("status", 430);
			return json.toJSONString();
		} else if (validateInpEmailCode(request) == 401) {
			json.put("status", 431);
			return json.toJSONString();
		} else {
			registToDatabase(loginName, password, email);
			json.put("status", 200);
			return json.toJSONString();
		}
	}

	/**
	 * 数据库初始化
	 * 
	 * @param loginName
	 * @param password
	 * @param email
	 */
	private void registToDatabase(String loginName, String password, String email) {
		User system = userService.getUserById(0);
		Date now = new Date();
		// 添加酒店
		Hotel hotel = new Hotel();
		hotel.setAddDate(now);
		hotel.setEntryDatetime(now);
		hotel.setEntryUser(system);
		hotel.setIsClose(false);

		// 添加用户设置
		Setting setting = new Setting();
		setting.setLang("zh_CN");
		settingService.saveSetting(setting);

		// 添加用户
		User user = new User();
		user.setLoginName(loginName);
		user.setPassword(password);
		user.setEmail(email);
		user.setHotel(hotel);
		user.setRegistDate(now);
		user.setEntryDatetime(now);
		user.setEntryUser(system);
		user.setSetting(setting);
		userService.saveUser(user);

		// 添加初始化请假类型
		// 事假
		LeaveType affair = new LeaveType();
		affair.setDeduct(0.0);
		affair.setName("affair");
		affair.setEntryDatetime(now);
		affair.setEntryUser(system);
		affair.setHotel(hotel);
		// 年假
		LeaveType year = new LeaveType();
		year.setDeduct(0.0);
		year.setName("year");
		year.setEntryDatetime(now);
		year.setEntryUser(system);
		year.setHotel(hotel);
		// 婚假
		LeaveType marry = new LeaveType();
		marry.setDeduct(0.0);
		marry.setName("marry");
		marry.setEntryDatetime(now);
		marry.setEntryUser(system);
		marry.setHotel(hotel);
		// 丧假
		LeaveType funeral = new LeaveType();
		funeral.setDeduct(0.0);
		funeral.setName("funeral");
		funeral.setEntryDatetime(now);
		funeral.setEntryUser(system);
		funeral.setHotel(hotel);
		// 病假
		LeaveType sick = new LeaveType();
		sick.setDeduct(0.0);
		sick.setName("sick");
		sick.setEntryDatetime(now);
		sick.setEntryUser(system);
		sick.setHotel(hotel);
		// 产假
		LeaveType maternity = new LeaveType();
		maternity.setDeduct(0.0);
		maternity.setName("maternity");
		maternity.setEntryDatetime(now);
		maternity.setEntryUser(system);
		maternity.setHotel(hotel);

		List<LeaveType> leaveTypes = new ArrayList<LeaveType>();
		leaveTypes.add(affair);
		leaveTypes.add(year);
		leaveTypes.add(marry);
		leaveTypes.add(funeral);
		leaveTypes.add(sick);
		leaveTypes.add(maternity);
		leaveTypeService.saveList(leaveTypes);

		// 添加默认物料类型
		MaterielType materielType = new MaterielType();
		materielType.setName("Other");
		materielType.setHotel(hotel);
		materielTypeService.saveMaterielType(materielType);

		// 添加角色
		Role role = new Role();
		role.setHotel(hotel);
		role.setEntryDatetime(now);
		role.setName("Admin(经理)");
		role.setEntryUser(system);
		role.setMenus(menuService.getAll());
		role.getUsers().add(user);
		roleService.saveRole(role);
	}

	/**
	 * 验证用户名是否存在
	 * 
	 * @param request
	 * @return {"status": code}
	 *         <table border="1" cellspacing="0">
	 *         <tr>
	 *         <th>代码</th>
	 *         <th>说明</th>
	 *         </tr>
	 *         <tr>
	 *         <td>200</td>
	 *         <td>用户存在</td>
	 *         </tr>
	 *         <tr>
	 *         <td>201</td>
	 *         <td>用户不存在</td>
	 *         </tr>
	 *         </table>
	 */
	@RequestMapping(value = "validateLoginName", method = RequestMethod.GET)
	@ResponseBody
	public String validateLoginName(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String loginName = request.getParameter("loginName");
		if (isLoginNameExist(loginName)) {
			json.put("status", 200);
		} else {
			json.put("status", 201);
		}
		return json.toJSONString();
	}

	/**
	 * 用户名是否存在
	 * 
	 * @param loginName
	 * @return
	 *         <table border="1" cellspacing="0">
	 *         <tr>
	 *         <th>代码</th>
	 *         <th>说明</th>
	 *         </tr>
	 *         <tr>
	 *         <td>true</td>
	 *         <td>用户存在</td>
	 *         </tr>
	 *         <tr>
	 *         <td>false</td>
	 *         <td>用户不存在</td>
	 *         </tr>
	 *         </table>
	 */
	public boolean isLoginNameExist(String loginName) {
		if (userService.getUserByLoginName(loginName) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证邮箱格式是否正确
	 * 
	 * @param email
	 * @return
	 *         <table border="1" cellspacing="0">
	 *         <tr>
	 *         <th>代码</th>
	 *         <th>说明</th>
	 *         </tr>
	 *         <tr>
	 *         <td>true</td>
	 *         <td>邮箱格式正确</td>
	 *         </tr>
	 *         <tr>
	 *         <td>false</td>
	 *         <td>邮箱格式不正确</td>
	 *         </tr>
	 *         </table>
	 */
	public boolean validateEmail(String email) {
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		return regex.matcher(email).matches();
	}

	/**
	 * 验证 邮件验证码
	 * 
	 * @param request
	 * @return
	 *         <table border="1" cellspacing="0">
	 *         <tr>
	 *         <th>代码</th>
	 *         <th>说明</th>
	 *         </tr>
	 *         <tr>
	 *         <td>200</td>
	 *         <td>验证通过</td>
	 *         </tr>
	 *         <tr>
	 *         <td>400</td>
	 *         <td>没有获取验证码</td>
	 *         </tr>
	 *         <tr>
	 *         <td>401</td>
	 *         <td>验证码错误</td>
	 *         </tr>
	 *         </table>
	 */
	public int validateInpEmailCode(HttpServletRequest request) {
		String realCode = (String) request.getSession().getAttribute("registCode");
		String myCode = request.getParameter("code");
		if (realCode == null) {
			return 400;
		} else if (myCode.equals(realCode)) {
			return 200;
		} else {
			return 401;
		}
	}

	/**
	 * 验证 图片验证码
	 * 
	 * @param request
	 * @return
	 *         <table border="1" cellspacing="0">
	 *         <tr>
	 *         <th>代码</th>
	 *         <th>说明</th>
	 *         </tr>
	 *         <tr>
	 *         <td>200</td>
	 *         <td>验证通过</td>
	 *         </tr>
	 *         <tr>
	 *         <td>400</td>
	 *         <td>没有输入验证码</td>
	 *         </tr>
	 *         <tr>
	 *         <td>401</td>
	 *         <td>验证码错误</td>
	 *         </tr>
	 *         </table>
	 */
	public int validateInpPicCode(HttpServletRequest request, String myCode) {
		String realCode = (String) request.getSession().getAttribute(ValidateCodeServlet.VALIDATE_CODE);
		if (myCode == null) {
			return 400;
		} else if (myCode.equals(realCode)) {
			return 200;
		} else {
			return 401;
		}
	}

}
