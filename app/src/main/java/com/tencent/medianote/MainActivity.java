package com.tencent.medianote;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.medianote.fragment.CameraFragment;
import com.tencent.medianote.fragment.ImageDrawingFragment;
import com.tencent.medianote.fragment.MenuFragment;

import java.util.Map;

public class MainActivity extends FragmentActivity implements MenuFragment.OnMenuFragmentListener{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack();
            }
        });

        start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        start();
    }

    private boolean addVoIPPermission(Context context) {
        String[] voipPermissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        return addPermission(context, voipPermissions);
    }

    private boolean addPermission(Context context, String[] permissions) {
        if (permissions == null)
            return true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (!(context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)) {
                    try {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{ permission }, 0);
                    } catch (Exception e) {
                    }
                    return false;
                }
            }
        }
        return true;
    }



    private void start() {
        if (addVoIPPermission(this)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,MenuFragment.newInstance(),"menu").commitAllowingStateLoss();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onMenuClick(String menu,Class c) {
        try {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    (Fragment) c.newInstance(),null).addToBackStack(null).commitAllowingStateLoss();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
