package com.wittymonkey.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Repository;

import com.wittymonkey.dao.IUserDao;
import com.wittymonkey.entity.User;

@Repository(value = "userDao")
public class UserDaoImpl extends GenericDaoImpl<User> implements IUserDao {

	@Override
	public User getUserByStaffNo(String staffNo) {
		String hql = "from User where staffNo = :staffNo and dimissionDate is null";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("staffNo", staffNo);
		List<User> users = queryListHQL(hql, param);
		if (users == null || users.isEmpty()) {
			return null;
		} else {
			return users.get(0);
		}
	}

	@Override
	public User getUserByStaffNoAndPassword(String staffNo, String password) {
		String hql = "from User where staffNo = :staffNo and password = :password and dimissionDate is null";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("staffNo", staffNo);
		param.put("password", password);
		return queryOneHql(hql,param);
	}

	@Override
	public User getUserByEmailAndPassword(String email, String password) {
		String hql = "from User where email = :email and password = :password and dimissionDate is null";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("password", password);
		return queryOneHql(hql,param);
	}

	@Override
	public void saveUser(User user) {
		save(user);
		getCurrentSession().flush();
	}

	@Override
	public User getUserByEmail(String email) {
		String hql = "from User where email = :email and dimissionDate is null";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email.toLowerCase());
		return queryOneHql(hql,param);
	}

	@Override
	public List<User> getUserByPage(Integer hotel, Integer start, Integer total) {
		String hql = "from User where hotel.id = :hotelId and dimissionDate is null order by id";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("hotelId", hotel);
		return queryListHQL(hql,param,start,total);
	}

	@Override
	public Integer getTotalByHotel(Integer hotel) {
		String hql = "select count(1) from User where hotel.id = :hotelId and dimissionDate is null";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("hotelId", hotel);
		return countHQL(hql,param);
	}
	String nextStaffNo;
	@Override
	public String getNextStaffNoByHotel(final Integer hotelId) {
		nextStaffNo = null;
		getCurrentSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				CallableStatement call = connection.prepareCall("{call get_next_staff_id(?)}");
				call.setInt(1,hotelId);
				ResultSet resultSet = call.executeQuery();
				if (resultSet != null){
					while (resultSet.next()){
						UserDaoImpl.this.nextStaffNo = resultSet.getString(1);
					}
				}
			}
		});
		return nextStaffNo;
	}

	@Override
	public User getUserById(Integer id) {
		String hql = "from User where id = ?";
		return queryOneHql(hql, id);
	}
}
