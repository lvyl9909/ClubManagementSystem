package org.teamy.backend.model;

import org.teamy.backend.security.model.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FacultyAdministrator extends Person{
    private List<FundingApplication> FundingApplications;

    public FacultyAdministrator(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles, List<FundingApplication> FundingApplications) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.FundingApplications = FundingApplications;
    }
    public FacultyAdministrator(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles ) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.FundingApplications = new ArrayList<>();
    }


    public List<FundingApplication> getFundingApplications() {
        return FundingApplications;
    }

    public void setFundingApplications(List<FundingApplication> FundingApplications) {
        this.FundingApplications = FundingApplications;
    }

    public void addFundingApplication(FundingApplication fundingApplication){
        this.FundingApplications.add(fundingApplication);
    }

    public boolean deleteFundingApplication(FundingApplication fundingApplication){
        if(this.FundingApplications.contains(fundingApplication)){
            this.FundingApplications.remove(fundingApplication);
            return true;
        }
        else {
            return false;
        }
    }
}
