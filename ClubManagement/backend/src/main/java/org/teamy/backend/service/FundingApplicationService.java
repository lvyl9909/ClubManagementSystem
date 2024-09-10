package org.teamy.backend.service;

import org.teamy.backend.DataMapper.FundingApplicationMapper;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.repository.EventRepository;
import org.teamy.backend.repository.FundingApplicationRepository;
import org.teamy.backend.repository.RSVPRepository;
import org.teamy.backend.repository.TicketRepository;

import java.sql.SQLException;
import java.util.List;

public class FundingApplicationService {
    private final FundingApplicationRepository fundingApplicationRepository;
    private static FundingApplicationService instance;
    public static synchronized FundingApplicationService getInstance(FundingApplicationRepository fundingApplicationRepository) {
        if (instance == null) {
            instance = new FundingApplicationService(fundingApplicationRepository);
        }
        return instance;
    }
    private FundingApplicationService(FundingApplicationRepository fundingApplicationRepository) {
        this.fundingApplicationRepository = fundingApplicationRepository;
    }
    public boolean saveFundingApplication(FundingApplication fundingApplication) throws Exception {
        // You can add additional business logic here, such as data validation
        if (fundingApplication ==null) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        // Recall methods in DAO layer
        try {
            return fundingApplicationRepository.saveFundingApplication(fundingApplication);
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
