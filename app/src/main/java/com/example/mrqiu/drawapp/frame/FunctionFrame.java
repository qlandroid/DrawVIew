package com.example.mrqiu.drawapp.frame;

import android.app.Fragment;
import android.os.Bundle;
import android.service.carrier.CarrierService;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mrqiu.drawapp.OnFunctionClickListener;
import com.example.mrqiu.drawapp.R;

/**
 * Created by mrqiu on 2017/4/6.
 */

public class FunctionFrame extends Fragment {

    private OnFunctionClickListener mOnFunctionClickListener;

    private Button btnPen, btnEraser, btnColor, btnColorPen,
            btnSave,btnPenWidth, btnEraserWidth,btnSrcImage;


    public static FunctionFrame newInstance(OnFunctionClickListener l) {

        Bundle args = new Bundle();

        FunctionFrame fragment = new FunctionFrame();
        fragment.setArguments(args);
        fragment.setOnFunctionClickListener(l);
        return fragment;
    }

    public void setOnFunctionClickListener(OnFunctionClickListener l) {
        this.mOnFunctionClickListener = l;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_fanction, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnColor = (Button) view.findViewById(R.id.btn_color);
        btnColorPen = (Button) view.findViewById(R.id.btn_colorPen);
        btnEraser = (Button) view.findViewById(R.id.btn_eraser);
        btnEraserWidth = (Button) view.findViewById(R.id.btn_eraserWidth);
        btnPenWidth = (Button) view.findViewById(R.id.btn_penWidth);
        btnPen = (Button) view.findViewById(R.id.btn_pen);
        btnSave = (Button) view.findViewById(R.id.btn_save);
        btnSrcImage = (Button) view.findViewById(R.id.btn_srcImage);

        btnSrcImage.setOnClickListener(listener);
        btnSave.setOnClickListener(listener);
        btnColor.setOnClickListener(listener);
        btnColorPen.setOnClickListener(listener);
        btnEraser.setOnClickListener(listener);
        btnEraserWidth.setOnClickListener(listener);
        btnPenWidth.setOnClickListener(listener);
        btnPen.setOnClickListener(listener);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_color:
                    mOnFunctionClickListener.clickPenColor();
                    break;
                case R.id.btn_colorPen:
                    mOnFunctionClickListener.clickColorPen();
                    break;
                case R.id.btn_eraser:
                    mOnFunctionClickListener.clickEraser();
                    break;
                case R.id.btn_eraserWidth:
                    mOnFunctionClickListener.clickEraserWidth();
                    break;
                case R.id.btn_pen:
                    mOnFunctionClickListener.clickPen();
                    break;
                case R.id.btn_penWidth:
                    mOnFunctionClickListener.clickPenWidth();
                    break;
                case R.id.btn_save:
                    mOnFunctionClickListener.clickSaveImage();
                    break;
                case R.id.btn_srcImage:
                    mOnFunctionClickListener.clickSrcImage();
                    break;
            }
        }
    };
}
