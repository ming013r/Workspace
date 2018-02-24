package com.example.yongyouming.hwritequiz;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CheckVersion extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    WebApi webapi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_version);
        webapi =new WebApi();
        webapi.startInternet();
        verifyStoragePermissions(CheckVersion.this);


    }
    void Download() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/HWriteQuiz.apk";
        try {
            URL url = new URL("http://"+ConfigFile.IP+"/api/UtilApi/updateAPK_quiz");
            URLConnection connection = url.openConnection();
            connection.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("YourApp", "Well that didn't work out so well...");
            Log.e("YourApp", e.getMessage());
        }
        Execute(path);
    }


    void Execute(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        else
        {
            String version=webapi.GET("UtilAPI/VersionCode_quiz");
            if(ConfigFile.version.equals(version))
            {
                Intent it =new Intent();
                it.setClass(CheckVersion.this,Login.class);
                startActivity(it);
            }
            else
            {
                Download();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        int index = 0;
        Map<String, Integer> PermissionsMap = new HashMap<String, Integer>();
        for (String permission : permissions){
            PermissionsMap.put(permission, grantResults[index]);
            index++;
        }

        if((PermissionsMap.get(WRITE_EXTERNAL_STORAGE) != 0)
                || PermissionsMap.get(READ_EXTERNAL_STORAGE) != 0){
            Toast.makeText(this, "此程式必須要存取您的資料", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            String version=webapi.GET("UtilAPI/VersionCode_quiz");

            if(ConfigFile.version.equals(version))
            {
                Intent it =new Intent();
                it.setClass(CheckVersion.this,Login.class);
                startActivity(it);
            }
            else
            {
                Download();
            }
        }
    }
}
