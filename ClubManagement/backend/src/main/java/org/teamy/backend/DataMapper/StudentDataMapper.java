package org.teamy.backend.DataMapper;

import java.sql.Connection;

public class StudentDataMapper {
    private Connection connection;

    public StudentDataMapper(Connection connection) {
        this.connection = connection;
    }

}
