package com.ecaray.printlib;

import android.content.Context;

import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.helper.Print4OneHelper;
import com.ecaray.printlib.interfaces.PrintCallBack;

import java.util.List;
import java.util.Objects;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/14 15:27
 */
public class PrinterModule {
    //打印缺纸
    public static final int PRINT_NO_PAPER = 0;
    //打印出错
    public static final int PRINT_ERROR = 1;

    //思必拓设备打印成功
    public static final int SIBITUO_PRINT_ERROR = 0;
    //思必拓设备打印成功
    public static final int SIBITUO_PRINT_SUCCESS = 1;
    /**
     * 打印时的位置   0.靠左对齐
     *               1.居中对齐
     *               2.靠右对齐
     */
    public static final int POSITION_LEFT = 0;
    public static final int POSITION_CENTER = 1;
    public static final int POSITION_RIGHT = 2;

    /**
     * 打印的字体大小 0.最大
     *               1.中等
     *               2.最小
     */
    public static final int FONT_BIG = 0;
    public static final int FONT_NORMAL = 1;
    public static final int FONT_SMALL = 2;


    private static PrinterModule mPrinterModule = null;

    public PrinterModule() {}

    public static PrinterModule getInstance(){
        if(mPrinterModule == null){
            synchronized (PrinterModule.class){
                mPrinterModule = new PrinterModule();
            }
        }
        return mPrinterModule;
    }

    /**
     * 一体机打印
     */
    public void print4One(Context context, List<PrintBean> printList, PrintCallBack printCallBack){
        Print4OneHelper lPrint4OneHelper = Print4OneHelper.getInstance(context.getApplicationContext(), printList, printCallBack);
        lPrint4OneHelper.startPrint();
    }

    /**
     * 分体机打印
     * @param printList     打印数据列表
     */
    public void print(Context context, List<Objects> printList, PrintCallBack printCallBack){

    }

}
