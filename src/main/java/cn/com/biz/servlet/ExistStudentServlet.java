package cn.com.biz.servlet;

import cn.com.biz.service.StudentService;
import cn.com.biz.service.impl.StudentServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 该servlet根据请求中的id判断学生id是否已存在
 * @author liuchengrui
 * @date 2020/12/1 20:30
 */
@WebServlet("/isExist")
public class ExistStudentServlet extends HttpServlet {

    private StudentService studentService = new StudentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentId = request.getParameter("id");
        Boolean isSuccess = studentService.existStudent(studentId);
        PrintWriter writer = response.getWriter();
        if (isSuccess) {
            writer.print("400");
        }
        writer.close();
    }
}
