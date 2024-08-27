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
    private final StudentService studentService;
    private final EventService eventService;

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
        this.studentId=new ArrayList<>();
        this.events = new ArrayList<>();
        this.fundingApplications = new ArrayList<>();
        this.students = new ArrayList<>();
        this.eventsId = new ArrayList<>();

        this.studentService=null;
        this.eventService =null;
    }

    public Club(String name, String description, StudentService studentService, EventService eventService) {
        this.name = name;
        this.description = description;
        this.studentId=new ArrayList<>();
        this.events = new ArrayList<>();
        this.fundingApplications = new ArrayList<>();
        this.students = new ArrayList<>();
        this.eventsId = new ArrayList<>();

        this.studentService= studentService;
        this.eventService = eventService;
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

    public List<Student> getStudents() {
        if(students.isEmpty()){
            for (Integer id:studentId){
                try {
                    Student student = studentService.getStudentById(id);
                    students.add(student);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return students;
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

    public List<Event> getEvents() {
        if(events.isEmpty()){
            for (Integer id:eventsId){
                try {
                    Event event = eventService.getEventById(id);
                    events.add(event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
