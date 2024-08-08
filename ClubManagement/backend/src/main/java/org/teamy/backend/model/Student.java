package org.teamy.backend.model;

public class Student extends Person{
    private String studentId;
    public Student(String name, String email, Long phoneNumber) {
        super(name, email, phoneNumber);
    }
}
