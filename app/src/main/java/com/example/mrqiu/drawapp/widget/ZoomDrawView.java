package com.example.mrqiu.drawapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Contacts;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.example.mrqiu.drawapp.R;

import java.util.ArrayList;
import java.util.logging.ErrorManager;

/**
 * Created by mrqiu on 2017/4/5.
 */

public class ZoomDrawView extends View implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    public static final float SCALE_MAX = 4.0f;


    public int UIWidth, UIHeight;
    private boolean once = true;

    private ScaleGestureDetector mScaleGestureDetector = null;

    private Matrix mScaleSrcBtpMatrix = new Matrix();
    private Matrix mScaleDrawMatrix = new Matrix();

    private float initScale = 0.2f;
    private Bitmap mSrcBitmap;
    private Bitmap mBgBitmap;

    private float mSrcInitScale = 1.0f;
    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    private Canvas mSrcBtpCanvas;


    private Paint mPenPaint;
    private Paint mEraserPaint;
    private Paint mColorPenPaint;

    private Canvas mPenCanvas;
    private Bitmap mPenBitmap;

    private int mPenWidth = 10;
    private int mEraserWidth = 50;
    private int mPenColor = Color.BLACK;


    public ZoomDrawView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ZoomDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ZoomDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mMode = MODE_PEN;
        mDrawList = new ArrayList<>();
        initPen();
        initEraser();


        UIWidth = getResources().getDisplayMetrics().widthPixels;
        UIHeight = getResources().getDisplayMetrics().heightPixels;
        int width = UIWidth;
        if (UIHeight > UIWidth) {
            width = UIHeight;
        }
        mPenBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        mPenCanvas = new Canvas(mPenBitmap);


        mBgBitmap = Bitmap.createBitmap(UIWidth, UIHeight, Bitmap.Config.ARGB_8888);
        mSrcBtpCanvas = new Canvas(mBgBitmap);

        mSrcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);

    }

    private void initColorPen() {
        mColorPenPaint = new Paint();
        mColorPenPaint.setAntiAlias(true);
        mColorPenPaint.setStrokeWidth(mPenWidth);
        mColorPenPaint.setStyle(Paint.Style.STROKE);
        mColorPenPaint.setColor(mPenColor);
        mColorPenPaint.setStrokeCap(Paint.Cap.ROUND);
        mColorPenPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void initPen() {
        mPenPaint = new Paint();
        mPenPaint.setAntiAlias(true);
        mPenPaint.setStrokeWidth(mPenWidth);
        mPenPaint.setStyle(Paint.Style.STROKE);
        mPenPaint.setColor(mPenColor);
        mPenPaint.setStrokeCap(Paint.Cap.ROUND);
        mPenPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void initEraser() {
        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        //这个属性是设置paint为橡皮擦重中之重
        //这是重点
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        //上面这句代码是橡皮擦设置的重点（重要的事是不是一定要说三遍）
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeWidth(mEraserWidth);

    }

    private float mLastMoveX, mLastMoveY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mScaleGestureDetector.onTouchEvent(event);

        int pointerCount = event.getPointerCount();

        if (pointerCount > 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float mx = event.getX();
                    float my = event.getY();
                    float dx = mx - mLastMoveX;
                    float dy = my - mLastMoveY;
                    if (Math.abs(dx) > 6 || Math.abs(dy) > 6) {

                        mScaleDrawMatrix.postTranslate(dx, dy);
                        mScaleSrcBtpMatrix.postTranslate(dx, dy);
                        checkBorderAndCenterWhenScale();
                        mLastMoveX = mx;
                        mLastMoveY = my;
                    }
                    break;
            }
        } else if (pointerCount <= 1)
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    touchMove(event);
                    break;
                case MotionEvent.ACTION_DOWN:
                    mLastMoveX = event.getX();
                    mLastMoveY = event.getY();
                    touchDown(event);
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp(event);
                    break;
            }
        return true;
    }

    private float mX, mY;
    public float TOUCH_TOLERANCE = 6;
    /**
     * 画笔的模式
     */
    public static final int MODE_PEN = 0;
    public static final int MODE_ERASER = 1;

    private ArrayList<DrawView.DrawParams> mDrawList;//用于存储每次的操作
    private int mMode = MODE_PEN;

    private void touchMove(MotionEvent event) {
        float moveX = getMoveX();
        float moveY = getMoveY();
        Log.i("touchDown", "touchDown: " + moveX + " ,moveY = " + moveY + ", X = " + getX() + ", y = " + getY());
        float x = (event.getX() - moveX) / getScale();
        float y = (event.getY() - moveY) / getScale();
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //mPath.reset();
            //mPath.moveTo(mX,mY);
            //mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

            mDrawList.get(mDrawList.size() - 1).path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            Log.i("touchMove", "touchMove: " + mX + ",mY=" + mY + "," + mDrawList.size());
            invalidate();
        }
        mX = x;
        mY = y;

    }

    private void touchUp(MotionEvent event) {

    }

    private void touchDown(MotionEvent event) {
        float moveX = getMoveX();
        float moveY = getMoveY();
        Log.i("touchDown", "touchDown: " + moveX + " ,moveY = " + moveY);
        float x = (event.getX() - moveX) / getScale();
        float y = (event.getY() - moveY) / getScale();
        mX = x;
        mY = y;
        DrawView.DrawParams drawParams = new DrawView.DrawParams();
        if (mMode == MODE_PEN) {
            drawParams.paint = new Paint(mPenPaint);
        } else if (mMode == MODE_ERASER) {
            drawParams.paint = new Paint(mEraserPaint);
        }
        drawParams.path = new Path();
        drawParams.path.moveTo(x, y);
        //mPath.moveTo(x, y);
        mDrawList.add(drawParams);
        invalidate();

    }


    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale() {
        mScaleDrawMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    public final float getMoveX() {
        mScaleDrawMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];

    }

    public final float getMoveY() {
        mScaleDrawMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (mSrcBitmap == null)
            return true;

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleDrawMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            mScaleSrcBtpMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            invalidate();
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        mScaleDrawMatrix.postTranslate(deltaX, deltaY);
        mScaleSrcBtpMatrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleDrawMatrix;
        RectF rect = new RectF();
        Bitmap d = mPenBitmap;
        if (null != d) {
            rect.set(0, 0, d.getWidth(), d.getHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            initSrcBitmap();

            once = false;
        }
    }

    private void initSrcBitmap() {
        if (mSrcBitmap == null)
            return;
        int width = getWidth();
        int height = getHeight();
        // 拿到图片的宽和高
        int dw = (int) (mSrcBitmap.getWidth() * getScale());
        int dh = (int) (mSrcBitmap.getHeight() * getScale());
        float scale = 1.0f;
        // 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        }
        if (dh > height && dw <= width) {
            scale = height * 1.0f / dh;
        }
        // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
        if (dw > width && dh > height) {
            scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
        }
        //initScale = scale;
        mSrcInitScale = scale;
        mScaleSrcBtpMatrix.setTranslate((width - dw) / 2, (height - dh) / 2);
        mScaleSrcBtpMatrix.postScale(scale, scale, getWidth() / 2,
                getHeight() / 2);
        // 图片移动至屏幕中心
    }


    Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {

        mPenCanvas.drawBitmap(mPenBitmap, 0, 0, mEraserPaint);
        if (mDrawList.size() > 0)
            for (int i = 0; i < mDrawList.size(); i++) {
                DrawView.DrawParams drawParams = mDrawList.get(i);
                mPenCanvas.drawPath(drawParams.path, drawParams.paint);
            }
        if (mSrcBitmap != null)
            canvas.drawBitmap(mSrcBitmap, mScaleSrcBtpMatrix, mPaint);

        canvas.drawBitmap(mPenBitmap, mScaleDrawMatrix, mPaint);
        super.onDraw(canvas);
    }


    public void setPenStyle(Paint.Style style) {
        mPenPaint.setStyle(style);
    }

    public void setPenColor(int color) {
        this.mPenColor = color;
        mPenPaint.setColor(color);

    }

    public void setPenColor(int alpha, int red, int green, int blue) {
        int color = Color.argb(alpha, red, green, blue);
        this.mPenColor = color;
        mPenPaint.setColor(mPenColor);
    }

    public void setPenWidth(int penWidth) {
        this.mPenWidth = penWidth;
        mPenPaint.setStrokeWidth(penWidth);

    }

    public void setEraserWidth(int eraserWidth) {
        this.mEraserWidth = eraserWidth;
        mEraserPaint.setStrokeWidth(eraserWidth);

    }


    public Bitmap outBitmap() {
        Bitmap outBitmap = Bitmap.createBitmap(UIHeight, UIHeight, Bitmap.Config.ARGB_8888);
        Canvas outCanvas;
        outCanvas = new Canvas(outBitmap);
        outCanvas.drawColor(Color.WHITE);

        // mScaleSrcBtpMatrix.postScale(1/getScale(),1/getScale(),getWidth()/2,getHeight()/2);
        //mScaleDrawMatrix.postScale(1/getScale(),1/getScale(),getWidth()/2,getHeight()/2);
        //Matrix matrix = new Matrix();
        //outSrcBitmap(mSrcBitmap,matrix);
        float scale = getScale();
        mScaleDrawMatrix.postScale(1 / scale, 1 / scale, getWidth() / 2, getHeight() / 2);
        mScaleSrcBtpMatrix.postScale(1 / scale, 1 / scale, getWidth() / 2, getHeight() / 2);
        checkBorderAndCenterWhenScale();

        outCanvas.drawBitmap(mSrcBitmap, mScaleSrcBtpMatrix, null);
        outCanvas.drawBitmap(mPenBitmap, mScaleDrawMatrix, null);

        return outBitmap;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        checkBorderAndCenterWhenScale();
        Toast.makeText(getContext(), "View 的大小改变了", Toast.LENGTH_SHORT).show();
    }

    public int getPenWidth() {
        return mPenWidth;
    }

    public int getEraserWidth() {
        return mEraserWidth;
    }


    public void setMode(int mode) {
        mMode = mode;
    }

    public void setSrcImage(String srcImage) {
        mSrcBitmap = BitmapFactory.decodeFile(srcImage);
        initSrcBitmap();
        invalidate();
    }
}
