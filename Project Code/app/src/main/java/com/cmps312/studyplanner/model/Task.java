package com.cmps312.studyplanner.model;

import java.util.ArrayList;

public class Task  {
    private String id;
    private String title;
    private String priority;
    private String date;
    private String time;
    private Boolean status;
    ArrayList<Subtask> subTasks;
    private long timeStamp;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task(){}
    public Task(String title, String priority, String date,
                String time, Boolean status, ArrayList<Subtask> subTasks) {
        this.title = title;
        this.priority = priority;
        this.date = date;
        this.time = time;
        this.status = status;
        this.subTasks = subTasks;
        timeStamp = System.currentTimeMillis();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ArrayList<Subtask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<Subtask> subTasks) {
        this.subTasks = subTasks;
    }


}
