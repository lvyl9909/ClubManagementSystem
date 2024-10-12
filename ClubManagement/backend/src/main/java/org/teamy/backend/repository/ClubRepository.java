package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.*;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.Student;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClubRepository {
    private final ClubDataMapper clubDataMapper;
    private final EventDataMapper eventDataMapper;
    private final FundingApplicationMapper fundingApplicationMapper;
    private final StudentRepository studentRepository;
    private final StudentClubDataMapper studentsClubsDataMapper;
    private static ClubRepository instance;

    private ClubRepository(ClubDataMapper clubDataMapper, EventDataMapper eventDataMapper, FundingApplicationMapper fundingApplicationMapper, StudentRepository studentRepository, StudentClubDataMapper studentsClubsDataMapper) {
        this.clubDataMapper = clubDataMapper;
        this.eventDataMapper = eventDataMapper;
        this.fundingApplicationMapper = fundingApplicationMapper;
        this.studentRepository = studentRepository;
        this.studentsClubsDataMapper = studentsClubsDataMapper;
    }
    public static synchronized ClubRepository getInstance(ClubDataMapper clubDataMapper, EventDataMapper eventDataMapper, FundingApplicationMapper fundingApplicationMapper, StudentRepository studentRepository, StudentClubDataMapper studentsClubsDataMapper) {
        if (instance == null) {
            instance = new ClubRepository(clubDataMapper,eventDataMapper,fundingApplicationMapper,studentRepository,studentsClubsDataMapper);
        }
        return instance;
    }

    public Club findClubById(int id, Connection connection) throws SQLException {
        try {
            Club club = clubDataMapper.findClubById(id,connection);
            if (club != null) {
                club.setStudentId(studentsClubsDataMapper.findStudentIdByClubId(id,connection));
                club.setEventsId(eventDataMapper.findEventIdByClubId(id,connection));
                club.setFundingApplicationsId(fundingApplicationMapper.findApplicationIdByClubId(id,connection));
            }
            return club;
        } catch (RuntimeException e) {
            throw new SQLException("Error fetching club data", e);
        }
    }
    public boolean saveClub(Club club) throws Exception {
        boolean result = clubDataMapper.saveClub(club);
        return result;
    }
    public List<Club> getAllClub() {
        return clubDataMapper.getAllClub();
    }

    public Club lazyLoadStudent(Club club){
        try {
            List<Student> students = studentRepository.findStudentsById(club.getStudentId());
            club.setStudents(students);
        } catch (Exception e) {
            throw new RuntimeException("Error loading students for club", e);
        }
        return club;
    }

    public Club lazyLoadEvent(Club club){
        try {
            List<Event> events = eventDataMapper.findEventsByIds(club.getEventsId());
            club.setEvents(events);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading students for club", e);
        }
        return club;
    }
    public Club lazyLoadApplication(Club club){
        try {
            List<FundingApplication> fundingApplications =
                    fundingApplicationMapper.findFundingApplicationsByIds(club.getFundingApplicationsId());
            club.setFundingApplications(fundingApplications);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading Application for club", e);
        }
        return club;
    }
}
