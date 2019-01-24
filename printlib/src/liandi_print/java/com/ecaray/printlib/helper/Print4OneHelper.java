package com.ecaray.printlib.helper;

import android.content.Context;

import com.ecaray.printlib.R;
import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.interfaces.PrintCallBack;
import com.ecaray.printlib.util.LogUtils;
import com.ecaray.printlib.util.ToastUtils;
import com.ecaray.printlib.utils.DeviceBindServiceHelper;
import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.QrCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2017/3/14 17:27
 */
public class Print4OneHelper {

    private static Print4OneHelper mPrintHelper = null;
    private Context mContext;
    //打印列表
    private List<PrintBean> mPrintList = new ArrayList<>();
    //打印回调
    private PrintCallBack mPrintCallBack;

    public Print4OneHelper(Context context, List<PrintBean> printList) {
        mContext = context;
        mPrintList = printList;

        progress.addStep(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                // true：超过一行自动截取；false:超过一行不截取
                printer.setAutoTrunc(false);
                // Default mode is real mode, now set it to virtual mode.
                printer.setMode(Printer.MODE_VIRTUAL);
            }
        });
    }

    public static Print4OneHelper getInstance(Context context, List<PrintBean> printList, PrintCallBack printCallBack) {
        if (mPrintHelper == null) {
            synchronized (Printer.class) {
                mPrintHelper = new Print4OneHelper(context, printList);
            }
        }
        return mPrintHelper;
    }

    /**
     * Make a print progress to design the receipt.
     */
    private Printer.Progress progress = new Printer.Progress() {
        @Override
        public void doPrint(Printer printer) throws Exception {

            for (PrintBean lPrintBean : mPrintList) {
                switch (lPrintBean.contentType) {
                    case PrintBean.TXT:             //文本
                        printer.setFormat(setFrontSize(lPrintBean.size));
                        if (lPrintBean.position == PrintBean.CENTER) {
                            printer.printMid(lPrintBean.content.concat("\n"));
                        } else {
                            printer.printText(lPrintBean.content.concat("\n"));
                        }
                        break;
                    case PrintBean.ONE_DIMENSION:   //一维码
                        printer.printBarCode(30, 10, lPrintBean.content);
                        break;
                    case PrintBean.TWO_DIMENSION:
                        //二维码
                        QrCode lQrCode = new QrCode(lPrintBean.content, 1);
                        printer.printQrCode(20, lQrCode, 350);
                        break;
                    case PrintBean.DOT_LINE:        //虚线
                        String dot_line = "--------------------------------";
                        lPrintBean.content = dot_line;
                        printer.setFormat(setFrontSize(PrintBean.NORMAL));
                        printer.printMid(lPrintBean.content.concat("\n"));
                        break;
                    default:
                        break;
                }
            }
            //空行
            printer.feedLine(6);
        }

        @Override
        public void onCrash() {
            DeviceBindServiceHelper.getInstance(mContext).bindDeviceService();
        }

        @Override
        public void onFinish(int code) {
            /**
             * The result is fine.
             */
            if (code == Printer.ERROR_NONE) {
                LogUtils.e("正常状态，无错误");
            } else {
                LogUtils.e("PRINT ERR - " + getErrorDescription(code));
                ToastUtils.showShort(mContext, getErrorToast(code));
            }

            DeviceBindServiceHelper.getInstance(mContext).unbindDeviceService();
        }

        public String getErrorDescription(int code) {
            switch (code) {
                case Printer.ERROR_PAPERENDED:
                    return "缺纸，不能打印";
                case Printer.ERROR_HARDERR:
                    return "硬件错误";
                case Printer.ERROR_OVERHEAT:
                    return "打印头过热";
                case Printer.ERROR_BUFOVERFLOW:
                    return "缓冲模式下所操作的位置超出范围";
                case Printer.ERROR_LOWVOL:
                    return "低压保护";
                case Printer.ERROR_PAPERENDING:
                    return "纸张将要用尽，还允许打印 (单步进针打特有返回值)";
                case Printer.ERROR_MOTORERR:
                    return "打印机芯故障(过快或者过慢)";
                case Printer.ERROR_PENOFOUND:
                    return "自动定位没有找到对齐位置,纸张回到原来位置";
                case Printer.ERROR_PAPERJAM:
                    return "卡纸";
                case Printer.ERROR_NOBM:
                    return "没有找到黑标";
                case Printer.ERROR_BUSY:
                    return "打印机处于忙状态";
                case Printer.ERROR_BMBLACK:
                    return "黑标探测器检测到黑色信号";
                case Printer.ERROR_WORKON:
                    return "打印机电源处于打开状态";
                case Printer.ERROR_LIFTHEAD:
                    return "打印头抬起 (自助热敏打印机特有返回值)";
                case Printer.ERROR_LOWTEMP:
                    return "低温保护或AD出错 (自助热敏打印机特有返回值)";
                case Printer.ERROR_COMMERR:
                    return "手座机状态正常，但通讯失败 (520针打特有返回值)";
                case Printer.ERROR_CUTPOSITIONERR:
                    return "切纸刀不在原位 (自助热敏打印机特有返回值)";
                case Printer.MODE_REAL:
                    return "以实模式的方式来使用打印机(默认)";
                case Printer.MODE_VIRTUAL:
                    return "以虚模式的方式来使用打印机";
                default:
                    break;
            }

            return "unknown error (" + code + ")";
        }

        public String getErrorToast(int code) {
            switch (code) {
                case Printer.ERROR_PAPERENDED:
                    return mContext.getString(R.string.printer_out_of_paper);
                case Printer.ERROR_OVERHEAT:
                    return "打印头过热";
                case Printer.ERROR_PENOFOUND:
                    return "自动定位没有找到对齐位置,纸张回到原来位置";
                case Printer.ERROR_PAPERJAM:
                    return "卡纸";
            }
            return "打印机出现故障";
        }
    };

    public void startPrint() {
        try {
            DeviceBindServiceHelper.getInstance(mContext).bindDeviceService();
            progress.start();
        } catch (RequestException e) {
//            onDeviceServiceCrash();
            e.printStackTrace();
        }
    }


    /**
     * 设置打印字体大小
     *
     * @param size 字体大小
     */
    private Printer.Format setFrontSize(int size) {
        Printer.Format format = new Printer.Format();
        switch (size) {
            case PrintBean.SMALL:
                format.setHzScale(Printer.Format.HZ_SC1x1);
                format.setHzSize(Printer.Format.HzSize.DOT16x16);
                format.setAscScale(Printer.Format.ASC_SC1x1);
                format.setAscSize(Printer.Format.AscSize.DOT16x8);
                break;
            case PrintBean.NORMAL:
                format.setHzScale(Printer.Format.HZ_SC1x1);
                format.setHzSize(Printer.Format.HzSize.DOT24x24);
                format.setAscScale(Printer.Format.ASC_SC1x1);
                format.setAscSize(Printer.Format.AscSize.DOT24x12);
                break;
            case PrintBean.BIG:
                format.setHzScale(Printer.Format.HZ_SC2x2);
                format.setHzSize(Printer.Format.HzSize.DOT16x16);
                format.setAscScale(Printer.Format.ASC_SC2x2);
                format.setAscSize(Printer.Format.AscSize.DOT16x8);
                break;
            default:
                break;
        }
        return format;
    }

}

