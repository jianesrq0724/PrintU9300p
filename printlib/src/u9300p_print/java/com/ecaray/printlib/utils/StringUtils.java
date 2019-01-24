package com.ecaray.printlib.utils;

/**
 * 功能：
 * 创建者：ruiqin.shen
 * 创建日期：2017/12/22
 * 版权所有：深圳市亿车科技有限公司
 */

public class StringUtils {
    /**
     * 获取X方向开始位置,居中的时候
     *
     * @returngra
     */
    public static int getStartPointX(String content, int fontSize) {
        //汉字的个数
        int wordCount;
        //字母和数字的个数
        int letterCount;
        //计算汉字的个数
        letterCount = getLetterCount(content);
        wordCount = content.length() - letterCount;
        //内容占的像素
        int contentLength = fontSize * wordCount + fontSize * letterCount / 2;
        int startPoint = (384 - contentLength) / 2;
        //字母和数字按汉字的一半计算
        return startPoint;
    }

    /**
     * 计算大小写、数字的个数
     *
     * @param content
     * @return
     */
    protected static int getLetterCount(String content) {
        int letterCount = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            //判断大小写字母，数字
            boolean isNotWord = (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
            if (isNotWord) {
                letterCount++;
            }
        }
        return letterCount;
    }
}
