package com.ecaray.printlib.utils;

import android.content.Context;
import android.os.Handler;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;

/**
 * 类描述: 合肥一体机服务绑定帮助类
 * 创建人: Shirley
 * 创建日期: 2017/1/14 15:11
 * 修改人:Shirley
 * 修改时间: 2017/1/14 15:11
 * 修改备注:
 */
public class DeviceBindServiceHelper {

    private Context mContext;

    public static DeviceBindServiceHelper sDeviceBindServiceHelper;

    private Handler handler = new Handler();

    public DeviceBindServiceHelper(Context context) {
        mContext = context;
    }

    public static synchronized DeviceBindServiceHelper getInstance(Context context) {
        if(sDeviceBindServiceHelper == null){
            sDeviceBindServiceHelper = new DeviceBindServiceHelper(context.getApplicationContext());
        }
        return sDeviceBindServiceHelper;
    }

    /**
     * Run something on ui thread after milliseconds.
     */
    public void runOnUiThreadDelayed(Runnable r, int delayMillis) {
        handler.postDelayed(r, delayMillis);
    }

    /**
     * To gain control of the device service,
     * you need invoke this method before any device operation.
     */
    public void bindDeviceService() {
        try {
            DeviceService.login(mContext);
        } catch (RequestException e) {
            // Rebind after a few milliseconds,
            // If you want this application keep the right of the device service
//            runOnUiThreadDelayed(() -> bindDeviceService(), 300);
            runOnUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    bindDeviceService();
                }
            }, 300);
            e.printStackTrace();
        } catch (ServiceOccupiedException | ReloginException | UnsupportMultiProcess e) {
            e.printStackTrace();
        }
    }

    /**
     * Release the right of using the device.
     */
    public void unbindDeviceService() {
        DeviceService.logout();
    }

    /**
     * Get handler in the ui thread
     */
    public Handler getUIHandler() {
        return handler;
    }
}
