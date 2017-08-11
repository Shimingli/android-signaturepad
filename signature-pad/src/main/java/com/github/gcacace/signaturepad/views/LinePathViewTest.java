package com.github.gcacace.signaturepad.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.github.gcacace.signaturepad.utils.logger.KLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 绘制Path的View 用于签名
 *
 * @author wastrel
 */
@SuppressLint("ClickableViewAccessibility")
public class LinePathViewTest extends View {

    private  static final String TAG=LinePathViewTest.class.getSimpleName();
    private List<PathSegment> pathSegments;

    private class PathSegment{
        Path path;
        float width;
        int alpha;

        public PathSegment(Path path) {
            this.path = path;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

    }
    private Context mContext;

    /**
     * 笔画X坐标起点
     */
    private float mX;
    /**
     * 笔画Y坐标起点
     */
    private float mY;
    /**
     * 手写画笔
     */
    private final Paint mGesturePaint = new Paint();
    /**
     * 路径  final
     */
    private  Path mPath = new Path();
    /**
     * 背景画布
     */
    private Canvas cacheCanvas;
    /**
     * 背景Bitmap缓存
     */
    private Bitmap cachebBitmap;
    /**
     * 是否已经签名
     */
    private boolean isTouched = false;


    /**
     * 画笔宽度 px；
     */
    private int mPaintWidth = 10;

    /**
     * 前景色
     */
    private int mPenColor = Color.BLACK;

    private int mBackColor= Color.TRANSPARENT;
    private VelocityTracker mVelocityTracker;
    private int velocityValueX;
    private int mYVelocity;

    public LinePathViewTest(Context context) {
        super(context);
        init(context);
    }

    public LinePathViewTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinePathViewTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(mPaintWidth);
        mGesturePaint.setColor(mPenColor);
        pathSegments = new ArrayList<>();

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cachebBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(mBackColor);
        isTouched=false;
    }
    /**
     * 监听滑动的速度的方法
     *
     * @param event
     */
    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }
    /**
     * 释放监听滑动速度方法
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 跟踪滑动的速度
        obtainVelocityTracker(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pathSegments.clear();
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                // 计算并获取滑动速度
                mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
                velocityValueX = (int) mVelocityTracker.getXVelocity();
                mYVelocity = (int) mVelocityTracker.getYVelocity();
//                if (Math.abs(velocityValueX)>Math.abs(mYVelocity)){
//                    float v = (float) (mPaintWidth - 0.008 * Math.abs(velocityValueX));
//                    if (v>0.5f) {
//                        mGesturePaint.setStrokeWidth(v);
//                    }else {
//                        mGesturePaint.setStrokeWidth(0.5f);
//                    }
//                }else {
//                    float v = (float) (mPaintWidth - 0.008 * Math.abs(mYVelocity));
//                    if (v>0.5f) {
//                        mGesturePaint.setStrokeWidth(v);
//                    }else {
//                        mGesturePaint.setStrokeWidth(0.5f);
//                    }
//                }
                getPaths(mPath);
                mPath.lineTo(event.getX(),event.getY());
//                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(mPath, mGesturePaint);
                pathSegments.clear();
                releaseVelocityTracker();
                mPath.reset();
                break;
        }
        // 更新绘制
        invalidate();
        return true;
    }

    /**
     * 越小，线条锯齿度越小
     */
    private static final float DEFAULT_SEGMENT_LENGTH = 10F;
    private static final float DEFAULT_WIDTH = 3F;
    private static final float MAX_WIDTH = 45F;
    /**
     * 截取path
     * @param path
     */
    private void getPaths(Path path){
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        KLog.d("shiming length" +length);
        int segmentSize = (int) Math.ceil(length / DEFAULT_SEGMENT_LENGTH);
        Path ps = null;
        PathSegment pe = null;
        int nowSize = pathSegments.size();//集合中已经有的
        if(nowSize == 0){
            ps = new Path();
            pm.getSegment(0, length, ps, true);
            pe = new PathSegment(ps);
            pe.setAlpha(255);
            pe.setWidth(DEFAULT_WIDTH);
            pathSegments.add(pe);
        } else{
            for (int i = nowSize; i < segmentSize; i++) {
                ps = new Path();
                pm.getSegment((i - 1) * DEFAULT_SEGMENT_LENGTH - 0.4f, Math.min(i * DEFAULT_SEGMENT_LENGTH, length), ps,  true);
                pe = new PathSegment(ps);
                pe.setAlpha(255);
                pe.setWidth((float) Math.min(MAX_WIDTH, i * 0.3 + DEFAULT_WIDTH));
                pathSegments.add(pe);
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawBitmap(cachebBitmap, 0, 0, mGesturePaint);
//        // 通过画布绘制多点形成的图形
//        canvas.drawPath(mPath, mGesturePaint);
        canvas.drawBitmap(cachebBitmap, 0, 0, mGesturePaint);
        for (PathSegment p: pathSegments) {
            mGesturePaint.setAlpha(p.getAlpha());
            mGesturePaint.setStrokeWidth(p.getWidth());
            canvas.drawPath(p.getPath(), mGesturePaint);
        }


    }

    // 手指点下屏幕时调用
    private void touchDown(MotionEvent event) {

        // mPath.rewind();
        // 重置绘制路线，即隐藏之前绘制的轨迹
        mPath.reset();
        float x = event.getX();
        float y = event.getY();

        mX = x;
        mY = y;
        // mPath绘制的绘制起点
        mPath.moveTo(x, y);
    }

    // 手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float previousX = mX;
        final float previousY = mY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);

        // 两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;

            // 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);
//            mPath.op();
            // 第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
    }

    /**
     * 清除画板
     */
    public void clear() {
        if (cacheCanvas != null) {
            isTouched = false;
            mGesturePaint.setColor(mPenColor);
            cacheCanvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
            mGesturePaint.setColor(mPenColor);
            invalidate();
        }
    }


    /**
     * 保存画板
     *
     * @param path 保存到路劲
     */

    public void save(String path) throws IOException {
        save(path, false, 0);
    }

    /**
     * 保存画板
     *
     * @param path       保存到路劲
     * @param clearBlank 是否清楚空白区域
     * @param blank  边缘空白区域
     */
    public void save(String path, boolean clearBlank, int blank) throws IOException {

        Bitmap bitmap=cachebBitmap;
        //BitmapUtil.createScaledBitmapByHeight(srcBitmap, 300);//  压缩图片
        if (clearBlank) {
            bitmap = clearBlank(bitmap, blank);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(buffer);
                outputStream.close();
        }
    }

    /**
     * 获取画板的bitmap
     * @return
     */
    public Bitmap getBitMap()
    {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap=getDrawingCache();
        setDrawingCacheEnabled(false);
        return bitmap;
    }


    public Bitmap getSignatureBitmap() {
        Bitmap originalBitmap = getTransparentSignatureBitmap();
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        return whiteBgBitmap;
    }
    public Bitmap getTransparentSignatureBitmap() {
        ensureSignatureBitmap();
        return cachebBitmap;
    }
    private void ensureSignatureBitmap() {
        if (cachebBitmap == null) {
            cachebBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Config.ARGB_8888);
            cacheCanvas = new Canvas(cachebBitmap);
        }
    }

    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }

    /**
     * 设置画笔宽度 默认宽度为10px
     *
     * @param mPaintWidth
     */
    public void setPaintWidth(int mPaintWidth) {
        mPaintWidth = mPaintWidth > 0 ? mPaintWidth : 10;
        this.mPaintWidth = mPaintWidth;
        mGesturePaint.setStrokeWidth(mPaintWidth);

    }


    public void setBackColor(@ColorInt int backColor)
    {
        mBackColor=backColor;
    }


    /**
     * 设置画笔颜色
     *
     * @param mPenColor
     */
    public void setPenColor(int mPenColor) {
        this.mPenColor = mPenColor;
        mGesturePaint.setColor(mPenColor);
    }

    /**
     * 是否有签名
     *
     * @return
     */
    public boolean getTouched() {
        return isTouched;
    }
}
