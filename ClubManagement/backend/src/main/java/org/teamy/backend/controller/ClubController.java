package org.teamy.backend.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamy.backend.DataMapper.ClubDataMapper;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.service.ClubService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;


@WebServlet("/clubs/*")
public class ClubController extends HttpServlet {
    private ClubService clubService;
    private Gson gson = new Gson();  // Gson instance

    @Override
    public void init() throws ServletException {
        // Suppose you have a way to get an instance of ClubService
        // Such as through dependency injection, service locator pattern, or manual instantiation
        DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
        clubService = new ClubService(new ClubDataMapper(databaseConnectionManager.getConnection()));  // assume ClubMapperImpl is the specific implementation
        System.out.println("success init");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id"); // Gets the "id" argument from the query string
        String pathInfo = req.getPathInfo(); // Gets the path portion of the URL

        if (Objects.equals(idParam, "-1")) {
            listClubs(req, resp); // If there is no id parameter, all clubs are listed
        } else {
            try {
                Integer clubId = Integer.valueOf(idParam); // Converts the id argument to an integer
                viewClub(req, resp, clubId); // call viewClub method
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format"); // ID invalid, return 404
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
//        else {
//            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // return 404
//        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("processing");
        String pathInfo = req.getPathInfo(); // get URL path info

        if (pathInfo.equals("/save")) {
            saveClub(req, resp);
        }
    }

    private void saveClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Parse the club data from the request, assuming that the request body is in JSON format
            Club club = parseClubFromRequest(req);

            // Call the Service layer to save the club
            boolean isSaved = clubService.saveClub(club);

            if (isSaved) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                //resp.getWriter().write("Club saved successfully.");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                //resp.getWriter().write("Failed to save the club.");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            //resp.getWriter().write("An error occurred while saving the club. "+e.getMessage());
        }
    }

    private Club parseClubFromRequest(HttpServletRequest req) throws IOException {
        // 使用BufferedReader读取请求体
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuffer.append(line);
            System.out.println(line);
        }

        // Use Gson to parse JSON strings into Java objects
        Club club = gson.fromJson(jsonBuffer.toString(), Club.class);
        System.out.println(club.toString());

        // correct data
        if (club.getName() == null || club.getName().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be empty");
        }

        return club;
    }

    private void viewClub(HttpServletRequest req, HttpServletResponse resp, Integer ClubId) throws Exception {
        PrintWriter out = resp.getWriter();
        Club club = clubService.getClubById(ClubId);
        if (club != null) {
            out.write("{\"name\":\"" + club.getName() + "\", \"description\":\"" + club.getDescription() + "\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.write("{\"error\":\"Club not found.\"}");
        }
    }

    private void listClubs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Club> clubs = clubService.getAllClub();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Use Gson to convert the list to JSON and return it
        Gson gson = new Gson();
        String json = gson.toJson(clubs);
        out.print(json);
        out.flush();
    }

}
