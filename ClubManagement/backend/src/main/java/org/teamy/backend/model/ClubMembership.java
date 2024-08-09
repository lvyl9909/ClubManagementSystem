package org.teamy.backend.model;

public class ClubMembership {
    private Student student;
    private Club club;
    private boolean isAdmin;

    public ClubMembership(Student student, Club club, boolean isAdmin) {
        this.student = student;
        this.club = club;
        this.isAdmin = isAdmin;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
