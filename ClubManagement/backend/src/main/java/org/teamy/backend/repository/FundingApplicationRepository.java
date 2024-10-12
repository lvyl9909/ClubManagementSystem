package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.FundingApplicationMapper;
import org.teamy.backend.model.FundingApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    public FundingApplication findFundingApplicationsByIds(int Id) {
        return fundingApplicationMapper.findFundingApplicationsByIds(Id);
    }
    public List<FundingApplication> getAllFundingApplication() {
        try {
            return fundingApplicationMapper.findAllApplication();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void lockFundingApplicationByClubId(int clubId, Connection conn) throws SQLException {
        fundingApplicationMapper.lockFundingApplicationByClubId(clubId, conn);
    }

    public boolean saveFundingApplication(FundingApplication fundingApplication, Connection conn) throws SQLException {
        return fundingApplicationMapper.saveFundingApplication(fundingApplication, conn);
    }

    public boolean approveFundingApplication(int reviewerId,int applicationId){
        return fundingApplicationMapper.approveFundingApplication(applicationId,reviewerId);
    }
    public boolean rejectFundingApplication(int reviewerId,int applicationId){
        return fundingApplicationMapper.rejectFundingApplication(applicationId,reviewerId);
    }
    public boolean updateFundingApplication(FundingApplication fundingApplication) throws Exception {
        return fundingApplicationMapper.updateFundingApplication(fundingApplication);
    }
}
