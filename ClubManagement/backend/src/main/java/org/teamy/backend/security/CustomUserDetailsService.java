package org.teamy.backend.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teamy.backend.DataMapper.StudentDataMapper;
import org.teamy.backend.model.Person;
import org.teamy.backend.repository.StudentRepository;
import org.teamy.backend.security.model.Role;
import org.teamy.backend.service.StudentService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, Person> userDatabase = new HashMap<>();
    private final StudentRepository studentRepository;
    private static CustomUserDetailsService instance;
    public static synchronized CustomUserDetailsService getInstance(StudentRepository studentRepository) {
        if (instance == null) {
            instance = new CustomUserDetailsService(studentRepository);
        }
        return instance;
    }
    public CustomUserDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.studentRepository = null;
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
        Person user=null;
        try {
            user = studentRepository.findStudentByUsername(username);

            if (user == null) {
                System.out.println("username not found");
                throw new UsernameNotFoundException("User not found");
            }else {
                System.out.println(user.getAuthorities());
                System.out.println(user.getPassword());
            }
            return user;
        } catch (SQLException e) {
            throw new UsernameNotFoundException("Database error", e);
        }
    }

    // 你可以添加方法来动态添加或删除用户
    public void createUser(Person person) {
        userDatabase.put(person.getUsername(), person);
    }

}