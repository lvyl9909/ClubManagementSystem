package org.teamy.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Club extends DomainObject {
    private String name;
    private String description;
    private List<Integer> studentId;
    private List<Student> students;
    private List<Integer> eventsId;
    private List<Event> events;
    private List<FundingApplication> FundingApplications;
    private List<Integer> FundingApplicationsId;


    public Club(Integer id,String name, String description) {
        this.setId(id);  // Inherited from DomainObject
        this.name = name;
        this.description = description;
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
    public List<FundingApplication> getFundingApplications() {
        return FundingApplications;
    }

    public void setFundingApplications(List<FundingApplication> FundingApplications) {
        this.FundingApplications = FundingApplications;
    }

    public List<Integer> getFundingApplicationsId() {
        return FundingApplicationsId;
    }

    public void setFundingApplicationsId(List<Integer> fundingApplicationsId) {
        FundingApplicationsId = fundingApplicationsId;
    }
}
