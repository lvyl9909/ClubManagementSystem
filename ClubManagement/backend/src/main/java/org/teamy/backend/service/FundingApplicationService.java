package org.teamy.backend.service;

import org.teamy.backend.DataMapper.FundingApplicationMapper;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.repository.*;

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
    public boolean saveFundingApplication(FundingApplication fundingApplication) throws Exception {
        // You can add additional business logic here, such as data validation
        if (fundingApplication ==null) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        // Recall methods in DAO layer
        try {
            boolean isSuccess =  fundingApplicationRepository.saveFundingApplication(fundingApplication);
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
