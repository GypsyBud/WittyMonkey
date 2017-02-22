package com.wittymonkey.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * 预定
 * @author neilw
 *
 */
@Entity
@Table
public class Reserve implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@ManyToOne(targetEntity = Customer.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;


	@ManyToOne(targetEntity=RoomMaster.class)
	@JoinColumn(name="room_id", referencedColumnName="id")
	private RoomMaster room;

	// 预定时间
	@Column(name="reserve_date")
	private Date reserveDate;

	// 预计入住时间
	@Column(name="est_checkin_date")
	private Date estCheckinDate;

	//预计退房时间
	@Column(name="est_checkout_date")
	private Date estCheckoutDate;

	//定金
	@Column
	private Double deposit;

	// 状态（待入住/已入住/已取消）
	@Column
	private Integer status;

	@Column(name="entry_datetime")
	private Date entryDatetime;

	@ManyToOne(targetEntity=User.class, fetch=FetchType.EAGER)
	@JoinColumn(name="entry_id", referencedColumnName="id")
	private User entryUser;

	@Column(length=1024)
	private String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public RoomMaster getRoom() {
        return room;
    }

    public void setRoom(RoomMaster room) {
        this.room = room;
    }

    public Date getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(Date reserveDate) {
        this.reserveDate = reserveDate;
    }

    public Date getEstCheckinDate() {
        return estCheckinDate;
    }

    public void setEstCheckinDate(Date estCheckinDate) {
        this.estCheckinDate = estCheckinDate;
    }

    public Date getEstCheckoutDate() {
        return estCheckoutDate;
    }

    public void setEstCheckoutDate(Date estCheckoutDate) {
        this.estCheckoutDate = estCheckoutDate;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
