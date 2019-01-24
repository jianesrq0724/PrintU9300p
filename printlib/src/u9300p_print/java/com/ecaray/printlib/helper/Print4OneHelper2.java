package com.ecaray.printlib.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.posapi.PosApi;
import android.text.TextUtils;
import android.widget.Toast;

import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.interfaces.PrintCallBack;
import com.ecaray.printlib.util.LogUtils;
import com.ecaray.printlib.util.ToastUtils;
import com.ecaray.printlib.utils.BarcodeCreater;
import com.ecaray.printlib.utils.BitmapTools;
import com.ecaray.printlib.utils.PowerUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/14 17:27
 */
public class Print4OneHelper2 {

    private static Print4OneHelper2 mPrintHelper = null;
    private static Context mContext;
    //打印列表
    private List<PrintBean> mPrintList = new ArrayList<>();
    //打印回调
    private PrintCallBack mPrintCallBack;
    //打印对象
    private Looper mSendLooper = null;
    private Handler mSendHandler = null;
    private static final int MSG_PRINT_NEXT = 11;
    ControlThread mControlThread;
    private PosApi mApi = null;

    /**
     * 浓度：25-60之间
     */
    private static final int concentration = 60;

    /**
     * handle开始打印
     */
    private static final int MESSAGE_START_PRINT = 0x01;
    /**
     * handle没有纸
     */
    private static final int MESSAGE_NOT_PAGE = 0x02;

    public Print4OneHelper2(Context context, List<PrintBean> printList) {
        mContext = context;
        mPrintList = printList;


//        initDev();
    }

    private class ControlThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            mSendLooper = Looper.myLooper();
            mSendHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {

                        case MSG_PRINT_NEXT:
                            dealData();
                            break;

                        default:
                            break;
                    }
                }
            };
            //mSendHandler.sendMessage(mSendHandler.obtainMessage(MSG_CONNECT_SERVER));
            Looper.loop();

        }
    }

    public void init() {
        mControlThread = new ControlThread();
        mControlThread.start();

//        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
//        mContext.registerReceiver(mReceiver, mFilter);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equalsIgnoreCase(PosApi.ACTION_POS_COMM_STATUS)) {
                int cmdFlag = intent.getIntExtra(PosApi.KEY_CMD_FLAG, -1);
                int status = intent.getIntExtra(PosApi.KEY_CMD_STATUS, -1);

                if (cmdFlag == PosApi.POS_PRINT_PICTURE || cmdFlag == PosApi.POS_PRINT_TEXT) {

                    switch (status) {
                        case PosApi.ERR_POS_PRINT_SUCC:
                            //Print Success
                            printNext();
                            break;
                        case PosApi.ERR_POS_PRINT_NO_PAPER:
                            //No paper
                            ToastUtils.showCustomShort(mContext.getApplicationContext(), "打印纸已用完,请及时补充");
                            printStop(status);
                            break;
                        case PosApi.ERR_POS_PRINT_FAILED:
                            //Print Failed
                            printStop(status);
                            break;
                        case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
                            //Low Power
                            printStop(status);
                            break;
                        case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
                            //Hight Power
                            printStop(status);
                            break;
                        default:
                            break;
                    }
                }


            }
        }

    };


    public void printNext() {
        if (mPrintList != null) {
            mPrintList.clear();
        }
        mSendHandler.sendEmptyMessage(MSG_PRINT_NEXT);
    }


    public void printStop(int state) {
        mSendHandler.removeMessages(MSG_PRINT_NEXT);
    }


    private void initDev() {
        //Power on
        PowerUtil.power("1");

        //init
        mApi = PosApi.getInstance(mContext);
        mApi.setOnComEventListener(mCommEventListener);
        //get interface
        mApi.initDeviceEx("/dev/ttyMT2");
    }

    public static Print4OneHelper2 getInstance(Context context, List<PrintBean> printList, PrintCallBack printCallBack) {
//        if (mPrintHelper == null) {
//            synchronized (Print4OneHelper.class) {
//                mPrintHelper = new Print4OneHelper(context, printList);
//            }
//        }
//        return mPrintHelper;
        return new Print4OneHelper2(context, printList);
    }


    public void startPrint() {
        init();
        mSendHandler.sendEmptyMessage(MSG_PRINT_NEXT);
    }

    /**
     * 打印分线程
     */
    class PrintThread implements Runnable {
        @Override
        public void run() {
            dealData();
            stopPrint();
        }
    }


    PosApi.OnCommEventListener mCommEventListener = new PosApi.OnCommEventListener() {

        @Override
        public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
            // TODO Auto-generated method stub
            switch (cmdFlag) {
                case PosApi.POS_INIT:
                    if (state == PosApi.COMM_STATUS_SUCCESS) {

                    } else {
                        Toast.makeText(mContext, "Failed to initialize", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 处理数据
     */
    private void dealData() {
        Iterator<PrintBean> iterator = mPrintList.iterator();
        //有数据 并且有纸 开始循环打印
        while (iterator.hasNext()) {
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
                    printImage(lPrintBean.content);
                    break;
                //虚线
                case PrintBean.DOT_LINE:
//                    drawLine();
                    break;
                default:
                    break;
            }
        }

        printPaperRows();

    }

    private void printPaperRows() {

        String message = setPaperSpaceRows(4);

        byte[] text = null;
        try {
            text = message.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mApi.printText(concentration, text, text.length);
    }


    public static String setPaperSpaceRows(int rows) {
        String spaceStr = "";
        for (int i = 0; i < rows; i++) {
            spaceStr += "\n";
        }
        return spaceStr;
    }


    /**
     * 打印方法
     *
     * @param message 数据
     * @param size    字体大小
     */
    private void printText(int position, String message, int size) {
//        mPrinter.setGrayLevel(4);
//        mPrinter.setupPage(384, -1);
        int startPointX = 5;
        //居中打印动态计算开始位置(384 - 文字长度) /2;
//        if (position == PrintBean.CENTER) {
//            startPointX = StringUtils.getStartPointX(message, size);
//        }
//        mPrinter.drawTextEx(message, startPointX, 0, 384, -1, "", size, 0, 0, 0);
//        mPrinter.printPage(0);


        message = message + setPaperSpaceRows(1);

        byte[] text = null;
        try {
            text = message.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mApi.printText(concentration, text, text.length);

        LogUtils.i("开始打印：" + message);
    }


    /**
     * 判断是否 有打印纸
     *
     * @return
     */
    private boolean isHavePrintPage() {
//        if (mPrinter.getStatus() == -1) {
//            printHandle.sendEmptyMessage(MESSAGE_NOT_PAGE);
//            return false;
//        }
        return true;
    }


    /**
     * 打印图像
     *
     * @param content 内容w
     */
    public void printImage(final String content) {
        //生成二维码
        Bitmap mBitmap = BarcodeCreater.encode2dAsBitmap(content, 255, 255, 2);

        byte[] printData = BitmapTools.bitmap2PrinterBytes(mBitmap);

        mApi.printImage(concentration, 0, mBitmap.getWidth(), mBitmap.getHeight(), printData);
    }

    /**
     * 打印虚线
     */
    public void drawLine() {
        String dot_line = "-----------------------------------------";
        byte[] text = null;
        try {
            text = dot_line.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mApi.printText(concentration, text, text.length);
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
//        if (mApi != null) {
//            mApi.closeDev();
//        }
//        PowerUtil.power("0");
    }

}

