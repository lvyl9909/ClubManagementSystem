package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.FacultyAdministrator;
import org.teamy.backend.security.model.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class FacultyAdministratorMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    private static FacultyAdministratorMapper instance;
    public static synchronized FacultyAdministratorMapper getInstance(DatabaseConnectionManager dbManager) {
        if (instance == null) {
            instance = new FacultyAdministratorMapper(dbManager);
        }
        return instance;
    }
    private FacultyAdministratorMapper(DatabaseConnectionManager databaseConnectionManager){
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public FacultyAdministrator findFacultyAdministratorById(int Id) {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM faculty_administrators WHERE id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return new FacultyAdministrator(rs.getLong("id"), rs.getString("faculty_id"), rs.getString("name")
                        ,rs.getString("email"),rs.getLong("phone_number"),"123", rs.getBoolean("isactive"), Collections.singleton(new Role("ADMIN")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
}
