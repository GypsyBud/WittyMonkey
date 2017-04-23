package com.wittymonkey.dao;

import java.io.Serializable;
import java.util.List;

import com.wittymonkey.entity.Materiel;

public interface IMaterielDao extends IGenericDao<Materiel, Serializable>{
    List<Materiel> getMaterielByType(Integer typeId);
}
