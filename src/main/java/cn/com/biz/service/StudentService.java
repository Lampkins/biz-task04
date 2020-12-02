package cn.com.biz.service;

import cn.com.biz.pojo.StudentDO;
import cn.com.biz.commons.PageInfo;

/**
 * 接口提供学生数据的管理功能
 * @author liuchengrui
 * @date 2020/12/1 10:46
 */
public interface StudentService {

    /**
     * 根据提供的学生id判断该学生是否已存在
     * @param studentId 学生id
     * @return 添加是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:01
     */
    Boolean existStudent(String studentId);


    /**
     * 根据提供的学生信息录入一个学生数据
     * @param studentDO 学生信息
     * @return 添加是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:01
     */
    Boolean saveStudent(StudentDO studentDO);

    /**
     * 根据提供的学生id删除该学生数据
     * @param studentId 学生id
     * @return 删除是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:02
     */
    Boolean removeStudent(String studentId);

    /**
     * 根据提供的学生信息更新该学生数据，学生id必须提供
     * @param studentDO 学生信息
     * @return 更新是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:03
     */
    Boolean updateStudent(StudentDO studentDO);

    /**
     * 以学生平均分倒序排序，分页获取学生数据<br>
     * 其中页码(pageNum)和每页记录数(pageSize)必须大于0，否则设置为默认值1和10
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页详细信息
     * @author liuchengrui
     * @date 2020/12/1 11:04
     */
    PageInfo<StudentDO> listStudentsPage(Integer pageNum, Integer pageSize);


}
