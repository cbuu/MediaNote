package com.tencent.medianote.fragment;

import android.animation.TimeAnimator;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.tencent.medianote.R;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaFragment extends BaseFragment implements TimeAnimator.TimeListener{

    private static final String TAG = MediaFragment.class.getSimpleName();

    private TextureView mPlaybackView;
    private TimeAnimator mTimeAnimator = new TimeAnimator();

    private MediaExtractor mVideoExtractor = new MediaExtractor();
    private MediaExtractor mAudioExtractor = new MediaExtractor();
    private MediaCodec mVideoMediaCodec;
    private MediaCodec mAudioMediaCodec;

    private HandlerThread videoThread = new HandlerThread("video");
    private HandlerThread audioThread = new HandlerThread("video");

    private Handler videoHandler;
    private Handler audioHandler;

    MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
    int videoTrackIndex = -1;
    int audioTrackIndex = -1;

    boolean isVideoEOS = false;

    @Override
    public void onPause() {
        super.onPause();
        if(mTimeAnimator != null && mTimeAnimator.isRunning()) {
            mTimeAnimator.end();
        }

        mVideoMediaCodec.stop();
        mVideoMediaCodec.release();
        mVideoExtractor.release();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_media;
    }

    @Override
    protected void contructView(View view) {

        view.findViewById(R.id.muxer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.findViewById(R.id.extract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        view.findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPlaybackView = view.findViewById(R.id.playView);

        init();
    }

    private void init(){

//        videoThread.start();
//        audioThread.start();
//
//        videoHandler = new Handler(videoThread.getLooper());
//        audioHandler = new Handler(audioThread.getLooper());




    }

    private void muxer(){

    }

    private void extract(){

    }

    private void play(){

        Log.d(TAG,"play");

        Uri mediaUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.vid_bigbuckbunny);

        try {
            mVideoExtractor.setDataSource(getContext(),mediaUri,null);
            mAudioExtractor.setDataSource(getContext(),mediaUri,null);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"mextractor setDataSource error");
        }

        videoTrackIndex = getVidioTrackIndex(mVideoExtractor);
        audioTrackIndex = getAudioTrackIndex(mAudioExtractor);

        if (videoTrackIndex == -1){
            Log.e(TAG,"no video track");
            return;
        }

        MediaFormat mediaFormat = mVideoExtractor.getTrackFormat(videoTrackIndex);

        int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        int heigth = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);

        long time = mediaFormat.getLong(MediaFormat.KEY_DURATION) / 1000; // second

        Log.d(TAG,"video w = " + width + " h = " + heigth + "  time = " + time);

        mVideoExtractor.selectTrack(videoTrackIndex);

        try {
            String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
            mVideoMediaCodec = MediaCodec.createDecoderByType(mimeType);

            mVideoMediaCodec.configure(mediaFormat,new Surface(mPlaybackView.getSurfaceTexture()),null,0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mVideoMediaCodec == null) {
            Log.v(TAG, "MediaCodec null");
            return;
        }

        mVideoMediaCodec.start();


        if (audioTrackIndex == -1){
            Log.e(TAG,"no audio track");
            return;
        }

        mAudioExtractor.selectTrack(audioTrackIndex);

        mTimeAnimator.setTimeListener(this);
        mTimeAnimator.start();
    }


    private void stop(){

    }


    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {

//        Log.d(TAG,"onTimeUpdate");

        if (!isVideoEOS){
            isVideoEOS = putBufferToCoder(mVideoExtractor, mVideoMediaCodec);
        }

        int outputBufferIndex = mVideoMediaCodec.dequeueOutputBuffer(videoBufferInfo,0);

        switch (outputBufferIndex) {
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                Log.v(TAG, "format changed");
                break;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                Log.v(TAG, "解码当前帧超时");
                break;
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                //outputBuffers = videoCodec.getOutputBuffers();
                Log.v(TAG, "output buffers changed");
                break;
            default:
                //直接渲染到Surface时使用不到outputBuffer
                //ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                //延时操作
                //如果缓冲区里的可展示时间>当前视频播放的进度，就休眠一下
//                sleepRender(videoBufferInfo, startMs);
                //渲染
                mVideoMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                break;
        }

        if ((videoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0){
            mTimeAnimator.end();
            mVideoMediaCodec.stop();
            mVideoMediaCodec.release();
            mVideoExtractor.release();
        }
    }

    private int getVidioTrackIndex(MediaExtractor extractor){
        int trackIndex = -1;
        int count = extractor.getTrackCount();
        for (int i = 0;i < count; i++ ){
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video")) {
                trackIndex = i;
                break;
            }
        }

        return trackIndex;
    }

    private int getAudioTrackIndex(MediaExtractor extractor){
        int trackIndex = -1;
        int count = extractor.getTrackCount();
        for (int i = 0;i < count; i++ ){
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                trackIndex = i;
                break;
            }
        }

        return trackIndex;
    }

    private boolean putBufferToCoder(MediaExtractor extractor, MediaCodec decoder) {
        boolean isMediaEOS = false;
        int inputBufferIndex = decoder.dequeueInputBuffer(0);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);


            //retrieve a frame to inputBuffer
            int sampleSize = extractor.readSampleData(inputBuffer, 0);

            if (sampleSize < 0) {
                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isMediaEOS = true;
                Log.v(TAG, "media eos");
            } else {
                decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                extractor.advance();
            }
        }
        return isMediaEOS;
    }
}
