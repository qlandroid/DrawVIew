package com.example.mrqiu.drawapp.frame;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mrqiu.drawapp.OnChangeWidthListener;
import com.example.mrqiu.drawapp.OnFrameBackListener;
import com.example.mrqiu.drawapp.R;

/**
 * Created by mrqiu on 2017/4/6.
 */

public class WidthFrame extends Fragment {
    public static final String INIT_WIDTH = "initWidth";
    private Button btnBack;
    private SeekBar sbWidth;
    private TextView tvWidth;

    private OnFrameBackListener mOnFrameBackListener;
    private OnChangeWidthListener mOnChangeWidthListener;

    private int mWidth;

    private StringBuffer sb ;

    public static WidthFrame newInstance(int initWidth, OnChangeWidthListener changeWidthListener, OnFrameBackListener backListener) {

        Bundle args = new Bundle();

        WidthFrame fragment = new WidthFrame();
        fragment.setOnChangeWidthListener(changeWidthListener);
        fragment.setOnFrameBackListener(backListener);

        args.putInt(INIT_WIDTH, initWidth);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnFrameBackListener(OnFrameBackListener l) {
        this.mOnFrameBackListener = l;
    }

    public void setOnChangeWidthListener(OnChangeWidthListener l) {
        this.mOnChangeWidthListener = l;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mWidth = bundle.getInt(INIT_WIDTH);
        }
        return inflater.inflate(R.layout.frame_width, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = (Button) view.findViewById(R.id.btn_widthBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnFrameBackListener.onBack();
            }
        });

        tvWidth = (TextView) view.findViewById(R.id.tv_width);
        sb = new StringBuffer();
        sb.append("宽度：");
        sb.append(mWidth);
        tvWidth.setText(sb.toString());

        sbWidth = (SeekBar) view.findViewById(R.id.sb_width);
        sbWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sb.delete(0,sb.length()-1);
                sb.append("宽度:");
                sb.append(progress);
                tvWidth.setText(sb.toString());
                mOnChangeWidthListener.onChangeWidth(progress);
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
