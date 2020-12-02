package cn.com.biz.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生实体类
 * @author liuchengrui
 * @date 2020/12/1 10:39
 */
@Data
public class StudentDO {

    /**
     * 学生学号，长度40
     */
    private String id;
    /**
     * 学生姓名，长度40
     */
    private String name;
    /**
     * 出生日期
     */
    private String birthday;
    /**
     * 备注，长度255
     */
    private String description;
    /**
     * 平均分
     */
    private Integer avgScore;
}
