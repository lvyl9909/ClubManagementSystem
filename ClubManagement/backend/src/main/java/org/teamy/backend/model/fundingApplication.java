package org.teamy.backend.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class fundingApplication {
    private String description;
    private BigDecimal amount;
    private Integer semester;
    private Club club;
    private fundingApplicationStatus status;
    private List<Event> event;
    private Date date;
    private FacultyAdministrator reviewer;

    public fundingApplication(String description, BigDecimal amount, Integer semester, Club club, fundingApplicationStatus status, List<Event> event, Date date, FacultyAdministrator reviewer) {
        this.description = description;
        this.amount = amount;
        this.semester = semester;
        this.club = club;
        this.status = status;
        this.event = event;
        this.date = date;
        this.reviewer = reviewer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public fundingApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(fundingApplicationStatus status) {
        this.status = status;
    }

    public List<Event> getEvent() {
        return event;
    }

    public void setEvent(List<Event> event) {
        this.event = event;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FacultyAdministrator getReviewer() {
        return reviewer;
    }

    public void setReviewer(FacultyAdministrator reviewer) {
        this.reviewer = reviewer;
    }
}
