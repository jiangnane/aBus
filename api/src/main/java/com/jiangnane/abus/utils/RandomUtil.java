package com.jiangnane.abus.utils;

import java.util.List;

/**
 * 随机数工具
 * <p>
 * Created by hanwei on 21/9/15.
 */
public class RandomUtil {

    public static <T extends Object> T pickFrom(List<T> source) {
        if (source == null) {
            return null;
        }

        int size = source.size();
        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return source.get(0);
        }

        int index = pickInt(0, size - 1);
        return source.get(index);
    }

    public static <T extends Object> T pickFrom(T[] source) {
        if (source == null) {
            return null;
        }

        int size = source.length;
        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return source[0];
        }

        int index = pickInt(0, size - 1);
        return source[index];
    }

    /**
     * 从最小值到最大值中间随机去一个int(包含最大值和最小值)
     * @param min
     * @param max
     * @return
     */
    public static int pickInt(int min, int max) {
        long randomNum = System.currentTimeMillis();
        return (int) (randomNum % (max - min + 1) + min);
    }
}
