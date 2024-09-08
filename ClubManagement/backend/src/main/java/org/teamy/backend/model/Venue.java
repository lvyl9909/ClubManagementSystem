package org.teamy.backend.model;

public class Venue {
    private Integer id;
    private String name;
    private String description;
    private String location;
    private Integer capacity;

    public Venue(Integer id,String name, String description, String location, Integer capacity) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
