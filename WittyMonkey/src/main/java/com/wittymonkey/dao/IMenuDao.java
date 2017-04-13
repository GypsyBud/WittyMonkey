package com.wittymonkey.dao;

import java.io.Serializable;
import java.util.List;

import com.wittymonkey.entity.Menu;

public interface IMenuDao extends IGenericDao<Menu, Serializable> {

	/**
	 * 查找所有菜单
	 * @return
	 */
	List<Menu> getAll();

	Menu getMenuById(Integer id);

	List<Menu> getAllUnconfigurable();

	List<Menu> getAllConfigurable();

}
