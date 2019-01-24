package com.ecaray.printlib.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ecaray.printlib.R;
import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.interfaces.PrintCallBack;
import com.ecaray.printlib.util.ToastUtils;
import com.ecaray.printlib.utils.BitmapUtil;
import com.imagpay.Settings;
import com.imagpay.enums.PosModel;
import com.imagpay.mpos.MposHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.bitmap;
import static com.imagpay.Settings.MPOS_PRINT_ALIGN_CENTER;
import static com.imagpay.Settings.MPOS_PRINT_ALIGN_LEFT;
import static com.imagpay.Settings.MPOS_PRINT_ALIGN_RIGHT;

/**
 * 类描述:
 * 创建人: Eric_Huang
 * 创建时间: 2018/3/8 14:40
 */
public class Print4OneHelper {

    private static Print4OneHelper mPrintHelper = null;
    private static Context mContext;
    private final MposHandler mHandler;
    private final Settings mSettings;
    //打印列表
    private List<PrintBean> mPrintList = new ArrayList<>();
    //打印回调
    private PrintCallBack mPrintCallBack;

    public Print4OneHelper(Context context, List<PrintBean> printList, PrintCallBack printCallBack) {
        mContext = context;
        mPrintList = printList;
        mPrintCallBack = printCallBack;

        //打印机初始化
        mHandler = MposHandler.getInstance(context, PosModel.Z90);
        mHandler.setShowLog(true);
        mSettings = Settings.getInstance(mHandler);
        mSettings.mPosPowerOn();
    }

    public static Print4OneHelper getInstance(Context context, List<PrintBean> printList, PrintCallBack printCallBack) {
        if (mPrintHelper == null) {
            synchronized (Print4OneHelper.class) {
                mPrintHelper = new Print4OneHelper(context, printList, printCallBack);
            }
        }
        return mPrintHelper;
    }

    /**
     * 开始打印
     */
    public void startPrint(){
        for (PrintBean mPrintBean : mPrintList) {
            printData(mPrintBean);//执行打印数据。
        }
    }

    private void printData(PrintBean mPrintBean) {
        if (mPrintBean != null){
            //重置打印设置
            mSettings.mPosEnterPrint();
            //设置打印位置
            switch (mPrintBean.position){
                case PrintBean.CENTER:
                    mSettings.mPosPrintAlign(MPOS_PRINT_ALIGN_CENTER);
                    break;
                case PrintBean.LEFT:
                    mSettings.mPosPrintAlign(MPOS_PRINT_ALIGN_LEFT);
                    break;
                case PrintBean.RIGHT:
                    mSettings.mPosPrintAlign(MPOS_PRINT_ALIGN_RIGHT);
                    break;
            }
            //设置打印字体大小
            switch (mPrintBean.size) {
                case PrintBean.BIG:
                    mSettings.mPosPrintTextSize(Settings.MPOS_PRINT_FONT_DEFAULT);
                    break;
                case PrintBean.NORMAL:
                    mSettings.mPosPrintTextSize(Settings.MPOS_PRINT_TEXT_NORMAL);
                    break;
                case PrintBean.SMALL:
                    mSettings.mPosPrintTextSize(Settings.MPOS_PRINT_FONT_DEFAULT);
                    break;
                default:
                    ToastUtils.showShort(mContext, "字体出错");
                    break;
            }

            switch (mPrintBean.contentType){
                case PrintBean.TWO_DIMENSION:
                    Bitmap bitmap = BitmapUtil.create2DCoderBitmap(mPrintBean.content, 300,300);
                    mSettings.mPosPrnImg(bitmap);

                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    bitmap = null;
                    break;
                default:
                    mSettings.mPosPrnStr(mPrintBean.content);
                    break;
            }

        }

    }


}

