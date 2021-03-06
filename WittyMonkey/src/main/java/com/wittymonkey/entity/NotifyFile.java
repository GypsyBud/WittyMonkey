package com.wittymonkey.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 通知中包含的文件
 *
 * @author neilw
 */
@Entity
@Table(name = "notify_file")
public class NotifyFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(targetEntity = Notify.class)
    @JoinColumn(name = "notify_id", referencedColumnName = "id")
    private Notify notify;

    @Column
    private String url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
