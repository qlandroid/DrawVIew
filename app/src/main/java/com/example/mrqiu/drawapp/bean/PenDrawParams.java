package com.example.mrqiu.drawapp.bean;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by mrqiu on 2017/4/3.
 */

public class PenDrawParams  implements DrawParams{

    private Paint paint;
    private Path path;

    public PenDrawParams( Paint paint,Path path) {
        this.paint = paint;
        this.path = path;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public Path getPath() {
        return path;
    }
}
