package com.ecaray.printlib.helper;

import android.content.Context;

import com.ecaray.printlib.PrinterModule;
import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.interfaces.PrintCallBack;
import com.ecaray.printlib.util.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rego.printlib.export.regoPrinter;
import util.DeviceControl;
import util.preDefiniation;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/14 17:27
 */
public class Print4OneHelper{
    private static final int ERROR = 2;//打印发生错误
    private static Print4OneHelper mPrintHelper = null;
    private Context mContext;
    //打印列表
    private List<PrintBean> mPrintList = new ArrayList<>();
    //打印回调
    private PrintCallBack mPrintCallBack;
    private regoPrinter mRegoPrinter;
    private int state;
    private DeviceControl DevCtrl;
    private boolean isTT43 = false;

    public Print4OneHelper(Context context, List<PrintBean> printList, PrintCallBack printCallBack) {
        mContext = context;
        mPrintList = printList;
        mPrintCallBack = printCallBack;
        if (mRegoPrinter == null) {
            mRegoPrinter = new regoPrinter(mContext);
        }
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
        modelJudgment();//判断机型并做一系列的初始化工作。
        for (PrintBean mPrintBean : mPrintList) {
            printData(mPrintBean);//执行打印数据。
        }
    }

    private void printData(PrintBean mPrintBean) {
        if (mPrintBean != null) {//判断是不是头部信息  如果是  加大字体
            mRegoPrinter.CON_PageStart(state, false, 50, 50);//状态、是否是图形数据，宽，高。
            switch (mPrintBean.position) {
                case PrintBean.LEFT:
                    mRegoPrinter.ASCII_CtrlAlignType(state,0);//状态和位置 0左侧 1中间 2 右侧
                    break;
                case PrintBean.CENTER:
                    mRegoPrinter.ASCII_CtrlAlignType(state,1);//状态和位置 0左侧 1中间 2 右侧
                    break;
                case PrintBean.RIGHT:
                    mRegoPrinter.ASCII_CtrlAlignType(state,2);//状态和位置 0左侧 1中间 2 右侧
                    break;
                default:
                    break;
            }
            mRegoPrinter.ASCII_CtrlSetLineSpace(state,40);//设计一行的高度
            mRegoPrinter.ASCII_CtrlOppositeColor(state, false);//是否反色打印
            setLevel(6);//设置字体颜色黑色程度 0-29;
            switch (mPrintBean.contentType) {
                case PrintBean.TWO_DIMENSION:
                    /**
                     *      objCode - 某个打开的端口对象
                     *      type2D - 二维条码类型 BarcodeType.getValue()
                     *      strPrint - 二维码字符串
                     *      version - 条码版本
                     *      ecc - 纠错等级
                     *      size - 条码大小
                     */
                    mRegoPrinter.ASCII_Print2DBarcode(state, preDefiniation.BarcodeType.BT_QRcode.getValue(),mPrintBean.content,8,76,4);
                    break;
                case PrintBean.TXT:
                    //1、状态 2、是否倍宽 3、是否背高 4、是否加粗 5、是否加下划线 6、是否小字体 7、具体打印的文本 8、编码类型
                    switch (mPrintBean.size) {
                        case PrintBean.BIG:
                            mRegoPrinter.ASCII_PrintString(state, 0, 0, 1, 0, 0,mPrintBean.content, "gb2312");
                            break;
                        case PrintBean.NORMAL:
                            mRegoPrinter.ASCII_PrintString(state, 0, 0, 0, 0, 0,mPrintBean.content, "gb2312");
                            break;
                        case PrintBean.SMALL:
                            mRegoPrinter.ASCII_PrintString(state, 0, 0, 0, 0, 1,mPrintBean.content, "gb2312");
                            break;
                        case PrintBean.LINE_NORMAL:
                            mRegoPrinter.ASCII_PrintString(state, 0, 0, 0, 1, 0,mPrintBean.content, "gb2312");
                            break;
                        default:
                            break;
                    }
                    break;

                default:
                    break;
            }


            mRegoPrinter.ASCII_CtrlFeedLines(state,0);// state 和进纸行数  发送进纸指令
            mRegoPrinter.ASCII_CtrlPrintCRLF(state,0);//state和换几行  发送打印换行符
            //    结束打印内容填充，开始打印数据 并且返回打印状态  0打印失败 1打印成功
            int printStatus =  mRegoPrinter.CON_PageEnd(state,preDefiniation.TransferMode.getEnum(0).getValue());
            switch (printStatus) {
                case PrinterModule.SIBITUO_PRINT_ERROR:
                    mPrintCallBack.fail(ERROR);
                    break;
                case PrinterModule.SIBITUO_PRINT_SUCCESS:
                    mPrintCallBack.success();
                    break;

                default:
                    break;
            }
        }
    }

    private void modelJudgment() {
        // 默认45 45和45Q的打印机串口相同 gpio不同
        state =mRegoPrinter.CON_ConnectDevices("RG-E487",
                "/dev/ttyMT1:115200", 200);
        // kt45
        if (android.os.Build.VERSION.RELEASE.equals("4.4.2")) {
            DevCtrl = new DeviceControl(DeviceControl.powerPathKT);
            isTT43 = false;
            try {
                DevCtrl.PowerOnMTDevice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 45q 设置gpio
        else if (android.os.Build.VERSION.RELEASE.equals("5.1")) {
            DevCtrl = new DeviceControl(DeviceControl.powerPathKT);
            DevCtrl.setGpio(94);
            isTT43 = false;
            try {
                DevCtrl.PowerOnMTDevice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // // TT43
        else if (android.os.Build.VERSION.RELEASE.equals("4.0.3")) {
            // /dev/ttyG1:115200
            state = mRegoPrinter.CON_ConnectDevices("RG-E487",
                    "/dev/ttyG1:115200", 200);
            DevCtrl = new DeviceControl(DeviceControl.powerPathTT);
            isTT43 = true;
            try {
                DevCtrl.PowerOnDevice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i("state:"+state);
    }

    // 设置灰度
    private void setLevel(int level) {
        // TODO Auto-generated method stub
        byte[] setCmd = new byte[7];
        setCmd[0] = 0x1b;
        setCmd[1] = 0x06;
        setCmd[2] = 0x1b;
        setCmd[3] = (byte) 0xfd;
        setCmd[4] = (byte) level;// (level - 1);
        setCmd[5] = 0x1b;
        setCmd[6] = 0x16;
        mRegoPrinter.ASCII_PrintBuffer(state, setCmd,
                setCmd.length);
        System.out.println("setOk");
    }

}

