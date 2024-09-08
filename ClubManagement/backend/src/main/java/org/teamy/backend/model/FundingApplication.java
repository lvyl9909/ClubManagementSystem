package org.teamy.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.SimpleTimeZone;

public class FundingApplication {
    private Integer id;
    private String description;
    private BigDecimal amount;
    private Integer semester;
    private Integer clubId;

    private Club club;
    private fundingApplicationStatus status;
//    private List<Event> event;
    private String date;
    private FacultyAdministrator reviewer;
    private Integer reviewerId;


    public FundingApplication(Integer id,String description, BigDecimal amount, Integer semester, Integer clubId, fundingApplicationStatus status, Date date, Integer reviewerId) {
        this.id=id;
        this.description = description;
        this.amount = amount;
        this.semester = semester;
        this.clubId = clubId;
        this.status = status;
        this.date = date.toString();
        this.reviewerId = reviewerId;
    }

    public FundingApplication(String description, BigDecimal amount, Integer semester, Club club, fundingApplicationStatus status, Date date) {
        this.description = description;
        this.amount = amount;
        this.semester = semester;
        this.club = club;
        this.status = status;
        this.date = date.toString();
        this.reviewer = null;
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


    public String getDate() {
        return date;
    }
    public java.sql.Date getSqlDate() {
        return java.sql.Date.valueOf(LocalDate.parse(date));
    }


    public void setDate(String date) {
        this.date = date;
    }

    public FacultyAdministrator getReviewer() {
        return reviewer;
    }

    public void setReviewer(FacultyAdministrator reviewer) {
        this.reviewer = reviewer;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    public Integer getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Integer reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
