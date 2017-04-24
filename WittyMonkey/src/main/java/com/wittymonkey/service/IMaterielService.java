package com.wittymonkey.service;

import com.wittymonkey.entity.Materiel;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IMaterielService {
    Integer getTotal(Map<Integer, Object> condition);

    List<Materiel> getMaterielByPage(Map<Integer, Object> condition, Integer start, Integer total);

    Materiel getMaterielByBarcode(Integer hotelId, String barcode);

    void saveMateriel(Materiel materiel);

    Materiel getMaterielById(Integer id);

    void deleteMateriel(Materiel materiel) throws SQLException;
}
