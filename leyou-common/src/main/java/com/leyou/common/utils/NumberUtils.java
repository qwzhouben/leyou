package com.leyou.common.utils;

import java.util.Random;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/9 0009 21:04
 */
public class NumberUtils {

    /**
     * 生成指定位数随机数字
     * @param len
     * @return
     */
    public static String generateCode(int len) {
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(
                Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0, len);
    }

    public static void main(String[] args) {
        System.out.println(generateCode(6));
    }
}
