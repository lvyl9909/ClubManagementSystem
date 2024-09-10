package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.*;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClubRepository {
    private final ClubDataMapper clubDataMapper;
    private final EventDataMapper eventDataMapper;
    private final FundingApplicationMapper fundingApplicationMapper;
    private final StudentRepository studentRepository;
    private final StudentClubDataMapper studentsClubsDataMapper;


    public ClubRepository(ClubDataMapper clubDataMapper, EventDataMapper eventDataMapper, FundingApplicationMapper fundingApplicationMapper, StudentRepository studentRepository, StudentClubDataMapper studentsClubsDataMapper) {
        this.clubDataMapper = clubDataMapper;
        this.eventDataMapper = eventDataMapper;
        this.fundingApplicationMapper = fundingApplicationMapper;
        this.studentRepository = studentRepository;
        this.studentsClubsDataMapper = studentsClubsDataMapper;
    }
    public Club findClubById(int id) throws SQLException {
        Club club = clubDataMapper.findClubById(id);
        club.setStudentId(studentsClubsDataMapper.findStudentIdByClubId(id));
        club.setEventsId(eventDataMapper.findEventIdByClubId(id));
        club.setFundingApplicationsId(fundingApplicationMapper.findApplicationIdByClubId(id));
        return club;
    }
    public boolean saveClub(Club club) throws Exception {
        return clubDataMapper.saveClub(club);
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
            List<FundingApplication> fundingApplications = fundingApplicationMapper.findFundingApplicationsByIds(club.getFundingApplicationsId());
            club.setFundingApplications(fundingApplications);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading students for club", e);
        }
        return club;
    }
}
