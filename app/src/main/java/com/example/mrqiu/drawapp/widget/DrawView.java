package com.example.mrqiu.drawapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.mrqiu.drawapp.R;

import java.util.ArrayList;

/**
 * Created by mrqiu on 2017/4/3.
 */

public class DrawView extends View implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener
,ViewTreeObserver.OnGlobalLayoutListener{

    private static final int INIT_PEN_WIDTH = 80;

    /**
     * 画笔的模式
     */
    public static final int MODE_PEN = 0;
    public static final int MODE_ERASER = 1;

    private ArrayList<DrawParams> mDrawList;//用于存储每次的操作

    private float mX, mY;
    private float TOUCH_TOLERANCE = 4;
    private int mMode;
    private Paint mPenPaint;//画画的画笔
    private Paint mEraserPaint;//用于橡皮擦的画笔


    private int mPenWidth;//用于保存当前的画笔宽度
    private int mPenColor;//用于保存当前画笔的颜色
    private Paint.Style mPenStyle; //当前画笔的样式

    private int mEraserWidth = 50;//用于保存当前橡皮擦的宽度

    private Canvas mPenCanvas;//用于画画的画板
    private Bitmap mPenBitmap;//一个空白的图片，通过画板控制 mPenCanvas;


    /**
     * 用于多点触控 屏幕滑动事件任务交给该类处理，即在onTouch 事件中操作
     */
    private ScaleGestureDetector mScaleGestureDetector;

    /**
     * 用于判断是否是多点触控
     */
    private boolean isMoreHand;
    private float mMaxScale = 8f;
    private float mInitScale = 4f;

    private Bitmap mSrcBitmap;

    /**
     * 用于判断当前是否是在放大缩小状态
     */
    private boolean isScale;





    public static class DrawParams {
        Path path;
        Paint paint;
    }

    public DrawView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mDrawList = new ArrayList<>();
        mPenColor = Color.BLUE;
        mPenStyle = Paint.Style.STROKE;
        mPenWidth = INIT_PEN_WIDTH;


        mPenPaint = new Paint();
        mPenPaint.setAntiAlias(true);
        mPenPaint.setStrokeWidth(mPenWidth);
        mPenPaint.setStyle(mPenStyle);
        mPenPaint.setColor(mPenColor);
        mPenPaint.setStrokeCap(Paint.Cap.ROUND);
        mPenPaint.setStrokeJoin(Paint.Join.ROUND);

        mEraserPaint = new Paint();
        mEraserPaint.setStrokeWidth(mEraserWidth);
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


        //创建一个画布背景
        mPenBitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        //用于操作的画布
        mPenCanvas = new Canvas(mPenBitmap);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        setOnTouchListener(this);
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
    public void onGlobalLayout() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        mSrcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg, options);

        int width = getWidth();
        int height = getHeight();
        int dw = options.outWidth;
        int dh = options.outHeight;
        float scale = 0;
        if (dw < width && dh > height){
            scale = height * 1.0f / dh;
        } if (dw < width && dh < height){
            scale = Math.min(width * 1.0f / dw,height * 1.0f / dh);
        } if (dw > width && dh <height){
            scale = width * 1.0f / dw;
        } if(dw > width && dh > height){
            scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
        }
       if (scale < 1) {
            options.inSampleSize = (int) (scale * 10);
        }else if (scale >1 ){
            options.inSampleSize = (int) ((scale -1) * 10);
        }
        options.inJustDecodeBounds = false;
        mSrcBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.timg,options);


    }



    private float mMoveX, mMoveY;
    private int mLastPointerCount;

    private float mDMoveX,mDMoveY,mLastDMoveX,mLastDMoveY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int pointerCount = event.getPointerCount();
        mLastPointerCount = pointerCount;
        isMoreHand = pointerCount > 1;
        Log.i("onTouch", "onTouch: " + "x = " + event.getX() + ", y = " + event.getY());
        if (mLastPointerCount == pointerCount) {
            //手指没有变化
        } else {
            //手指数量变化了
        }

        mScaleGestureDetector.onTouchEvent(event);
        Log.i("onTouch", "onTouch:  手指触控数量pointerCount = " + pointerCount);
        if (isMoreHand) {

            /*int dx = (int) ((event.getX(0) - event.getX(1)) / 2 );
            int dy = (int) ((event.getY(0) - event.getY(1)) / 2 );

            dx = Math.abs(dx);
            dy = Math.abs(dy);
            if (Math.abs(mMoveX - dx) > 4 || Math.abs(mMoveY - dy ) > 4) {
                mMoveX = dx;
                mMoveY = dy;
                scrollTo(dx, dy);
                Log.i("scrollTo", "onTouch: " + "dx = " + dx + " , dy = " + dy);
            }
*/
            mDMoveX = (event.getX(0) + event.getX(1))/2;
            mDMoveY = (event.getY(0) + event.getY(1))/2;
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mLastDMoveX = mDMoveX;
                    mLastDMoveY = mDMoveY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("onTouchMove", "onTouch: 是放大状态移动了 ");

                    int moveX = (int) (mLastDMoveX - mDMoveX);
                    int moveY = (int) (mLastDMoveY - mDMoveY);
                    if (Math.abs(moveX) > 4 || Math.abs(moveY) > 4) {
                        scrollTo(moveX, moveY);
                        mLastDMoveX = mDMoveX ;
                        mLastDMoveY = mDMoveY;
                    }


                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isScale = false;
                    break;
            }

        } else {
            if (!isScale)
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDown(event);
                        break;
                    case MotionEvent.ACTION_UP:

                        touchUp(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchMove(event);
                        break;
                }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isScale = false;
        }
        return true;
    }




        private void touchMove(MotionEvent event) {
            float x = event.getX() + mMoveX;
            float y = event.getY() + mMoveY;
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
            float x = event.getX() + mMoveX;
            float y = event.getY() + mMoveY;
            mX = x;
            mY = y;
            DrawParams drawParams = new DrawParams();
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

    public void setMode(int mode) {
        mMode = mode;
    }

    private float mScale = 1f;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {


        if (isMoreHand) {
            //获得当前的缩放比例，
            float scaleFactor = detector.getScaleFactor();
            float scale = mScale * scaleFactor;
            Log.i("mScale", "onScale:  scale = " + scale);
            if (Math.abs(mScale - scale) > 0.03)
                if (scaleFactor > 1f && scale < 3f) {
                /*
                 * 当前是放大状态，控制最大放大倍数为5倍，
                 */
                    isScale = true;

                    mScale = scale;
                    Log.i("onScale", "onScale: 放大");
                } else if (scaleFactor < 1f && scale > 1f) {
                /*
                    当前是缩小状态，如果当前缩小，并且，当前的缩放比例不能小于1，这样才能保证最小状态时为原始比例；
                 */
                    mScale = scale;
                    isScale = true;
                    Log.i("onScale", "onScale:  缩小");
                }
            Log.i("onScale", "onScale: mScale = " + mScale);
            setScaleY(mScale);
            setScaleX(mScale);
            //mMoveY = detector.getFocusY();
            //mMoveX = detector.getFocusX();
            //scrollTo(mMoveX,mMoveY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


    private void initDraw() {
        mPenCanvas.drawColor(Color.argb(0, 0, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSrcBitmap != null) {
            int height = getHeight() - mSrcBitmap.getHeight();
            int width = getWidth() - mSrcBitmap.getWidth();
            canvas.drawBitmap(mSrcBitmap, width/2, height/2, null);
        }
        /**
         * 用于解决 使用透明色时，划线颜色一直加深的问题，该方法就是每次操作的时候都会把画板用橡皮擦清空内容
         * 然后在让保存的每次操作进行对画板的设置；
         */
        mPenCanvas.drawBitmap(mPenBitmap, 0, 0, mEraserPaint);
        if (mDrawList.size() > 0)
            for (int i = 0; i < mDrawList.size(); i++) {
                DrawParams drawParams = mDrawList.get(i);
                mPenCanvas.drawPath(drawParams.path, drawParams.paint);
            }
        canvas.drawBitmap(mPenBitmap, 0, 0, null);
        super.onDraw(canvas);
    }

    public void setPenColor(int penColor) {
        this.mPenColor = penColor;
        mPenPaint.setColor(penColor);
    }
}
