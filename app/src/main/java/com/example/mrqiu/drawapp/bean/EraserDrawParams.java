package com.example.mrqiu.drawapp.bean;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by mrqiu on 2017/4/3.
 */

public class EraserDrawParams implements DrawParams {
    private Path path;
    private Paint paint;

    public EraserDrawParams(Path path,Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    @Override
    public Paint getPaint() {
        return null;
    }

    @Override
    public Path getPath() {
        return null;
    }
}
