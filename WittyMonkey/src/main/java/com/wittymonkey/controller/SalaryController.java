package com.wittymonkey.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wittymonkey.entity.*;
import com.wittymonkey.service.*;
import com.wittymonkey.util.ChangeToSimple;
import com.wittymonkey.util.DateUtil;
import com.wittymonkey.util.NumberUtil;
import com.wittymonkey.vo.Constraint;
import com.wittymonkey.vo.SalaryPayroll;
import com.wittymonkey.vo.SalaryVO;
import com.wittymonkey.vo.SimpleSalaryRecord;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by neilw on 2017/4/20.
 */
@Controller
public class SalaryController {

    @Autowired
    private ISalaryService salaryService;

    @Autowired
    private ISalaryRecordService salaryRecordService;

    @Autowired
    private ISalaryHistoryService salaryHistoryService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ILeaveDetailService leaveDetailService;

    @RequestMapping(value = "getSalaryByPage", method = RequestMethod.GET)
    @ResponseBody
    public String getSalaryByPage(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Integer curr = Integer.parseInt(request.getParameter("curr"));
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        Integer pageSize = loginUser.getSetting().getPageSize();
        Hotel hotel = (Hotel) request.getSession().getAttribute("hotel");
        Map<Integer, Object> param = new HashMap<Integer, Object>();
        param.put(Constraint.SALARY_SEARCH_CONDITION_HOTEL_ID, hotel.getId());
        Integer type = Integer.parseInt(request.getParameter("type"));
        switch (type) {
            case 2:
                String name = request.getParameter("name");
                if (StringUtils.isNotBlank(name)) {
                    param.put(Constraint.SALARY_SEARCH_CONDITION_STAFF_NAME, name);
                }
                break;
            case 3:
                String staffNo = request.getParameter("staffNo");
                if (StringUtils.isNotBlank(staffNo)) {
                    param.put(Constraint.SALARY_SEARCH_CONDITION_STAFF_NO, staffNo);
                }
        }
        Integer count = salaryService.getTotal(param);
        List<Salary> salaries = salaryService.getSalaryByPage(param, (curr - 1) * pageSize, pageSize);
        List<SalaryVO> salaryVOS = ChangeToSimple.convertSalariesByTime(salaries, new Date());
        json.put("count", count);
        json.put("pageSize", pageSize);
        JSONArray array = new JSONArray();
        array.addAll(salaryVOS);
        json.put("data", array);
        return json.toJSONString();
    }

    @RequestMapping(value = "toSalaryChangeRecord", method = RequestMethod.GET)
    public String toSalaryChangeRecord(HttpServletRequest request) {
        Integer id = Integer.parseInt(request.getParameter("id"));
        Salary salary = salaryService.getSalaryById(id);
        request.getSession().setAttribute("salary", salary);
        return "salary_record";
    }

    @RequestMapping(value = "getSalaryRecordByPage", method = RequestMethod.GET)
    @ResponseBody
    public String getSalaryRecordByPage(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Salary salary = (Salary) request.getSession().getAttribute("salary");
        Integer curr = Integer.parseInt(request.getParameter("curr"));
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        Integer pageSize = loginUser.getSetting().getPageSize();
        Hotel hotel = (Hotel) request.getSession().getAttribute("hotel");
        Integer count = salaryRecordService.getTotal(salary.getId());
        List<SalaryRecord> salaryRecords = salaryRecordService.getSalaryRecordByPage(salary.getId(), (curr - 1) * pageSize, pageSize);
        List<SimpleSalaryRecord> simpleSalaryRecords = ChangeToSimple.salaryRecordList(salaryRecords);
        json.put("count", count);
        json.put("pageSize", pageSize);
        JSONArray array = new JSONArray();
        array.addAll(simpleSalaryRecords);
        json.put("data", array);
        return json.toJSONString();
    }

    @RequestMapping(value = "toSalaryAdd", method = RequestMethod.GET)
    public String toSalaryAdd(HttpServletRequest request) {
        request.setAttribute("id", Integer.parseInt(request.getParameter("id")));
        return "salary_add";
    }

    /**
     * 保存工资变动
     *
     * @param request
     * @return <table border="1" cellspacing="0">
     * <tr>
     * <th>代码</th>
     * <th>说明</th>
     * </tr>
     * <tr>
     * <td>400</td>
     * <td>金额不正确</td>
     * </tr>
     * <tr>
     * <td>410</td>
     * <td>时间不正确</td>
     * </tr>
     * <tr>
     * <td>411</td>
     * <td>时间冲突</td>
     * </tr>
     * <tr>
     * <td>420</td>
     * <td>备注过长</td>
     * </tr>
     * <tr>
     * <tr>500</tr>
     * <td>服务器错误</td>
     * </tr>
     * <tr>
     * <td>200</td>
     * <td>保存成功</td>
     * </tr>
     * </table>
     */
    @RequestMapping(value = "saveSalaryChange", method = POST)
    @ResponseBody
    public String saveSalaryChange(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        Integer id = Integer.parseInt(request.getParameter("id"));
        Double money = null;
        try {
            money = Double.parseDouble(request.getParameter("salary"));
        } catch (NumberFormatException e) {
            json.put("status", 400);
            return json.toJSONString();
        }
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            date = dateFormat.parse(request.getParameter("startDate"));
        } catch (ParseException e) {
            json.put("status", 410);
            return json.toJSONString();
        }
        if (money <= 0) {
            json.put("status", 410);
            return json.toJSONString();
        }
        String note = request.getParameter("note");
        if (StringUtils.isNotBlank(note) && note.length() > 1024) {
            json.put("status", 420);
            return json.toJSONString();
        }
        SalaryRecord salaryRecord = salaryRecordService.getSalaryRecordByStartDate(id, date);
        if (salaryRecord != null) {
            json.put("status", 411);
            return json.toJSONString();
        }
        Salary salary = (Salary) request.getSession().getAttribute("salary");
        if (salary == null) {
            json.put("status", 500);
            return json.toJSONString();
        }
        SalaryRecord record = new SalaryRecord();
        record.setEntryDatetime(new Date());
        record.setEntryUser(userService.getUserById(loginUser.getId()));
        record.setMoney(money);
        record.setNote(note);
        record.setSalary(salaryService.getSalaryById(salary.getId()));
        record.setStartDate(date);
        salaryRecordService.save(record);
        json.put("status", 200);
        return json.toJSONString();
    }

    /**
     * 删除工资记录
     *
     * @param request
     * @return <table border="1" cellspacing="0">
     * <tr>
     * <th>代码</th>
     * <th>说明</th>
     * </tr>
     * <tr>
     * <td>400</td>
     * <td>记录不存在</td>
     * </tr>
     * <tr>
     * <td>200</td>
     * <td>删除成功</td>
     * </tr>
     * </table>
     */
    @RequestMapping(value = "deleteSalaryRecord", method = POST)
    @ResponseBody
    public String deleteSalaryRecord(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Integer id = null;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            json.put("status", 400);
            return json.toJSONString();
        }
        SalaryRecord salaryRecord = salaryRecordService.getSalaryRecordById(id);
        if (salaryRecord == null) {
            json.put("status", 400);
            return json.toJSONString();
        }
        try {
            salaryRecordService.delete(salaryRecord);
        } catch (SQLException e) {
            json.put("status", 400);
            return json.toJSONString();
        }
        json.put("status", 200);
        return json.toJSONString();
    }

    @RequestMapping(value = "toEditSalary", method = RequestMethod.GET)
    public String toEditSalary(HttpServletRequest request) {
        Integer salaryId = Integer.parseInt(request.getParameter("salaryId"));
        Integer id = Integer.parseInt(request.getParameter("recordId"));
        SalaryRecord salaryRecord = salaryRecordService.getSalaryRecordById(id);
        request.getSession().setAttribute("salary", salaryRecord);
        request.setAttribute("salaryId", salaryId);
        return "salary_edit";
    }

    @RequestMapping(value = "updateSalary", method = POST)
    @ResponseBody
    public String updateSalary(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Integer salaryId = Integer.parseInt(request.getParameter("salaryId"));
        Integer recordId = Integer.parseInt(request.getParameter("id"));
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        Double money = null;
        try {
            money = Double.parseDouble(request.getParameter("salary"));
        } catch (NumberFormatException e) {
            json.put("status", 400);
            return json.toJSONString();
        }
        Date startDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            startDate = dateFormat.parse(request.getParameter("startDate"));
        } catch (ParseException e) {
            json.put("status", 410);
            return json.toJSONString();
        }
        String note = request.getParameter("note");
        if (StringUtils.isNotBlank(note) && note.length() > 1024) {
            json.put("status", 420);
            return json.toJSONString();
        }
        SalaryRecord record = salaryRecordService.getSalaryRecordById(recordId);
        if (record == null) {
            json.put("status", 403);
            return json.toJSONString();
        }
        // 查重（开始时间）
        SalaryRecord queryRecord = salaryRecordService.getSalaryRecordByStartDate(salaryId, startDate);
        if (queryRecord != null && !queryRecord.getId().equals(recordId)) {
            json.put("status", 411);
            return json.toJSONString();
        }
        record.setMoney(money);
        record.setStartDate(startDate);
        record.setNote(note);
        record.setEntryUser(userService.getUserById(loginUser.getId()));
        record.setEntryDatetime(new Date());
        salaryRecordService.save(record);
        json.put("status", 200);
        return json.toJSONString();
    }

    @RequestMapping(value = "toSalaryHistory", method = GET)
    public String toSalaryHistory(HttpServletRequest request) {
        request.setAttribute("id", request.getParameter("id"));
        return "salary_history";
    }

    /**
     * 获取工资历史
     *
     * @param request
     * @return JSONArray
     */
    @RequestMapping(value = "getSalaryHistory", method = GET)
    @ResponseBody
    public String getSalaryHistory(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Integer salaryId = Integer.parseInt(request.getParameter("id"));
        List<SalaryHistory> salaryHistories = salaryHistoryService.getSalaryHistoryBySalaryId(salaryId);
        JSONArray time = new JSONArray();
        JSONArray salary = new JSONArray();
        JSONArray tips = new JSONArray();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        for (SalaryHistory salaryHistory : salaryHistories) {
            time.add(dateFormat.format(salaryHistory.getSalaryDate()));
            salary.add(salaryHistory.getAmount());
            JSONObject tipContent = new JSONObject();
            tipContent.put("total", salaryHistory.getTotal());
            tipContent.put("leave", salaryHistory.getLeavePay());
            tipContent.put("other", salaryHistory.getOtherPay());
            tipContent.put("bonus", salaryHistory.getBonus());
            tipContent.put("date", dateFormat.format(salaryHistory.getSalaryDate()));
            tips.add(tipContent);
        }
        json.put("time", time);
        json.put("salary", salary);
        json.put("tips", tips);
        return json.toJSONString();
    }

    @RequestMapping(value = "toSalaryPayroll", method = GET)
    public String toSalaryPayroll(HttpServletRequest request) {
        return "salary_payroll";
    }

    @RequestMapping(value = "getLastMonthSalary", method = GET)
    @ResponseBody
    public String getLastMonthSalary(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Hotel hotel = (Hotel) request.getSession().getAttribute("hotel");
        List<User> users = userService.getUserByPage(hotel.getId(), Constraint.USER_INCUMBENT, null, null);
        Date lastMonth = DateUtil.lastMonthFirstDate(new Date());
        List<SalaryPayroll> salaryPayrolls = new ArrayList<SalaryPayroll>();
        for (User user : users) {
            SalaryHistory history = salaryHistoryService.getSalaryHistoryByUserIdAndSalaryDate(user.getId(), lastMonth);
            if (history == null) {
                Salary salary = salaryService.getSalaryByStaffId(user.getId());
                SalaryVO salaryVO = ChangeToSimple.convertSalaryByTime(salary, lastMonth);
                if (salaryVO != null && salaryVO.getMoney() != null) {
                    SalaryPayroll salaryPayroll = new SalaryPayroll();
                    salaryPayroll.setStaffId(user.getId());
                    salaryPayroll.setStaffNo(user.getStaffNo());
                    salaryPayroll.setStaffName(user.getRealName());
                    salaryPayroll.setBasic(salaryVO.getMoney());
                    salaryPayroll.setSalaryDate(lastMonth);
                    List<LeaveDetail> leaveDetails = leaveDetailService.getLeaveDetailByUserAndMonth(user.getId(), lastMonth);
                    Double leavePay = 0.0;
                    for (LeaveDetail detail : leaveDetails) {
                        leavePay += detail.getDeduct();
                    }
                    salaryPayroll.setLeave(NumberUtil.round(leavePay, 2));
                    salaryPayrolls.add(salaryPayroll);
                }
            }
        }
        request.getSession().setAttribute("payroll", salaryPayrolls);
        json.put("data", salaryPayrolls);
        json.put("date", lastMonth);
        return json.toJSONString();
    }

    /**
     * 发放工资
     *
     * @param request
     * @return <table border="1" cellspacing="0">
     * <tr>
     * <th>代码</th>
     * <th>说明</th>
     * </tr>
     * <tr>
     * <td>400</td>
     * <td>其它扣薪有错</td>
     * </tr>
     * <tr>
     * <td>401</td>
     * <td>奖金有错</td>
     * </tr>
     * <tr>
     * <td>200</td>
     * <td>发放完成</td>
     * </tr>
     */
    @RequestMapping(value = "batchPayroll", method = POST)
    @ResponseBody
    public String batchPayroll(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        User user = (User) request.getSession().getAttribute("loginUser");
        List<SalaryPayroll> salaryPayrolls = (List<SalaryPayroll>) request.getSession().getAttribute("payroll");
        for (SalaryPayroll payroll : salaryPayrolls) {
            Double otherPay = null;
            Double bonusPay = null;
            try {
                String other = request.getParameter("other_" + payroll.getStaffId());
                if (StringUtils.isBlank(other)) {
                    otherPay = 0.0;
                } else {
                    otherPay = Double.parseDouble(other);
                }
            } catch (NumberFormatException e) {
                json.put("status", 400);
                json.put("staffId", payroll.getStaffId());
                return json.toJSONString();
            }
            try {
                String bonus = request.getParameter("bonus_" + payroll.getStaffId());
                if (StringUtils.isBlank(bonus)) {
                    bonusPay = 0.0;
                } else {
                    bonusPay = Double.parseDouble(bonus);
                }
            } catch (NumberFormatException e) {
                json.put("status", 401);
                json.put("staffId", payroll.getStaffId());
                return json.toJSONString();
            }
            payroll.setOther(otherPay);
            payroll.setBonus(bonusPay);
        }
        List<SalaryHistory> histories = assymblyByPayRoll(salaryPayrolls, user);
        salaryHistoryService.batchSave(histories);
        Double total = 0.0;
        for (SalaryHistory history : histories) {
            total += history.getAmount();
        }
        json.put("status", 200);
        json.put("total", total);
        return json.toJSONString();
    }

    public List<SalaryHistory> assymblyByPayRoll(List<SalaryPayroll> payrolls, User entryUser) {
        List<SalaryHistory> histories = new ArrayList<SalaryHistory>();
        Date now = new Date();
        for (SalaryPayroll payroll : payrolls) {
            SalaryHistory history = new SalaryHistory();
            history.setEntryUser(userService.getUserById(entryUser.getId()));
            history.setEntryDatetime(now);
            history.setBonus(payroll.getBonus());
            history.setLeavePay(payroll.getLeave());
            history.setOtherPay(payroll.getOther());
            history.setSalary(salaryService.getSalaryByStaffId(payroll.getStaffId()));
            history.setSalaryDate(payroll.getSalaryDate());
            history.setTotal(payroll.getBasic());
            Double amount = payroll.getBasic() - payroll.getLeave() - payroll.getOther() + payroll.getBonus();
            if (amount < 0) {
                amount = 0.0;
            }
            history.setAmount(amount);
            histories.add(history);
        }
        return histories;
    }

    @RequestMapping(value = "toMySalary", method = GET)
    public String toMySalary(HttpServletRequest request){
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        Salary salary = salaryService.getSalaryByStaffId(loginUser.getId());
        request.getSession().setAttribute("salary", salary);
        return "my_salary";
    }
}
