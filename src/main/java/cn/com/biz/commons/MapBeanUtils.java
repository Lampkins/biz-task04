package cn.com.biz.commons;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Map和Bean相互转换
 * @author liuchengrui
 * @date 2020/11/30 10:02
 */
@Slf4j
public class MapBeanUtils {

    private MapBeanUtils() {}

    /**
     * 根据提供的参数map集合和要转换的bean类对象<br>
     * 如果map为空则返回空，类型创建需要捕获反射相关的异常并打印异常日志
     * @param map 提供的参数map
     * @param beanClass 转换的bean类对象
     * @param <T> 转换的bean类型
     * @return 转换成成功ean对象
     */
    public static <T> T mapToBean(Map<String, ?> map, Class<T> beanClass) {
        if (map == null) {
            return null;
        }
        try {
            T obj = beanClass.newInstance();
            BeanUtils.populate(obj, map);
            return obj;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    public static Map<?, ?> beanToMap(Object obj) {
        if(obj == null) {
            return null;
        }
        return new org.apache.commons.beanutils.BeanMap(obj);
    }

}
