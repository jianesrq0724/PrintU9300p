package com.carl.printu9300p;

import android.os.Bundle;
import android.posapi.PosApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.carl.printu9300p.utils.PowerUtil;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "button", Toast.LENGTH_SHORT).show();

                initDev();

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < 10; i++) {
                    sb.append("管理单位1：滁州智慧停车");
                    sb.append("\n");
                    sb.append("管理单位2：滁州智慧停车");
                    sb.append("\n");
                    sb.append("管理单位3：滁州智慧停车");
                    sb.append("\n");
                }
                sb.append("\n\n\n\n\n");
                mApi.printText(60, toBytes(sb.toString()), toBytes(sb.toString()).length);

//                destryDevice();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDev();

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < 10; i++) {
                    sb.append("管理单位1：滁州智慧停车");
                    sb.append("\n");
                    sb.append("管理单位2：滁州智慧停车");
                    sb.append("\n");
                    sb.append("管理单位3：滁州智慧停车");
                    sb.append("\n");
                }
                sb.append("\n\n\n\n\n");
                mApi.printText(60, toBytes(sb.toString()), toBytes(sb.toString()).length);

                Toast.makeText(MainActivity.this, "button2", Toast.LENGTH_SHORT).show();
            }
        });
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

    private PosApi mApi = null;

    private void initDev() {
        //Power on
        PowerUtil.power("1");

        //init
        mApi = PosApi.getInstance(this);
        mApi.setOnComEventListener(mCommEventListener);
        //get interface
        mApi.initDeviceEx("/dev/ttyMT2");
    }

    private void destryDevice() {
        if (mApi != null) {
            mApi.closeDev();
        }
        PowerUtil.power("0");
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
//        destryDevice();
    }
}
