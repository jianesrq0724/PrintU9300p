package com.ecaray.printlib.scanner.helper;

import android.content.Context;

import com.landicorp.android.eptapi.device.InnerScanner;

/**
 * This sample show that how to use inner scanner
 * @author chenwei
 *
 */
public class ScannerHelper extends com.ecaray.printlib.scanner.helper.AbstractHelper {

    private InnerScanner scanner;
    private Context mContext;

    public ScannerHelper(Context context,InnerScanner.OnScanListener listener) {
        super(context);
        mContext = context;
        scanner = InnerScanner.getInstance();
        scanner.setOnScanListener(listener);
    }

    /**
     * Start to scan.
     */
    public void start() {
        scanner.setParameter(new byte[]{0x02, 0x16, 0x06});
        scanner.start(20);
    }

    /**
     * Stop scanning.
     */
    public void stop() {
        scanner.stop();
    }

    /**
     * Inner scanner object is singlton. So the listener must be release in the
     * right time.
     */
    public void stopListen() {
        scanner.stopListen();
    }

}
