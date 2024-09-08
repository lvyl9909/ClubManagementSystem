package org.teamy.backend.service;

import org.teamy.backend.DataMapper.FundingApplicationMapper;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;

import java.sql.SQLException;
import java.util.List;

public class FundingApplicationService {
    private final FundingApplicationMapper fundingApplicationMapper;

    public FundingApplicationService(FundingApplicationMapper fundingApplicationMapper) {
        this.fundingApplicationMapper = fundingApplicationMapper;
    }
    public boolean saveFundingApplication(FundingApplication fundingApplication) throws Exception {
        // You can add additional business logic here, such as data validation
        if (fundingApplication ==null) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        // Recall methods in DAO layer
        try {
            return fundingApplicationMapper.saveFundingApplication(fundingApplication);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<FundingApplication> getFundingApplicationByClubId(Integer clubId){
        try {
            return fundingApplicationMapper.findApplicationByClubId(clubId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
