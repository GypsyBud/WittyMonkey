package com.wittymonkey.dao;

import com.wittymonkey.entity.Reserve;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface IReserveDao extends IGenericDao<Reserve, Serializable> {

    Reserve getReserveById(Integer reserveId);

    /**
     * 根据房间id获取指定状态下的预定信息
     *
     * @param roomId
     * @return
     */
    List<Reserve> getReserveByRoomId(Integer roomId, Integer status, Integer start, Integer pageSize);

    Reserve getReserveByDate(Integer roomId, Integer status, Date date);

    List<Reserve> getAllReservesByDate(Date date);

    Integer getTotalByRoomIdReserved(Integer roomId, Integer status);
}
