package com.ecaray.printlib.helper;

import android.content.Context;
import android.device.PrinterManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.interfaces.PrintCallBack;
import com.ecaray.printlib.util.LogUtils;
import com.ecaray.printlib.util.ToastUtils;
import com.ecaray.printlib.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/14 17:27
 */
public class Print4OneHelper {

    private static Print4OneHelper mPrintHelper = null;
    private static Context mContext;
    //打印列表
    private List<PrintBean> mPrintList = new ArrayList<>();
    //打印回调
    private PrintCallBack mPrintCallBack;
    //打印对象
    private PrinterManager mPrinter;

    /**
     * handle开始打印
     */
    private static final int MESSAGE_START_PRINT = 0x01;
    /**
     * handle没有纸
     */
    private static final int MESSAGE_NOT_PAGE = 0x02;

    public Print4OneHelper(Context context, List<PrintBean> printList) {
        mContext = context;
        mPrintList = printList;
    }

    public static Print4OneHelper getInstance(Context context, List<PrintBean> printList, PrintCallBack printCallBack) {
        if (mPrintHelper == null) {
            synchronized (Print4OneHelper.class) {
                mPrintHelper = new Print4OneHelper(context, printList);
            }
        }
        return mPrintHelper;
    }

    PrintHandle printHandle;

    public void startPrint() {
        printHandle = new PrintHandle();
        PrintThread printThread = new PrintThread();
        Thread thread = new Thread(printThread);
        thread.start();
    }

    /**
     * 打印分线程
     */
    class PrintThread implements Runnable {
        @Override
        public void run() {
            mPrinter = new PrinterManager();
            mPrinter.open();
            dealData();
            stopPrint();
        }
    }

    /**
     * 打印返回主线程
     */
    private static class PrintHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_START_PRINT:
                    ToastUtils.showCustomShort(mContext.getApplicationContext(), "开始打印");
                    break;
                case MESSAGE_NOT_PAGE:
                    ToastUtils.showCustomShort(mContext.getApplicationContext(), "打印纸已用完,请及时补充");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理数据
     */
    private void dealData() {
        Iterator<PrintBean> iterator = mPrintList.iterator();
        //有数据 并且有纸 开始循环打印
        while (iterator.hasNext() && isHavePrintPage()) {
            PrintBean lPrintBean = iterator.next();
            if (TextUtils.isEmpty(lPrintBean.content) && lPrintBean.contentType == PrintBean.TXT) {
                continue;
            }
            switch (lPrintBean.contentType) {
                //文本
                case PrintBean.TXT:
                    printText(lPrintBean.position, lPrintBean.content, setFrontSize(lPrintBean.size));
                    break;
                //二维码
                case PrintBean.TWO_DIMENSION:
                    printImage(lPrintBean.content, lPrintBean.isXuzhou);
                    break;
                //虚线
                case PrintBean.DOT_LINE:
                    drawLine();
                    break;
                default:
                    break;
            }
        }
        mPrinter.paperFeed(10);
        mPrinter.paperFeed(10);
    }

    /**
     * 打印方法
     *
     * @param message 数据
     * @param size    字体大小
     */
    private void printText(int position, String message, int size) {
        mPrinter.setGrayLevel(4);
        mPrinter.setupPage(384, -1);
        int startPointX = 5;
        //居中打印动态计算开始位置(384 - 文字长度) /2;
        if (position == PrintBean.CENTER) {
            startPointX = StringUtils.getStartPointX(message, size);
        }
        mPrinter.drawTextEx(message, startPointX, 0, 384, -1, "", size, 0, 0, 0);
        mPrinter.printPage(0);

        LogUtils.i("开始打印：" + message);
    }


    /**
     * 判断是否 有打印纸
     *
     * @return
     */
    private boolean isHavePrintPage() {
        if (mPrinter.getStatus() == -1) {
            printHandle.sendEmptyMessage(MESSAGE_NOT_PAGE);
            return false;
        }
        return true;
    }

    private void printBarCodeTips(String[] contens, int size) {
        mPrinter.setGrayLevel(4);
        mPrinter.setupPage(384, -1);
        for (int i = 0, len = contens.length; i < len; i++) {
            int x = 5;
            if (i == 1) {
                x = 192;
            }
            mPrinter.drawTextEx(contens[i], x, 0, 384, -1, "", size, 0, 0, 0);
        }
        mPrinter.printPage(0);

    }

    /**
     * 打印图像
     *
     * @param content 内容w
     */
    public void printImage(final String content, boolean isXuzhou) {
        mPrinter.setupPage(384, -1);
        if (isXuzhou) {
            mPrinter.drawBarcode(content, 60, 10, 58, 6, 90, 0);
        } else {
            mPrinter.drawBarcode(content, 60, 10, 58, 6, 90, 0);
        }
//        String[] tips = new String[]{"支付二维码","下载二维码"};
//        printBarCodeTips(tips,20);
//        mPrinter.setupPage(384,192);
//        mPrinter.drawBarcode(content, 0, 10, 58, 4, 192, 0);//mPrinter.drawBarcode(content, 0, 10, 58, 4, 30, 0);


        mPrinter.printPage(0);
    }

    /**
     * 打印虚线
     */
    public void drawLine() {
        mPrinter.setGrayLevel(4);
        String dot_line = "-----------------------------------------";
        mPrinter.setupPage(384, -1);
        mPrinter.drawTextEx(dot_line, 5, 0, 384, -1, "", 30, 0, 0, 0);
        mPrinter.printPage(0);

    }

    private int setFrontSize(int size) {
        switch (size) {
            case PrintBean.SMALL:
                return 16;
            case PrintBean.MIDDLE:
                return 20;
            case PrintBean.NORMAL:
                return 24;
            case PrintBean.BIG:
                return 30;
            default:
                return 22;
        }
    }

    public void stopPrint() {
        if (mPrinter != null) {
            mPrinter.close();
        }
    }

}

