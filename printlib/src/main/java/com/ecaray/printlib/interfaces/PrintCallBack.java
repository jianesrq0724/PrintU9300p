package com.ecaray.printlib.interfaces;

/**
 * 类描述: 打印回调类，返回成功或者失败
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/15 21:23
 */
public interface PrintCallBack {

    //无纸
    int NO_PAPER = 1;
    //错误
    int ERROR = 2;

    /**
     * 成功
     */
    void success();

    /**
     * 失败
     * @param errorCode 错误的Code
     */
    void fail(int errorCode);
}
