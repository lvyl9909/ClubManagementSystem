package org.teamy.backend.model;

import org.teamy.backend.security.model.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FacultyAdministrator extends Person{
    private List<fundingApplication> fundingApplications;

    public FacultyAdministrator(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles, List<fundingApplication> fundingApplications) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.fundingApplications = fundingApplications;
    }
    public FacultyAdministrator(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles ) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.fundingApplications = new ArrayList<>();
    }


    public List<fundingApplication> getFundingApplications() {
        return fundingApplications;
    }

    public void setFundingApplications(List<fundingApplication> fundingApplications) {
        this.fundingApplications = fundingApplications;
    }
}
