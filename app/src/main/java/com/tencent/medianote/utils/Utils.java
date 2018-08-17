package com.tencent.medianote.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {






    /**
     * pcm文件转wav文件
     *
     * @param inFilename  源文件路径
     * @param outFilename 目标文件路径
     */
    public static void pcmToWav(String inFilename, String outFilename) {

        Log.d("TEST",inFilename + " - change to wav now - " + outFilename);

        int mSampleRate = 16000;// 8000|16000
        int mChannel = AudioFormat.CHANNEL_IN_MONO;   //立体声
        int mEncoding = AudioFormat.ENCODING_PCM_16BIT;
        int mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);;  //缓存的音频大小



        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = mSampleRate;
        int channels = 2;
        long byteRate = 16 * mSampleRate * channels / 8;
        byte[] data = new byte[mBufferSize];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            Log.d("TEST","len = " + totalAudioLen);
            totalDataLen = totalAudioLen + 36;

            writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            int i = 0;
//            int offset = 0;
            while ((i = in.read(data)) != -1) {
//                Log.d("TEST","data size = " + data.length);
                out.write(data,0,i);
            }

            Log.d("TEST","len now = " + out.getChannel().size());
            in.close();
            out.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入wav文件头
     */
    private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';  //WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;   // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16;  // bits per sample
        header[35] = 0;
        header[36] = 'd'; //data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }


    public static String byteArray2HexString(byte[]data, int len){
        int printLen = len;
        final StringBuilder stringBuilder = new StringBuilder(len);

        if (data.length < len) {
//            android.util.Log.w(TAG, "data length is shorter then print command length");
            printLen = data.length;
        }
        for(int i = 0; i < printLen; i++){
            stringBuilder.append(String.format("%02X ", data[i]));
        }

        return stringBuilder.toString();
    }
}