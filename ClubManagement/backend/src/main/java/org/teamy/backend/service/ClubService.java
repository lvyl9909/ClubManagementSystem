package org.teamy.backend.service;

import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Student;

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

    public boolean saveClub(Club club) throws Exception {
        // 可以在这里添加额外的业务逻辑，比如数据验证
        if (club ==null||club.getName() == null || club.getName().isEmpty()) {
            throw new IllegalArgumentException("Club cannot be empty");
        }

        // 调用DAO层的方法
        return clubDataMapper.saveClub(club);
    }
}
