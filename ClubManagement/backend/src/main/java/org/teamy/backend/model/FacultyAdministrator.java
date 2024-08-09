package org.teamy.backend.model;

import java.util.List;

public class FacultyAdministrator extends Person{
    private List<fundingApplication> fundingApplications;

    public FacultyAdministrator(String name, String email, Long phoneNumber, List<fundingApplication> fundingApplications) {
        super(name, email, phoneNumber);
        this.fundingApplications = fundingApplications;
    }

    public List<fundingApplication> getFundingApplications() {
        return fundingApplications;
    }

    public void setFundingApplications(List<fundingApplication> fundingApplications) {
        this.fundingApplications = fundingApplications;
    }
}
