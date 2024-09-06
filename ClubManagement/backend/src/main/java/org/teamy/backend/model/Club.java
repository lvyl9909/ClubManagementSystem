package org.teamy.backend.model;

import org.teamy.backend.service.EventService;
import org.teamy.backend.service.StudentService;

import java.util.ArrayList;
import java.util.List;

public class Club {
    private String name;
    private String description;
    private List<Integer> studentId;
    private List<Student> students;
    private List<Integer> eventsId;
    private List<Event> events;
    private List<fundingApplication> fundingApplications;

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
        this.studentId=new ArrayList<>();
        this.events = new ArrayList<>();
        this.fundingApplications = new ArrayList<>();
        this.students = new ArrayList<>();
        this.eventsId = new ArrayList<>();

    }

    public Club() {
    }

    @Override
    public String toString() {
        return "Club{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getStudentId() {
        return studentId;
    }

    public void setStudentId(List<Integer> studentId) {
        this.studentId = studentId;
    }


    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Integer> getEventsId() {
        return eventsId;
    }

    public void setEventsId(List<Integer> eventsId) {
        this.eventsId = eventsId;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    //还需要加lazy load
    public List<fundingApplication> getFundingApplications() {
        return fundingApplications;
    }

    public void setFundingApplications(List<fundingApplication> fundingApplications) {
        this.fundingApplications = fundingApplications;
    }
}
