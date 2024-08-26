package org.teamy.backend.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teamy.backend.DataMapper.StudentDataMapper;
import org.teamy.backend.model.Person;
import org.teamy.backend.security.model.Role;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, Person> userDatabase = new HashMap<>();
    private final StudentDataMapper studentDataMapper;

    public CustomUserDetailsService(StudentDataMapper studentDataMapper) {
        this.studentDataMapper = studentDataMapper;
    }
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.studentDataMapper = null;
        Person admin = new Person();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("adminPass"));
        admin.setRoles(Set.of(Role.ADMIN));
        admin.setActive(true);
        userDatabase.put(admin.getUsername(), admin);

        Person user = new Person();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("userPass"));
        user.setRoles(Set.of(Role.USER));
        user.setActive(true);
        userDatabase.put(user.getUsername(), user);
    }

    @Override
    public Person loadUserByUsername(String username) throws UsernameNotFoundException {
//        Person user = userDatabase.get(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
        Person user=null;
        try {
            user = studentDataMapper.findStudentByStudentId(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return user;
        } catch (SQLException e) {
            throw new UsernameNotFoundException("Database error", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 你可以添加方法来动态添加或删除用户
    public void createUser(Person person) {
        userDatabase.put(person.getUsername(), person);
    }

}