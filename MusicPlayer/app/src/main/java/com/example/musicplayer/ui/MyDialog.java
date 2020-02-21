package com.example.musicplayer.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;

import java.util.List;

public class MyDialog extends Dialog {

    private int layoutRes;// 布局文件
    Context context;

    public MyDialog(Context context) {
        super(context);
        this.context = context;
    }
    public MyDialog(Context context, int layoutRes) {
        super(context);
        this.context = context;
        this.layoutRes = layoutRes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
    }

}
