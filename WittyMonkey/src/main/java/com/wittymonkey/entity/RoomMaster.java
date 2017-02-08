package com.wittymonkey.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/**
 * 房间基本信息
 * @author Neil
 *
 */
@Entity
@Table(name="room_master")
public class RoomMaster implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne(targetEntity=Floor.class)
	@JoinColumn(name="floor_id", referencedColumnName="id")
	private Floor floor;
	
	@Column
	private Double area;
	
	@Column
	private Double price;
	
	// 单人床数量
	@Column(name="singe_bed_num")
	private Integer singleBedNum;
	
	// 双人床数量
	@Column(name="double_bed_num")
	private Integer doubleBedNum;
	
	// 可入住人数
	@Column(name="available_num")
	private Integer availableNum;

	@OneToOne(targetEntity=RoomExt.class, mappedBy="roomMaster")
	private RoomExt roomExt;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getSingleBedNum() {
		return singleBedNum;
	}

	public void setSingleBedNum(Integer singleBedNum) {
		this.singleBedNum = singleBedNum;
	}

	public Integer getDoubleBedNum() {
		return doubleBedNum;
	}

	public void setDoubleBedNum(Integer doubleBedNum) {
		this.doubleBedNum = doubleBedNum;
	}

	public Integer getAvailableNum() {
		return availableNum;
	}

	public void setAvailableNum(Integer availableNum) {
		this.availableNum = availableNum;
	}
	
	
}
