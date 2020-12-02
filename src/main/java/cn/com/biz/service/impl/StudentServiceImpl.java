package cn.com.biz.service.impl;

import cn.com.biz.pojo.StudentDO;
import cn.com.biz.service.StudentService;
import cn.com.biz.commons.JacksonUtils;
import cn.com.biz.commons.JedisUtils;
import cn.com.biz.commons.PageInfo;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * @author liuchengrui
 * @date 2020/12/1 10:52
 */
@Slf4j
public class StudentServiceImpl implements StudentService {
    /**
     * 存储学生按平均分排序的sorted类型的key
     */
    private final String KEY_STUDENT_RANK = "student:rank";

    private final Integer MIN_SCORE = 0;

    private final Integer MAX_SCORE = 150;

    /**
     * 根据提供的学生id判断该学生是否已存在
     * @param studentId 学生id
     * @return 添加是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:01
     */
    @Override
    public Boolean existStudent(String studentId) {
        Jedis jedis = JedisUtils.getJedis();
        Boolean exists = jedis.exists(studentId);
        JedisUtils.close(jedis);
        return exists;
    }

    /**
     * 根据提供的学生信息录入一个学生数据
     * @param studentDO 学生信息
     * @return 添加是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:01
     */
    @Override
    public Boolean saveStudent(StudentDO studentDO) {
        Transaction multi = JedisUtils.getJedis().multi();
        Response<Boolean> exists = multi.exists(studentDO.getId());
        if (existStudent(studentDO.getId()) && checkField(studentDO)) {
            return saveOrUpdate(studentDO);
        }
        return false;
    }

    /**
     * 根据提供的学生id删除该学生数据
     * @param studentId 学生id
     * @return 删除是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:02
     */
    @Override
    public Boolean removeStudent(String studentId) {
        Jedis jedis = JedisUtils.getJedis();
        Transaction multi = JedisUtils.getJedis().multi();
        try {
            Response<Long> row = multi.del(studentId);
            multi.zrem(KEY_STUDENT_RANK, studentId);
            multi.exec();
            return true;
        } catch (JedisException e) {
            log.warn(e.getMessage(), e);
            multi.discard();
            return false;
        } finally {
            JedisUtils.close(jedis);
        }
    }

    /**
     * 根据提供的学生信息更新该学生数据，学生id必须提供
     * @param studentDO 学生信息
     * @return 更新是否成功
     * @author liuchengrui
     * @date 2020/12/1 11:03
     */
    @Override
    public Boolean updateStudent(StudentDO studentDO) {
        return saveOrUpdate(studentDO);
    }

    /**
     * 以学生平均分倒序排序，分页获取学生数据<br>
     * 其中页码(pageNum)和每页记录数(pageSize)必须大于0，否则设置为默认值1和10
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页详细信息
     * @author liuchengrui
     * @date 2020/12/1 11:04
     */
    @Override
    public PageInfo<StudentDO> listStudentsPage(Integer pageNum, Integer pageSize) {
        Jedis jedis = JedisUtils.getJedis();
        // 获取学生总数
        Long count = jedis.zcount(KEY_STUDENT_RANK, MIN_SCORE, MAX_SCORE);
        // 初始化分页信息
        PageInfo<StudentDO> page = new PageInfo<>(pageNum, pageSize, count);
        // 按获取按平均分排序后该页的学生编号
        Set<String> keys = jedis.zrevrange(KEY_STUDENT_RANK, page.getStartIndex(), page.getEndIndex());
        // 根据编号获取学生详细信息
        List<StudentDO> studentDOList = new ArrayList<>();
        keys.forEach(key -> {
            Map<String, String> stringStringMap = jedis.hgetAll(key);
            StudentDO studentDO = JacksonUtils.mapToBean(stringStringMap, StudentDO.class);
            studentDOList.add(studentDO);
        });
        page.setRecords(studentDOList);
        JedisUtils.close(jedis);
        return page;
    }

    /**
     * 添加或者更新学生数据
     * @param studentDO 学生信息
     * @return 是否成功
     */
    private Boolean saveOrUpdate(StudentDO studentDO) {
        Map<String, String> map = JacksonUtils.beanToMap(studentDO, String.class, String.class);
        Jedis jedis = JedisUtils.getJedis();
        Transaction multi = JedisUtils.getJedis().multi();
        try {
            // 将学生数据存入hash中
            multi.hset(studentDO.getId(), map);
            // 用SortedSet按学生平均分排序存储学生的id
            multi.zadd(KEY_STUDENT_RANK, studentDO.getAvgScore(), studentDO.getId());
            multi.exec();
            return true;
        } catch (JedisException e) {
            log.warn(e.getMessage(), e);
            multi.discard();
            return false;
        } finally {
            JedisUtils.close(jedis);
        }
    }

    /**
     * 检查各字段是否合法，如果字段为null则设置为空字符串
     * @param studentDO 学生信息
     * @return 是否合法
     * @author liuchengrui
     * @date 2020/12/1 14:46
     */
    private boolean checkField(StudentDO studentDO) {
        // 检查各字段是否为null，如果为null则设置为空字符串
        if (studentDO.getName() == null) {
            studentDO.setName("");
        }
        if (studentDO.getDescription() == null) {
            studentDO.setDescription("");
        }
        if (studentDO.getAvgScore() == null) {
            studentDO.setAvgScore(0);
        }
        if (studentDO.getBirthday() == null) {
            studentDO.setBirthday("");
        } else {
            // 检查平均分是否有误
            if (studentDO.getAvgScore() <= MIN_SCORE || studentDO.getAvgScore() >= MAX_SCORE) {
                return false;
            }

            // 检查传入的出生日期是否合法
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                dtf.parse(studentDO.getBirthday());
                return true;
            } catch (DateTimeParseException e) {
                log.warn(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }
}
