package com.tencent.medianote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.MediaController;

public class IOMediaView extends SurfaceView implements MediaController.MediaPlayerControl{

    private double aspectRatio;
    private MediaController mMediaController;
    private VideoPlayer mVideoPlayer;

    public IOMediaView(Context context) {
        super(context);
    }

    public IOMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IOMediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IOMediaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(){
        mMediaController = new MediaController(getContext());
        mMediaController.setMediaPlayer(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
