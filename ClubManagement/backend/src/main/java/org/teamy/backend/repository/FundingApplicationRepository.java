package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.FundingApplicationMapper;
import org.teamy.backend.model.FundingApplication;

import java.sql.SQLException;
import java.util.List;

public class FundingApplicationRepository {
    private final FundingApplicationMapper fundingApplicationMapper;
    private static FundingApplicationRepository instance;

    private FundingApplicationRepository(FundingApplicationMapper fundingApplicationMapper) {
        this.fundingApplicationMapper = fundingApplicationMapper;
    }
    public static synchronized FundingApplicationRepository getInstance(FundingApplicationMapper fundingApplicationMapper){
        if(instance == null){
            instance = new FundingApplicationRepository(fundingApplicationMapper);
        }
        return instance;
    }
    public FundingApplication findfundingApplicationById(int Id) {
        return fundingApplicationMapper.findfundingApplicationById(Id);
    }
    public boolean saveFundingApplication(FundingApplication fundingApplication)throws SQLException {
        return fundingApplicationMapper.saveFundingApplication(fundingApplication);
    }
}
