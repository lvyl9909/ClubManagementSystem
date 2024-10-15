package org.teamy.backend.model;

import java.math.BigDecimal;
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
    private float budget;


    public Club(Integer id,String name, String description,float budget) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Club ID must be positive");
        }
        this.setId(id);  // Inherited from DomainObject
        this.name = name;
        this.description = description;
        this.budget = budget;
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
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be empty");
        }
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

    public float getBudget(){
        return this.budget;
    }


    public void setBudget(float budget) {
        if (budget < 0) {
            throw new IllegalArgumentException("Budget cannot be negative");  // domain logic, no persistence dependency
        }
        this.budget = budget;
    }

    public boolean addEvent(Event event){
        if(event.getCost().compareTo(BigDecimal.valueOf(this.budget))!=1){
            setBudget(this.budget-event.getCost().floatValue());
            this.events.add(event);
            return true;
        }
        return false;
    }

    public void deleteEvent(Event event){
        this.events.remove(event);
        setBudget(this.budget+event.getCost().floatValue());
    }

    private void addStudent(int studentID, Student student){
        this.studentId.add(studentID);
        this.students.add(student);
    }

    private void deleteStudent(int studentID, Student student){
        this.studentId.remove(studentID);
        this.students.remove(student);
    }

    private void addFundingApplication(int fundingApplicationID, FundingApplication fundingApplication){
        this.FundingApplications.add(fundingApplication);
        this.FundingApplicationsId.add(fundingApplicationID);
        this.budget += fundingApplication.getAmount().floatValue();
    }
    public boolean isNameEmpty(){
        return this.name.isEmpty();
    }
}
