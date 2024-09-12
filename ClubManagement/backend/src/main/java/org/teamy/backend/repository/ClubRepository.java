package org.teamy.backend.repository;

import org.teamy.backend.DataMapper.*;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.Student;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClubRepository {
    private final ClubDataMapper clubDataMapper;
    private final EventDataMapper eventDataMapper;
    private final FundingApplicationMapper fundingApplicationMapper;
    private final StudentRepository studentRepository;
    private final StudentClubDataMapper studentsClubsDataMapper;
    private static ClubRepository instance;

    private final Cache<Integer, Club> clubCache;


    private ClubRepository(ClubDataMapper clubDataMapper, EventDataMapper eventDataMapper, FundingApplicationMapper fundingApplicationMapper, StudentRepository studentRepository, StudentClubDataMapper studentsClubsDataMapper) {
        this.clubDataMapper = clubDataMapper;
        this.eventDataMapper = eventDataMapper;
        this.fundingApplicationMapper = fundingApplicationMapper;
        this.studentRepository = studentRepository;
        this.studentsClubsDataMapper = studentsClubsDataMapper;

        // Initialize cache with max size and expiration time
        this.clubCache = CacheBuilder.newBuilder()
                .maximumSize(100) // Maximum 100 clubs in the cache
                .expireAfterWrite(30, TimeUnit.MINUTES) // Expire entries after 10 minutes
                .build();
    }
    public static synchronized ClubRepository getInstance(ClubDataMapper clubDataMapper, EventDataMapper eventDataMapper, FundingApplicationMapper fundingApplicationMapper, StudentRepository studentRepository, StudentClubDataMapper studentsClubsDataMapper) {
        if (instance == null) {
            instance = new ClubRepository(clubDataMapper,eventDataMapper,fundingApplicationMapper,studentRepository,studentsClubsDataMapper);
        }
        return instance;
    }
    public Club findClubById(int id) throws SQLException {
        // Check cache first
        Club club = clubCache.getIfPresent(id);
        if (club != null) {
            return club; // Return cached club if available
        }
        // If not in cache, fetch from database and cache the result
        club = clubDataMapper.findClubById(id);
        if (club != null) {
            club.setStudentId(studentsClubsDataMapper.findStudentIdByClubId(id));
            club.setEventsId(eventDataMapper.findEventIdByClubId(id));
            club.setFundingApplicationsId(fundingApplicationMapper.findApplicationIdByClubId(id));
            // Store in cache
            clubCache.put(id, club);
        }
        return club;
    }
    public boolean saveClub(Club club) throws Exception {
        boolean result = clubDataMapper.saveClub(club);
        if (result) {
            // Update cache after saving
            clubCache.put(club.getId(), club);
        }
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

            clubCache.put(club.getId(),club);
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

            clubCache.put(club.getId(),club);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading Application for club", e);
        }
        return club;
    }
}
