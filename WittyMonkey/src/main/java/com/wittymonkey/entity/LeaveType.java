package com.wittymonkey.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 请假类型
 * @author neilw
 *
 */
@Entity
@Table(name="leave_type")
public class LeaveType implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(length=20)
	private String name;
	
	@ManyToOne(targetEntity=Hotel.class, cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name="hotel_id", referencedColumnName="id")
	private Hotel hotel;
	
	// 扣薪额度（按一天工资的百分比）
	@Column
	private Double deduct;
	
	@Column(length=1024)
	private String note;

	@Column
	private Boolean deletable;

	@Column(name="entry_datetime")
	private Date entryDatetime;

	@ManyToOne(targetEntity=User.class, fetch=FetchType.EAGER, cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name="entry_id", referencedColumnName="id")
	private User entryUser;

	public Date getEntryDatetime() {
		return entryDatetime;
	}

	public void setEntryDatetime(Date entryDatetime) {
		this.entryDatetime = entryDatetime;
	}

	public User getEntryUser() {
		return entryUser;
	}

	public void setEntryUser(User entryUser) {
		this.entryUser = entryUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Double getDeduct() {
		return deduct;
	}

	public void setDeduct(Double deduct) {
		this.deduct = deduct;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getDeletable() {
		return deletable;
	}

	public void setDeletable(Boolean deletable) {
		this.deletable = deletable;
	}
}
