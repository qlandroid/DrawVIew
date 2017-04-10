package com.example.mrqiu.drawapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.example.mrqiu.drawapp.widget.ColorDialog;
import com.example.mrqiu.drawapp.widget.DrawView;


public class MainActivity extends AppCompatActivity {

    private SeekBar sb;
    private DrawView dv;
    ColorDialog cd ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb = (SeekBar) findViewById(R.id.sb);
        dv = (DrawView) findViewById(R.id.dv);
        sb.setMax(6);
        cd = new ColorDialog(this);
        cd.setmOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void clickTrue(int params) {
                dv.setPenColor(params);
            }
        });

    }


    public void clickEraser(View view) {
        dv.setMode(DrawView.MODE_ERASER);
    }

    public void clickPen(View view) {
        dv.setMode(DrawView.MODE_PEN);
    }

    public void clickPenColor(View view) {
        cd.show();
    }
}
