package com.example.mrqiu.drawapp.frame;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.mrqiu.drawapp.OnChangePenColorListener;
import com.example.mrqiu.drawapp.OnFrameBackListener;
import com.example.mrqiu.drawapp.R;

/**
 * Created by mrqiu on 2017/4/6.
 */

public class PenColorFrame extends Fragment {

    private View vColor;
    private SeekBar sb1, sb2, sb3;
    private Button btnBack;

    private int color1, color2, color3;

    private OnChangePenColorListener mOnChangePenColorListener;


    private OnFrameBackListener mOnPenCOlorBackListener;

    public static PenColorFrame newInstance(OnChangePenColorListener l,OnFrameBackListener mBackListener) {

        Bundle args = new Bundle();

        PenColorFrame fragment = new PenColorFrame();
        fragment.setOnChangePenColorListener(l);
        fragment.setOnFrameBackListener(mBackListener);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnFrameBackListener(OnFrameBackListener l){
        this.mOnPenCOlorBackListener = l;
    }

    public void setOnChangePenColorListener(OnChangePenColorListener listener) {
        this.mOnChangePenColorListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_pen_color, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sb1  = (SeekBar) view.findViewById(R.id.sb1);
        sb2  = (SeekBar) view.findViewById(R.id.sb2);
        sb3  = (SeekBar) view.findViewById(R.id.sb3);

        vColor = view.findViewById(R.id.v_penColor);

        btnBack = (Button) view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPenCOlorBackListener.onBack();
            }
        });

        sb1.setMax(255);
        sb2.setMax(255);
        sb3.setMax(255);

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                color1 = progress;
                int color = Color.rgb(color1, color2, color3);
                vColor.setBackgroundColor(color);
                mOnChangePenColorListener.onChangePenColor(0,color1,color2,color3);
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
                color2 = progress;
                int color = Color.rgb(color1, color2, color3);
                vColor.setBackgroundColor(color);
                mOnChangePenColorListener.onChangePenColor(0,color1,color2,color3);
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
                color3 = progress;
                int color = Color.rgb(color1, color2, color3);
                vColor.setBackgroundColor(color);
                mOnChangePenColorListener.onChangePenColor(0,color1,color2,color3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
