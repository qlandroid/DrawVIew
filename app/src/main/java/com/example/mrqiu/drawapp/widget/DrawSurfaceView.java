package com.example.mrqiu.drawapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.mrqiu.drawapp.R;

import java.util.ArrayList;

/**
 * Created by mrqiu on 2017/4/3.
 */

public class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback,ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private String TAG = "conowen";

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





    private SurfaceHolder sfh;
    private boolean ThreadFlag;
    private int counter;
    private Canvas canvas;

    private Thread mThread = new Thread(new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (ThreadFlag) {

                // 锁定画布，得到Canvas对象
                canvas = sfh.lockCanvas();

               onDrawView(canvas);

                if (canvas != null) {
                    // 解除锁定，并提交修改内容，更新屏幕
                    sfh.unlockCanvasAndPost(canvas);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    });

    private void onDrawView(Canvas canvas) {

    }


    public DrawSurfaceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }
    public DrawSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 通过SurfaceView获得SurfaceHolder对象
        sfh = this.getHolder();

        // 为SurfaceHolder添加回调结构SurfaceHolder.Callback
        sfh.addCallback(this);
    }
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        //mMode = MODE_PEN;
        //mDrawList = new ArrayList<>();
        initPen();
        initEraser();


        UIWidth = getResources().getDisplayMetrics().widthPixels;
        UIHeight = getResources().getDisplayMetrics().heightPixels;
        int width = UIWidth;
        if (UIHeight > UIWidth){
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

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        Log.i(TAG, "surfaceChanged");

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.i(TAG, "surfaceCreated");
        counter = 0;
        ThreadFlag = true;
        mThread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.i(TAG, "surfaceDestroyed");
        ThreadFlag = false;

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onGlobalLayout() {

    }
}
