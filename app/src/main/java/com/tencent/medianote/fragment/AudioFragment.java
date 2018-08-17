package com.tencent.medianote.fragment;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.medianote.R;
import com.tencent.medianote.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioFragment extends BaseFragment {

    private static final String TAG = AudioFragment.class.getSimpleName();

    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    private String fileName = null;
    private String wavName = null;
//    private String opusName = null;

    private AudioRecord record;
    private AudioTrack track;

    private int sampleRate = 16000;

    private int channel = AudioFormat.CHANNEL_IN_MONO;

    private int encoding = AudioFormat.ENCODING_PCM_16BIT;

    private int minRecordBufferSize = AudioRecord.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);

    private int minTrackBufferSize = AudioTrack.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);

    private File pcmFile;
    private File wavFile;
//    private File opusFile;

    private HandlerThread recordThread;
    private Handler recordHandler;


    private boolean isRecording = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_audio;
    }

    @Override
    protected void contructView(View view) {
        view.findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });

        view.findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });

        view.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        init(Environment.getExternalStorageDirectory() + "/record/" + "record.pcm");
    }

    public void init(String fileName){
        Log.d(TAG,"" + fileName);
        this.fileName = fileName;
        pcmFile = new File(this.fileName);
        File file = pcmFile.getParentFile();
        Log.d(TAG,"-" + file.getAbsolutePath());
        file.mkdir();

        wavName = file.getAbsolutePath() + "/pcm2wav.wav";
//        opusName = file.getAbsolutePath() + "/wav2opus.opus";

        wavFile = new File(wavName);
//        opusFile = new File(opusName);


        if(!pcmFile.exists()){
            boolean result = false;
            try {
                result = pcmFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("record","create file = " + result);
        }

        recordThread = new HandlerThread("record");
        recordThread.start();
        recordHandler = new Handler(recordThread.getLooper());

    }

    public void startRecord(){

        record = new AudioRecord(AUDIO_INPUT,
                sampleRate,
                channel,
                encoding,
                minRecordBufferSize
        );

        Log.d("AudioRecorder","===startRecord==="+record.getState());

        if(record.getState() != AudioRecord.STATE_INITIALIZED){
            Log.e("AudioRecorder","record not init");
            return;
        }

        if(record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            Log.e("AudioRecorder","recording");
            return;
        }

        recordHandler.post(new Runnable() {
            @Override
            public void run() {
                try {

//                    DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(pcmFile)));
                    DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(pcmFile)));

                    byte[] buffer = new byte[minRecordBufferSize];


                    //正式开始录制
                    record.startRecording();

                    while(record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                        int read = record.read(buffer,0,buffer.length);
                        if(read>0){
                            outputStream.write(buffer);
                        }
                    }

                    outputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void stopRecord() {
        Log.d("AudioRecorder","===stopRecord===");

        if (record.getState() != AudioRecord.STATE_INITIALIZED || record.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            Log.e("record","录音尚未开始");
        } else {
            record.stop();
            record.release();

            changeToWav();
        }
    }

    public void play(){
        track = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,AudioFormat.CHANNEL_OUT_MONO,encoding,minTrackBufferSize,AudioTrack.MODE_STREAM);
        track.setVolume(1.0f);


        recordHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("AudioRecorder","===startpaly===");
                    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(wavFile)));
                    int read = 0;
                    byte[] buffer = new byte[minTrackBufferSize];
                    track.play();
                    while((read = inputStream.read(buffer))>0){
                        track.write(buffer,0,minTrackBufferSize);
                    }

                    track.stop();
                    track.release();

                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void play(String fileName){

        final File playFile = new File(fileName);
        if(!playFile.exists()){
            Log.e("test","not exist");
        }

        track = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,AudioFormat.CHANNEL_OUT_MONO,encoding,minTrackBufferSize,AudioTrack.MODE_STREAM);
        track.setVolume(1.0f);


        recordHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("AudioRecorder","===startpaly===");
                    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(playFile)));
                    int read = 0;
                    byte[] buffer = new byte[minTrackBufferSize];
                    track.play();
                    while((read = inputStream.read(buffer))>0){
                        track.write(buffer,0,minTrackBufferSize);
                    }

                    track.stop();
                    track.release();

                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void changeToWav(){
        Utils.pcmToWav(this.fileName,wavName);
    }

    public void changeToWav(String path){
        Utils.pcmToWav(path,wavName);
    }

//    public void encode(){
//        if(top.oply.opuslib.Utils.isWAVFile(wavName)){
//            Log.d("TEST",wavName + " - encode now - ");
//            OpusTool opusTool = new OpusTool();
//            opusTool.encode(wavName,opusName,"--bitrate 16.nnn --framesize 60");
//        }else {
//            Log.e("TEST","not wav");
//        }
//
////        opusTool.encode()
//    }
//
//    public void decode(){
//        try {
//            OpusTool opusTool = new OpusTool();
//            opusTool.decode(opusName,wavName,"--bitrate 16.nnn --framesize 60");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void decode(String srcPath,String desPath){
//        try {
//            OpusTool opusTool = new OpusTool();
//            opusTool.decode(srcPath,desPath,"--bitrate 16.nnn --framesize 60");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
