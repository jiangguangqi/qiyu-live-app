package org.qiyu.live.common.interfaces.utils;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于使用Spring的BeanUtils进行对象和对象列表转换的实用工具类。
 *
 * @Author :jianggq
 * @Date :2024/6/24
 * Description : 用于使用Spring的BeanUtils进行对象和对象列表转换的实用工具类。
 */
public class ConvertBeanUtils {

    /**
     * 将源对象转换为指定类的目标对象。
     *
     * @param source      要转换的源对象
     * @param targetClass 目标类的Class对象
     * @param <T>         目标对象的类型
     * @return 转换后的目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target = newInstance(targetClass);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 将源对象列表转换为指定类的目标对象列表。
     *
     * @param sourceList  要转换的源对象列表
     * @param targetClass 目标类的Class对象
     * @param <K>         源对象列表中的对象类型
     * @param <T>         目标对象列表中的对象类型
     * @return 转换后的目标对象列表
     */
    public static <K, T> List<T> convertList(List<K> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        List<T> targetList = new ArrayList<>((int) (sourceList.size() / 0.75) + 1);
        for (K source : sourceList) {
            targetList.add(convert(source, targetClass));
        }
        return targetList;
    }

    /**
     * 实例化指定类的新对象。
     *
     * @param targetClass 要实例化的类的Class对象
     * @param <T>         要实例化的对象类型
     * @return 指定类的新实例
     * @throws BeanInstantiationException 如果实例化失败
     */
    private static <T> T newInstance(Class<T> targetClass) {
        try {
            return targetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException(targetClass, "实例化失败", e);
        }
    }
}
