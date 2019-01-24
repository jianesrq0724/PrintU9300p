package com.ecaray.printlib.scanner.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public abstract class AbstractHelper {
    private Context context;
    public AbstractHelper(Context context) {
        this.context = context;
    }

    @SuppressLint("ShowToast")
    protected void showNormalMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ShowToast")
    protected void showErrorMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
