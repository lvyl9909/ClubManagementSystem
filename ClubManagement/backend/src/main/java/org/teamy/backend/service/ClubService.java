package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;

import java.util.Collections;
import java.util.List;

public class ClubService {
    private final ClubDataMapper clubDataMapper;

    public ClubService(ClubDataMapper clubDataMapper) {
        this.clubDataMapper = clubDataMapper;
    }

    public Club getClubById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Club club = clubDataMapper.findClubById(id);
//        if (club == null) {
//            throw new Exception("Club with ID " + id + " not found");
//        }
        return club;
    }

    public Club getClubByName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be null or empty");
        }

        Club club = clubDataMapper.findClubByName(name);
        if (club == null) {
            throw new Exception("Club with name '" + name + "' not found");
        }

        return club;
    }
    public List<Club> getAllClub() {
        try {
            return clubDataMapper.getAllClub();
        } catch (Exception e) {
            // // Exceptions are handled here, such as logging or throwing custom exceptions
            System.err.println("Error occurred while fetching clubs: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Returns an empty list to prevent the upper code from crashing
        }
    }

    public boolean saveClub(Club club) throws Exception {
        // You can add additional business logic here, such as data validation
        if (club ==null||club.getName() == null || club.getName().isEmpty()) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        // Recall the methods in DAO layer
        return clubDataMapper.saveClub(club);
    }
}
