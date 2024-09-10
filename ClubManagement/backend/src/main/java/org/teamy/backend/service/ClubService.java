package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.Student;
import org.teamy.backend.repository.ClubRepository;

import java.util.Collections;
import java.util.List;

public class ClubService {
    private final ClubRepository clubRepository;
    private static ClubService instance;
    public static synchronized ClubService getInstance(ClubRepository clubRepository) {
        if (instance == null) {
            instance = new ClubService(clubRepository);
        }
        return instance;
    }
    private ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public Club getClubById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }
        Club club = clubRepository.findClubById(id);
        return club;
    }
    public List<Club> getAllClub() {
        try {
            return clubRepository.getAllClub();
        } catch (Exception e) {
            // // Exceptions are handled here, such as logging or throwing custom exceptions
            System.err.println("Error occurred while fetching clubs: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Returns an empty list to prevent the upper code from crashing
        }
    }

    public void saveClub(Club club) throws Exception {
        // You can add additional business logic here, such as data validation
        if (club ==null||club.getName() == null || club.getName().isEmpty()) {
            throw new IllegalArgumentException("Club cannot be empty");
        }
        if (!clubRepository.saveClub(club)) {
            throw new RuntimeException("Failed to save the club.");
        }
    }
    public List<FundingApplication> getFundingApplication(Club club){
        if (club.getFundingApplications()==null||club.getFundingApplications().isEmpty()){
            club=clubRepository.lazyLoadApplication(club);
        }
        return club.getFundingApplications();
    }
}
