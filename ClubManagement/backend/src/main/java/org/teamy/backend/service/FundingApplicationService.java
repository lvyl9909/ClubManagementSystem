package org.teamy.backend.service;

import org.teamy.backend.DataMapper.FundingApplicationMapper;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.repository.*;
import org.teamy.backend.concurrent.LockManagerWait;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FundingApplicationService {
    private final FundingApplicationRepository fundingApplicationRepository;
    private final ClubRepository clubRepository;
    private static FundingApplicationService instance;
    public static synchronized FundingApplicationService getInstance(FundingApplicationRepository fundingApplicationRepository,ClubRepository clubRepository) {
        if (instance == null) {
            instance = new FundingApplicationService(fundingApplicationRepository,clubRepository);
        }
        return instance;
    }
    private FundingApplicationService(FundingApplicationRepository fundingApplicationRepository, ClubRepository clubRepository) {
        this.fundingApplicationRepository = fundingApplicationRepository;
        this.clubRepository = clubRepository;
    }
    public boolean saveFundingApplication(FundingApplication fundingApplication, Connection conn) throws Exception {
        if (fundingApplication == null) {
            throw new IllegalArgumentException("Funding application cannot be null");
        }

        String clubId = String.valueOf(fundingApplication.getClubId());
        String threadName = Thread.currentThread().getName();

        try {
            // Start of thread-level locking
            LockManagerWait.getInstance().acquireLock(clubId, threadName);

            conn.setAutoCommit(false);

            // Lock the funding application at the database level to prevent concurrent submissions
            fundingApplicationRepository.lockFundingApplicationByClubId(fundingApplication.getClubId(), conn);

            // Save the funding application
            boolean isSuccess = fundingApplicationRepository.saveFundingApplication(fundingApplication, conn);

            // Commit the transaction
            conn.commit();

            return isSuccess;
        } catch (SQLException e) {
            conn.rollback();
            throw new RuntimeException("Error saving funding application: " + e.getMessage(), e);
        } finally {
            // Release the thread-level lock
            LockManagerWait.getInstance().releaseLock(clubId, threadName);

            conn.setAutoCommit(true);
        }
    }


    public List<FundingApplication> getAllFundingApplication() {
        try {
            return fundingApplicationRepository.getAllFundingApplication();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void approveFundingApplication(int applicationId,int reviewerId){
        fundingApplicationRepository.approveFundingApplication(reviewerId,applicationId);
    }

    public void rejectFundingApplication(int applicationId,int reviewerId){
        fundingApplicationRepository.rejectFundingApplication(reviewerId,applicationId);
    }
    public boolean updateFundingApplication(FundingApplication fundingApplication) throws Exception {
        if (fundingApplication ==null) {
            throw new IllegalArgumentException("Club cannot be empty");
        }
        try {
            boolean isSuccess = fundingApplicationRepository.updateFundingApplication(fundingApplication);
            clubRepository.invalidateClubCache(fundingApplication.getClubId());
            return isSuccess;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//    public List<FundingApplication> getFundingApplicationByClubId(Integer clubId){
//        try {
//            return fundingApplicationRepository.findApplicationByClubId(clubId);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
