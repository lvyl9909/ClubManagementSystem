package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.Student;
import org.teamy.backend.repository.ClubRepository;
import org.teamy.backend.repository.StudentRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ClubService {
    private final ClubRepository clubRepository;
    private final StudentRepository studentRepository;
    private static ClubService instance;
    private final DatabaseConnectionManager databaseConnectionManager;
    public static synchronized ClubService getInstance(ClubRepository clubRepository, StudentRepository studentRepository,DatabaseConnectionManager databaseConnectionManager) {
        if (instance == null) {
            instance = new ClubService(clubRepository,studentRepository,databaseConnectionManager);
        }
        return instance;
    }
    private ClubService(ClubRepository clubRepository, StudentRepository studentRepository,DatabaseConnectionManager databaseConnectionManager) {
        this.clubRepository = clubRepository;
        this.studentRepository = studentRepository;
        this.databaseConnectionManager = databaseConnectionManager;
    }

    public Club getClubById(int id) throws Exception {
        Connection connection =null;
        try {
            connection = databaseConnectionManager.nextConnection();
            Club club = clubRepository.findClubById(id,connection);
            return club;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            databaseConnectionManager.releaseConnection(connection);
        }
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

    public synchronized void saveClub(Club club) throws Exception {
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
