package com.example.mrqiu.drawapp.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.example.mrqiu.drawapp.OnDialogClickListener;
import com.example.mrqiu.drawapp.R;

/**
 * Created by mrqiu on 2017/4/3.
 */

public class ColorDialog {

    private int a, b, c, d;
    private View vColor;
    private OnDialogClickListener mOnDialogClickListener;
    private int color;
    private AlertDialog ad;

    public ColorDialog(Context context) {
        init(context);
    }

    public OnDialogClickListener getOnDialogClickListener() {
        return mOnDialogClickListener;
    }

    public void setmOnDialogClickListener(OnDialogClickListener mOnDialogClickListener) {
        this.mOnDialogClickListener = mOnDialogClickListener;
    }

    public void init(Context context) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_color, null, false);
        SeekBar sb0 = (SeekBar) dialogView.findViewById(R.id.dialog_sb0);
        SeekBar sb1 = (SeekBar) dialogView.findViewById(R.id.dialog_sb1);
        SeekBar sb2 = (SeekBar) dialogView.findViewById(R.id.dialog_sb2);
        SeekBar sb3 = (SeekBar) dialogView.findViewById(R.id.dialog_sb3);

        vColor = dialogView.findViewById(R.id.dialog_v_color);

        sb0.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                a = progress;
                vColor.setBackgroundColor(color = Color.argb(a, b, c, d));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                b = progress;
                vColor.setBackgroundColor(color = Color.argb(a, b, c, d));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                c = progress;
                vColor.setBackgroundColor(color = Color.argb(a, b, c, d));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                d = progress;
                vColor.setBackgroundColor(color = Color.argb(a, b, c, d));
                
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ad = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnDialogClickListener.clickTrue(color);
                    }
                })
                .create();

    }

    public void show(){
        ad.show();
    }
}
