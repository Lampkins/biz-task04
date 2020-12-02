package cn.com.biz.servlet;

import cn.com.biz.commons.JacksonUtils;
import cn.com.biz.commons.MapBeanUtils;
import cn.com.biz.pojo.StudentDO;
import cn.com.biz.service.StudentService;
import cn.com.biz.service.impl.StudentServiceImpl;
import cn.com.biz.commons.PageInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 该servlet主要用于处理与学生数据管理相关的请求
 * @author liuchengrui
 * @date 2020/12/1 10:46
 */
@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private StudentService studentService = new StudentServiceImpl();

    /**
     * 分页获取学生数据信息，接受页码(pageNum)一个参数
     * @author liuchengrui
     * @date 2020/12/1 16:38
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pageNumberStr = request.getParameter("pageNum");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNum = Integer.parseInt(pageNumberStr);
        PageInfo pageInfoVO = studentService.listStudentsPage(pageNum, 10);
        response.getWriter().print(JacksonUtils.objectToJsonStr(pageInfoVO));
    }

    /**
     * 添加一个用户
     * @author liuchengrui
     * @date 2020/12/1 16:40
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        StudentDO studentDO = MapBeanUtils.mapToBean(parameterMap, StudentDO.class);
        boolean isSuccess = studentService.saveStudent(studentDO);
        PrintWriter writer = response.getWriter();
        if (isSuccess) {
            writer.print("添加成功");
        } else {
            writer.print("添加失败");
        }
        writer.close();
    }

    /**
     * 删除用户
     * @author liuchengrui
     * @date 2020/12/1 16:42
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        String studentId = request.getParameter("id");
        Boolean isSuccess = studentService.removeStudent(studentId);
        if (isSuccess) {
            writer.print("删除成功");
        } else {
            writer.print("删除失败");
        }
        writer.close();
    }

    /**
     * 更新用户信息
     * @author liuchengrui
     * @date 2020/12/1 16:45
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        StudentDO studentDO = MapBeanUtils.mapToBean(parameterMap, StudentDO.class);
        PrintWriter writer = response.getWriter();
        Boolean isSuccess = studentService.updateStudent(studentDO);
        if (isSuccess) {
            writer.print("更新成功");
        } else {
            writer.print("更新失败");
        }
        writer.close();
    }
}
