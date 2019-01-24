package com.ecaray.printlib.helper;

import android.content.Context;
import android.util.Printer;

import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.interfaces.PrintCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/14 17:27
 */
public class Print4OneHelper{

    private static Print4OneHelper mPrintHelper = null;
    private Context mContext;
    //打印列表
    private List<PrintBean> mPrintList = new ArrayList<>();
    //打印回调
    private PrintCallBack mPrintCallBack;

    public Print4OneHelper(Context context, List<PrintBean> printList) {
        mContext = context;
        mPrintList = printList;
    }

    public static Print4OneHelper getInstance(Context context, List<PrintBean> printList, PrintCallBack printCallBack){
        if(mPrintHelper == null){
            synchronized (Printer.class){
                mPrintHelper = new Print4OneHelper(context, printList);

            }
        }
        return mPrintHelper;
    }

    public void startPrint() {

    }

}

