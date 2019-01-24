package com.ecaray.printlib.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.ecaray.printlib.PrinterModule;
import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.interfaces.PrintCallBack;
import com.ecaray.printlib.util.LogUtils;
import com.ecaray.printlib.utils.PrinterAPI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/14 17:27
 */
public class Print4OneHelper{

    public static Print4OneHelper mPrintHelper = null;
    private PrinterAPI mPrinter;
    private Context mContext;
    private int printTimes;//打印次数 第一次打印头是0  打印内容是1
    //打印列表
    private List<PrintBean> mPrintList = new ArrayList<>();
    //打印回调
    private PrintCallBack mPrintCallBack;

    public Print4OneHelper(Context context, List<PrintBean> printList, PrintCallBack printCallBack) {
        mContext = context;
        mPrintList = printList;
        mPrintCallBack = printCallBack;
        mPrinter = new PrinterAPI();
    }

    public static Print4OneHelper getInstance(Context context, List<PrintBean> printList, PrintCallBack printCallBack){
        if(mPrintHelper == null){
            synchronized (Print4OneHelper.class){
                mPrintHelper = new Print4OneHelper(context, printList,printCallBack);
            }
        }
        return mPrintHelper;
    }

    public void startPrint(){
        printTimes=0;
        mPrinter.openPrint();
        mPrinter.initPrint();
        printData();//打印数据
    }

    private void printData(){
        if (printTimes >= mPrintList.size()) {
            return;
        }
        if ( mPrintList != null && mPrintList.size()>0) {
            final PrintBean mPrintBean = mPrintList.get(printTimes);
            if (mPrintBean != null) {
                switch (mPrintBean.position) {
                    case PrintBean.LEFT:
                        mPrinter.setAlighType(PrinterModule.POSITION_LEFT);//居左
                        break;
                    case PrintBean.CENTER:
                        mPrinter.setAlighType(PrinterModule.POSITION_CENTER);//居中
                        /**
                         * 参数：是否，倍宽  倍高  加粗，加虚线
                         */
//                        mPrinter.setKGCU(true,true,false,false);
                        break;
                    case PrintBean.RIGHT:
                        mPrinter.setAlighType(PrinterModule.POSITION_RIGHT);//居右
                        break;

                    default:
                        break;
                }
                switch (mPrintBean.size) {
                    case PrintBean.BIG:

                        break;
                    case PrintBean.NORMAL:

                        break;
                    case PrintBean.SMALL:
                        break;
                    default:
                        break;
                }

                switch (mPrintBean.contentType){
                    case PrintBean.TWO_DIMENSION:
//                        mPrinter.printQrcode(mPrintBean.content,2);
                        printBarCode(mPrintBean);
                        break;
                    default:
                        printContent(mPrintBean);
                        break;
                }

            }


        }
    }

    private void printBarCode(PrintBean printBean){
        Bitmap lBitmap = null;
        try {
            lBitmap = mPrinter.createQRCode(printBean.content, BarcodeFormat.QR_CODE, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mPrinter.printPic(lBitmap, new PrinterAPI.printerStatusListener() {
            @Override
            public void work() {
            }

            @Override
            public void hot() {
                mPrintCallBack.fail(PrinterModule.PRINT_ERROR);
                printTimes=0;
            }

            @Override
            public void noPaper() {
                mPrintCallBack.fail(PrinterModule.PRINT_NO_PAPER);
                printTimes=0;
            }

            @Override
            public void end() {
                mPrintCallBack.success();
                printTimes++;
                printData();
                LogUtils.i("打印完成");
            }
        });
    }

    private void printContent(PrintBean printBean){
        mPrinter.printPaper(printBean.content, new PrinterAPI.printerStatusListener() {
            @Override
            public void work() {
            }

            @Override
            public void hot() {
                mPrintCallBack.fail(PrinterModule.PRINT_ERROR);
                printTimes=0;
            }

            @Override
            public void noPaper() {
                mPrintCallBack.fail(PrinterModule.PRINT_NO_PAPER);
                printTimes=0;
            }

            @Override
            public void end() {
                mPrintCallBack.success();
                printTimes++;
                printData();
                LogUtils.i("打印完成");
            }
        });
    }

}

