package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.model.Club;

public class ClubService {
    private final ClubDataMapper clubDataMapper;

    public ClubService(ClubDataMapper clubDataMapper) {
        this.clubDataMapper = clubDataMapper;
    }

    public Club getClubById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }

        Club club = clubDataMapper.findStudentById(id);
        if (club == null) {
            throw new Exception("Club with ID " + id + " not found");
        }

        return club;
    }

    public Club getClubByName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be null or empty");
        }

        Club club = clubDataMapper.findStudentByName(name);
        if (club == null) {
            throw new Exception("Club with name '" + name + "' not found");
        }

        return club;
    }
}
