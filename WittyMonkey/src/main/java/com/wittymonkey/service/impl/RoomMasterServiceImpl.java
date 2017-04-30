package com.wittymonkey.service.impl;

import com.wittymonkey.controller.RoomController;
import com.wittymonkey.dao.IReserveDao;
import com.wittymonkey.dao.IRoomMasterDao;
import com.wittymonkey.entity.Hotel;
import com.wittymonkey.entity.Reserve;
import com.wittymonkey.entity.RoomMaster;
import com.wittymonkey.service.IRoomMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service(value = "roomMasterService")
public class RoomMasterServiceImpl implements IRoomMasterService {

    @Autowired
    private IRoomMasterDao roomMasterDao;

    @Autowired
    private IReserveDao reserveDao;

    @Override
    public RoomMaster getRoomById(Integer id) {
        return roomMasterDao.getRoomById(id);
    }

    @Override
    public RoomMaster getRoomMasterByNo(Hotel hotel, String roomNo) {
        return roomMasterDao.getRoomMasterByNo(hotel.getId(), roomNo);
    }

    @Override
    public void saveRoom(RoomMaster roomMaster) {
        roomMasterDao.save(roomMaster);
    }

    @Override
    public void updateRoom(RoomMaster roomMaster) throws SQLException {
        roomMasterDao.update(roomMaster);
    }

    @Override
    public void deleteRoom(RoomMaster roomMaster) throws SQLException {
        roomMasterDao.delete(roomMaster);
    }

    @Override
    public List<RoomMaster> getRoomByHotel(Integer hotelId, Integer start, Integer total) {
        return roomMasterDao.getRoomByHotel(hotelId, start, total);
    }

    @Override
    public Integer getTotalByHotel(Integer hotelId) {
        return roomMasterDao.getTotalByHotel(hotelId);
    }

    @Override
    public Integer getTotalByCondition(Integer type, Object content) {
        return roomMasterDao.getTotalByCondition(type, content);
    }

    @Override
    public List<RoomMaster> getRoomByCondition(Integer hotelId, Integer type, Object content) {
        return roomMasterDao.getRoomByCondition(hotelId, type, content, null, null);
    }

    @Override
    public List<RoomMaster> getFreeByDate(Integer hotel, Integer status, Date from, Date to, Integer first, Integer total) {
        if (first == null || total == null) {
            return getAllFreeByDate(hotel, status, from, to);
        } else {
            List<RoomMaster> rooms = getAllFreeByDate(hotel, status, from, to);
            return rooms.subList(first, (first + total) > rooms.size() ? rooms.size() : first + total);
        }
    }

    @Override
    public Integer getTotalFreeByDate(Integer hotel, Integer status, Date from, Date to) {
        return getAllFreeByDate(hotel, status, from, to).size();
    }


    private List<RoomMaster> getAllFreeByDate(Integer hotel, Integer status, Date from, Date to) {
        // 获取所有空闲和已预定的房间
        List<RoomMaster> rooms = roomMasterDao.getFreeAndReservedByDate(hotel, status, from, to);
        Iterator<RoomMaster> iterator = rooms.iterator();
        // 移除其中已预定的房间
        while (iterator.hasNext()) {
            RoomMaster room = iterator.next();
            List<Reserve> reserves = reserveDao.getReserveByRoomId(room.getId(), Reserve.RESERVED, null, null);
            for (int i = 0; i < reserves.size(); i++) {
                Reserve reserve = reserves.get(i);
                /**
                 * 以下为时间冲突的判断逻辑：
                 * 已预定时间：         \________________\
                 * 要预定时间：  \________\ \_______\ \______\
                 */
                if (!((from.before(reserve.getEstCheckinDate()) && to.before(reserve.getEstCheckinDate()))
                        || from.after(reserve.getEstCheckoutDate()) && to.after(reserve.getEstCheckoutDate()))) {
                    iterator.remove();
                    break;
                }
            }
        }
        return rooms;
    }

    @Override
    public List<RoomMaster> getRoomByCondition(Integer hotelId, Integer type, Object content, Integer first, Integer total) {
        if (type == RoomController.TYPE_STATUS && Integer.parseInt(content.toString()) == RoomMaster.RESERVED) {
            List<Reserve> reserves = reserveDao.getAllReservesByDate(new Date());
            Set<RoomMaster> rooms = new HashSet<RoomMaster>();
            if (reserves != null && reserves.size() > 0) {
                for (Reserve reserve : reserves) {
                    rooms.add(reserve.getRoom());
                }
            }
            return new ArrayList<RoomMaster>(rooms);
        } else if (type == RoomController.TYPE_STATUS && Integer.parseInt(content.toString()) == RoomMaster.FREE) {
            List<RoomMaster> room = roomMasterDao.getRoomByCondition(hotelId, type, content, first, total);
            List<Reserve> reserves = reserveDao.getAllReservesByDate(new Date());
            List<RoomMaster> except = new ArrayList<RoomMaster>();
            if (reserves != null && reserves.size() > 0) {
                for (Reserve reserve : reserves) {
                    except.add(reserve.getRoom());
                }
            }
            room.removeAll(except);
            return room;
        } else {
            return roomMasterDao.getRoomByCondition(hotelId, type, content, first, total);
        }
    }
}
