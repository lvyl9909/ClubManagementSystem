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
        String pathInfo = req.getPathInfo(); // 获取URL中的路径部分

        if (pathInfo == null || pathInfo.equals("/")) {
            // /students -> 显示学生列表
            listStudents(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            // /students/{studentId} -> 显示特定学生详情
            String studentId = pathInfo.substring(1); // 去掉前面的斜杠获取ID
            viewStudent(req, resp, studentId);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 返回404错误
        }
    }

    private void viewStudent(HttpServletRequest req, HttpServletResponse resp, String studentId) throws IOException {
        resp.getWriter().write("Viewing student with ID: " + studentId);

    }

    private void listStudents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Listing all students...");
    }
}
