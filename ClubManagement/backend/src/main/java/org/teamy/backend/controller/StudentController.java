package org.teamy.backend.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.*;


import java.io.IOException;
@WebServlet("/students/*")
public class StudentController  extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Request the path info in URL

        if (pathInfo == null || pathInfo.equals("/")) {
            // /students -> show student list
            listStudents(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            // /students/{studentId} -> show specific student with id
            String studentId = pathInfo.substring(1); // Remove the preceding slash to get the ID
            viewStudent(req, resp, studentId);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // Return 404
        }
    }

    private void viewStudent(HttpServletRequest req, HttpServletResponse resp, String studentId) throws IOException {
        resp.getWriter().write("Viewing student with ID: " + studentId);

    }

    private void listStudents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Listing all students...");
    }
}
