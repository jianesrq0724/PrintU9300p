package com.ecaray.printlib.helper;

import android.content.Context;
import android.posapi.PosApi;

import com.ecaray.printlib.utils.PowerUtil;

/**
 * @author Carl
 *         version 1.0
 * @since 2019/1/24
 */
public class PrintRegisterHelper {

    private static PrintRegisterHelper mPrintRegisterHelper;

    private PosApi mApi = null;
    private Context mContext;

    public static PrintRegisterHelper getInstance(Context context) {
        if (mPrintRegisterHelper == null) {
            synchronized (PrintRegisterHelper.class) {
                mPrintRegisterHelper = new PrintRegisterHelper(context);
            }
        }
        return mPrintRegisterHelper;
    }

    private PrintRegisterHelper(Context context) {
        mContext = context;
    }


    public void initDev() {
        //Power on
        PowerUtil.power("1");
        //init
        mApi = PosApi.getInstance(mContext);
        mApi.initDeviceEx("/dev/ttyMT2");
    }


    public void destryDevice() {
        if (mApi != null) {
            mApi.closeDev();
        }
        PowerUtil.power("0");
    }



}
