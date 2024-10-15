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
    public FundingApplication findFundingApplicationsByIdsWithLock(int Id,Connection connection) {
        return fundingApplicationMapper.findFundingApplicationsByIdsWithLock(Id,connection);
    }
    public FundingApplication findFundingApplicationsByIds(int Id,Connection connection) {
        return fundingApplicationMapper.findFundingApplicationsById(Id,connection);
    }
    public FundingApplication findFundingApplicationsByIdBeforeReview(int Id,Connection connection) {
        return fundingApplicationMapper.findFundingApplicationsByIdBeforeReview(Id,connection);
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

    public boolean reviewFundingApplication(int reviewerId,FundingApplication application,String stat,Connection connection){
        return fundingApplicationMapper.reviewFundingApplication(application,reviewerId,stat,connection);
    }
    public boolean rejectFundingApplication(int reviewerId,int applicationId){
        return fundingApplicationMapper.rejectFundingApplication(applicationId,reviewerId);
    }
    public boolean updateFundingApplication(FundingApplication fundingApplication,Connection connection) throws Exception {
        return fundingApplicationMapper.updateFundingApplication(fundingApplication,connection);
    }

    public int existsByClubIdAndSemester(Integer clubId, Integer semester, Connection connection) throws SQLException {
        return fundingApplicationMapper.existsByClubIdAndSemester(clubId,semester,connection);
    }
}
