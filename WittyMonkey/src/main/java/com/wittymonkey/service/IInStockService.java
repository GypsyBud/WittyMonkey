package com.wittymonkey.service;

import com.wittymonkey.entity.InStock;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IInStockService {

    Integer getTotal(Map<Integer, Object> param);

    List<InStock> getInStocks(Map<Integer, Object> param, Integer start, Integer total);

    void save(InStock inStock);

    List<InStock> getInStockByDateRange(Integer hotelId, Date start, Date to);
}
