package com.carl.printu9300p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.posapi.PosApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.carl.printu9300p.utils.PowerUtil;
import com.ecaray.printlib.entity.PrintBean;
import com.ecaray.printlib.helper.Print4OneHelper;
import com.ecaray.printlib.helper.PrintRegisterHelper;
import com.ecaray.printlib.util.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Looper mSendLooper = null;
    private Handler mSendHandler = null;
    private static final int MSG_PRINT_NEXT = 11;
    //    ControlThread mControlThread;
    private Context mContext;
    //    private PosApi mApi = null;
    byte[] bytes2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
//        initDev();
        init();

        PrintRegisterHelper.getInstance(mContext).initDev();


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<PrintBean> printList = new ArrayList<>();

                for (int i = 0; i < 2; i++) {
                    printList.add(new PrintBean(getData()));
                }

                Print4OneHelper lPrint4OneHelper = Print4OneHelper.getInstance(MainActivity.this, printList, null);
                lPrint4OneHelper.startPrint();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initDev();
                for (int i = 0; i < 3; i++) {
                    bytes2 = toBytes(getData());
                    mSendHandler.sendEmptyMessage(MSG_PRINT_NEXT);
                }
            }
        });
    }


//    private class ControlThread extends Thread {
//        @Override
//        public void run() {
//            Looper.prepare();
//            mSendLooper = Looper.myLooper();
//            mSendHandler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    switch (msg.what) {
//
//                        case MSG_PRINT_NEXT:
//                            if (bytes2 != null && bytes2.length > 0) {
//                                mApi.printText(60, bytes2, bytes2.length);
//                            }
//                            break;
//
//                        default:
//                            break;
//                    }
//                }
//            };
//            //mSendHandler.sendMessage(mSendHandler.obtainMessage(MSG_CONNECT_SERVER));
//            Looper.loop();
//
//        }
//    }

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
                            Toast.makeText(mContext, "noPage", Toast.LENGTH_SHORT).show();
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
        if (bytes2 != null) {
            bytes2 = null;
        }
        mSendHandler.sendEmptyMessage(MSG_PRINT_NEXT);
    }

    public void printStop(int state) {
        mSendHandler.removeMessages(MSG_PRINT_NEXT);
    }


    public void init() {
//        mControlThread = new ControlThread();
//        mControlThread.start();

//        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
//        mContext.registerReceiver(mReceiver, mFilter);
    }


    public void close() {
        if (mContext != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        mSendHandler.removeMessages(MSG_PRINT_NEXT);
//        if (mControlThread != null) {
//            mSendLooper.quit();
//            mSendHandler = null;
//            mControlThread = null;
//        }
    }

    private String getData() {

        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            sb2.append("管理单位1：滁州智慧停车" + i);
            sb2.append("\n");
        }
        sb2.append("\n\n\n");
        return sb2.toString();
    }

    private byte[] toBytes(String content) {
        byte[] text = null;
        try {
            text = content.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    PosApi.OnCommEventListener mCommEventListener = new PosApi.OnCommEventListener() {

        @Override
        public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
            // TODO Auto-generated method stub
            switch (cmdFlag) {
                case PosApi.POS_INIT:
                    if (state == PosApi.COMM_STATUS_SUCCESS) {

                    } else {
                        Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrintRegisterHelper.getInstance(mContext).destryDevice();
//        close();
    }
}
