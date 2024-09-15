package org.teamy.backend.model;

public class Venue extends DomainObject {
    private String name;
    private String description;
    private String location;
    private Integer capacity;

    public Venue(Integer id,String name, String description, String location, Integer capacity) {
        this.setId(id);
        this.name = name;
        this.description = description;
        this.location = location;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Venue name cannot be empty");
        }
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Venue location cannot be empty");
        }
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        if (capacity == null || capacity < 0) {
            throw new IllegalArgumentException("Capacity must be a non-negative number");
        }
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
