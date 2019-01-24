package com.ecaray.printlib.entity;

import java.io.Serializable;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/15 15:09
 */
public class PrintBean implements Serializable {

    /**
     * 位置（左、中、右）
     */
    public static final int LEFT = 1;
    public static final int CENTER = 2;
    public static final int RIGHT = 3;

    /**
     * 字体大小(若超出3，则代表选择对应的字号)
     */
    public static final int SMALL = 1;
    public static final int MIDDLE = 2;
    public static final int NORMAL = 3;
    public static final int BIG = 4;
    public static final int LINE_SMALL = 5;
    public static final int LINE_NORMAL = 5;
    public static final int LINE_BIG = 5;


    /**
     * 内容类型
     */
    public static final String TXT = "txt";     //文本类型
    public static final String ONE_DIMENSION = "one_dimension";
    public static final String TWO_DIMENSION = "two_dimension"; //二维码
    public static final String DOT_LINE = "dot_line";    //虚线


    //内容
    public String content = "";
    //内容类型
    public String contentType = TXT;
    //字体大小
    public int size = NORMAL;
    //位置
    public int position = LEFT;
    //是否是徐州
    public boolean isXuzhou = false;

    public PrintBean(String content) {
        this.content = content;
    }

    public PrintBean(String content, int size) {
        this.content = content;
        this.size = size;
    }

    public PrintBean(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public PrintBean(String content, String contentType, boolean isXuzhou) {
        this.content = content;
        this.contentType = contentType;
        this.isXuzhou = isXuzhou;
    }

    public PrintBean(String content, String contentType, int position) {
        this.content = content;
        this.contentType = contentType;
        this.position = position;
    }

    public PrintBean(String content, int size, int position) {
        this.content = content;
        this.size = size;
        this.position = position;
    }

    /**
     * @param content     内容
     * @param size        字体大小
     * @param position    字体位置
     * @param contentType 内容类型
     */
    public PrintBean(String content, int size, int position, String contentType) {
        this.content = content;
        this.size = size;
        this.position = position;
        this.contentType = contentType;
    }
}
