package com.meronmks.zimitta.ImageMoveUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by meron on 2016/09/18.
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{
    private Context context;
    private SurfaceHolder mHolder;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private float mTranslateX, mTranslateY;
    private float mScale;
    private ScaleGestureDetector mScaleGestureDetector;
    private TranslationGestureDetector mTranslationGestureDetector;
    private float mPrevX, mPrevY;

    public CustomSurfaceView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        mMatrix = new Matrix();
        mScale = 1.0f;
        mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleListener);
        mTranslationGestureDetector = new TranslationGestureDetector(mTranslationListener);
        setOnTouchListener(this);
    }

    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        present();
    }

    public void setImageResourceId(int resourceId) {
        Resources resources = context.getResources();
        mBitmap = BitmapFactory.decodeResource(resources, resourceId);
    }

    public void present()
    {
        Canvas canvas = mHolder.lockCanvas();

        mMatrix.reset();
        mMatrix.postScale(mScale, mScale);
        mMatrix.postTranslate(-mBitmap.getWidth() / 2 * mScale, -mBitmap.getHeight() / 2 * mScale);
        mMatrix.postTranslate(mTranslateX, mTranslateY);

        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBitmap, mMatrix, null);

        mHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mHolder = surfaceHolder;
        if(mBitmap == null) return;
        present();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        mHolder = surfaceHolder;

        if(mBitmap == null) return;
        mTranslateX = width / 2;
        mTranslateY = height / 2;
        present();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private ScaleGestureDetector.SimpleOnScaleGestureListener mOnScaleListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            return true;
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mTranslationGestureDetector.onTouch(view, motionEvent);
        mScaleGestureDetector.onTouchEvent(motionEvent);
        present();
        return true;
    }

    private TranslationGestureListener mTranslationListener
            = new TranslationGestureListener() {
        @Override
        public void onTranslationEnd(TranslationGestureDetector detector) {
        }

        @Override
        public void onTranslationBegin(TranslationGestureDetector detector) {
            mPrevX = detector.getX();
            mPrevY = detector.getY();
        }

        @Override
        public void onTranslation(TranslationGestureDetector detector) {
            float deltaX = detector.getX() - mPrevX;
            float deltaY = detector.getY() - mPrevY;
            mTranslateX += deltaX;
            mTranslateY += deltaY;
            mPrevX = detector.getX();
            mPrevY = detector.getY();
        }
    };
}
