package com.cmps312.studyplanner.model;

public class Subtask {
    String title;
    Boolean status;

    public Subtask() {

    }
    public Subtask(String title, Boolean status) {
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
