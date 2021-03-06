package group.cc.occ.model;

import java.util.Date;
import javax.persistence.*;

public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type;

    private String title;

    private String content;

    @Column(name = "startTime")
    private Date starttime;

    @Column(name = "endTime")
    private Date endtime;

    private Integer level;

    @Column(name = "scheduleId")
    private Integer scheduleid;

    @Column(name = "submitUserId")
    private Integer submituserid;

    @Column(name = "submitTime")
    private Date submittime;

    @Column(name = "state")
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return startTime
     */
    public Date getStarttime() {
        return starttime;
    }

    /**
     * @param starttime
     */
    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    /**
     * @return endTime
     */
    public Date getEndtime() {
        return endtime;
    }

    /**
     * @param endtime
     */
    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    /**
     * @return level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * @param level
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * @return scheduleId
     */
    public Integer getScheduleid() {
        return scheduleid;
    }

    /**
     * @param scheduleid
     */
    public void setScheduleid(Integer scheduleid) {
        this.scheduleid = scheduleid;
    }

    /**
     * @return submitUserId
     */
    public Integer getSubmituserid() {
        return submituserid;
    }

    /**
     * @param submituserid
     */
    public void setSubmituserid(Integer submituserid) {
        this.submituserid = submituserid;
    }

    /**
     * @return submitTime
     */
    public Date getSubmittime() {
        return submittime;
    }

    /**
     * @param submittime
     */
    public void setSubmittime(Date submittime) {
        this.submittime = submittime;
    }
}