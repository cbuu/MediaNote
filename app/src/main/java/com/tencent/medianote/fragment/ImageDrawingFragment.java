package com.tencent.medianote.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.medianote.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageDrawingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageDrawingFragment extends Fragment {



    public ImageDrawingFragment() {
        // Required empty public constructor
    }

    public static ImageDrawingFragment newInstance() {
        ImageDrawingFragment fragment = new ImageDrawingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_drawing, container, false);
    }


    public static class MyImageView extends View{

        private Paint paint;

        private Bitmap bitmap;

        private Rect srcRect;
        private Rect dstRect;

        public MyImageView(Context context) {
            super(context);
            init();
        }

        public MyImageView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }


        private void init(){
            paint = new Paint();
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.logo_test);
            float density = getResources().getDisplayMetrics().density;
            srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            dstRect = new Rect(0,0,(int)(100 * density),(int)(100 * density));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(bitmap,srcRect,dstRect,paint);
        }
    }

    public static class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

        private SurfaceHolder mHolder;
        private Canvas mCanvas;
        private boolean mIsRunning;

        private Thread drawThread;

        private Paint paint;

        private Bitmap bitmap;
        private Rect srcRect;
        private Rect dstRect;

        public MySurfaceView(Context context) {
            super(context);
            init();
        }

        public MySurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init(){
            Log.d("Main","init");

            paint = new Paint();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_test);
            float density = getResources().getDisplayMetrics().density;
            srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            dstRect = new Rect(0,0,(int)(100 * density),(int)(100 * density));
            mHolder = getHolder();
            mHolder.addCallback(this);
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d("Main","created");

            mIsRunning = true;
            drawThread = new Thread(this);
            drawThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mIsRunning = false;
            mCanvas = null;
        }

        @Override
        public void run() {

            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                try {
                    mCanvas.drawColor(Color.WHITE);
                    Log.d("Main","run");

                    mCanvas.drawBitmap(bitmap,srcRect,dstRect,paint);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }
    }

}
