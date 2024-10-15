package org.teamy.backend.model;

import org.teamy.backend.security.model.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FacultyAdministrator extends Person{
    private List<FundingApplication> FundingApplications;
    private List<Integer> fundingApplicationIds;

    public FacultyAdministrator(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles, List<FundingApplication> FundingApplications) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.FundingApplications = FundingApplications;
    }
    public FacultyAdministrator(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles ) {
        super(id, username, name, email, phoneNumber, password, isActive, roles);
        this.FundingApplications = new ArrayList<>();
    }

    public FacultyAdministrator(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive,String role) {
        super(id, name,email,  phoneNumber, password, username, isActive,role);
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

    public List<Integer> getFundingApplicationIds() {
        return fundingApplicationIds;
    }

    public void setFundingApplicationIds(List<Integer> fundingApplicationIds) {
        this.fundingApplicationIds = fundingApplicationIds;
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
