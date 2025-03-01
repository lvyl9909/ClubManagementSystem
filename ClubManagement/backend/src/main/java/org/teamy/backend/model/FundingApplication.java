package org.teamy.backend.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.SimpleTimeZone;

public class FundingApplication extends DomainObject {
    private String description;
    private BigDecimal amount;
    private Integer semester;
    private Integer clubId;

    private Club club;
    private fundingApplicationStatus status;

    private String date;
    private FacultyAdministrator reviewer;
    private Integer reviewerId;

    private Integer version;

    public FundingApplication(Integer id,String description, BigDecimal amount, Integer semester, Integer clubId, fundingApplicationStatus status, Date date, Integer reviewerId,Integer version) {
        this.setId(id);
        this.description = description;
        this.amount = amount;
        this.semester = semester;
        this.clubId = clubId;
        this.status = status;
        this.date = date.toString();
        this.reviewerId = reviewerId;
        this.version = version;
    }
    public FundingApplication(Integer id,String description, BigDecimal amount, Integer semester, Integer clubId, fundingApplicationStatus status, Date date,Integer version) {
        this.setId(id);
        this.description = description;
        this.amount = amount;
        this.semester = semester;
        this.clubId = clubId;
        this.status = status;
        this.date = date.toString();
        this.version = version;
    }
    public FundingApplication(String description, BigDecimal amount, Integer semester, Integer clubId, fundingApplicationStatus status, Date date) {
        this.description = description;
        this.amount = amount;
        this.semester = semester;
        this.clubId = clubId;
        this.status = status;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.date =  dateFormat.format(date);
    }

    public FundingApplication() {
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
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
        if (clubId == null || clubId <= 0) {
            throw new IllegalArgumentException("Club ID must be a positive integer.");
        }
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
        return java.sql.Date.valueOf(date);  // 这里的 date 已经是格式化后的字符串
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
        if (clubId == null || clubId <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }
        this.clubId = clubId;
    }

    public Integer getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Integer reviewerId) {
        if (reviewerId != null && reviewerId <= 0) {
            throw new IllegalArgumentException("Reviewer ID must be positive");
        }
        this.reviewerId = reviewerId;
    }

}
