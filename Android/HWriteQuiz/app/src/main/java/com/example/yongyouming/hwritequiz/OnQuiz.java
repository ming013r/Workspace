package com.example.yongyouming.hwritequiz;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import uk.co.senab.photoview.PhotoViewAttacher;


public class OnQuiz extends AppCompatActivity {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String mCameraFileName;
    private Uri outuri;
    private String newPicFile;

    WebApi webapi;
    int qid,cid;
    String token;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_quiz);

        askPermission();


        setParas();

        Button upload =(Button)findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OnQuiz.this,PostAnswer(mCameraFileName,token,String.valueOf(qid)),Toast.LENGTH_SHORT).show();
            }
        }) ;


        Button capture=(Button)findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Capture();
            }
        });
        TextView qname =(TextView)findViewById(R.id.Qname);
        Intent it = getIntent();
        String token =it.getStringExtra("Token");
        String qid ="";
        qid =it.getStringExtra("Qid");
        qname.setText("試卷名稱"+GetName(qid));



        final ImageView quiz =(ImageView)findViewById(R.id.imageView);
        Glide.with(this).load(Constants.dURL+GetImage(qid)).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                quiz.setImageBitmap(resource);
            }
        });
        //zoom
        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(quiz);
        pAttacher.update();


    }
    private void Capture() {
        Intent intent = new Intent();
        // Picture from camera
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        // This is not the right way to do this, but for some reason, having
        // it store it in
        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("-mm-ss");

        newPicFile = "Bild"+ df.format(date) + ".jpg";
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);


        File outFile = new File(dir+"/Camera",newPicFile);

        mCameraFileName = outFile.toString();
        outuri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);


        startActivityForResult(intent, 1);
    }



    void setParas()
    {
        webapi=new WebApi();

        Intent it_prev=getIntent();
        qid=it_prev.getIntExtra("qid",-1);
        cid=it_prev.getIntExtra("cid",-1);
        token =it_prev.getStringExtra("token");
    }
    public String GetName(String qid)
    {
        String Name="";
        try{
            JSONArray jsonArray =new JSONArray(webapi.GET("QuizsApi/GetQuizPart?status=1&qid="+qid+"&token="));
            JSONObject currentQuiz=jsonArray.getJSONObject(0);
            Name=currentQuiz.getString("Name");

            //Toast.makeText(StartQuiz.this, qid, Toast.LENGTH_SHORT).show();
        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(OnQuiz.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return Name;
    }
    public String GetImage(String qid)
    {
        String Image="";
        try{
            JSONArray jsonArray =new JSONArray(webapi.GET("QuizsApi/GetQuizPart?status=1&qid="+qid+"&token="));
            JSONObject currentQuiz=jsonArray.getJSONObject(0);
            Image=currentQuiz.getString("path");
        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(OnQuiz.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return Image;
    }
    public String PostAnswer(String path, String token, String qid)
    {
        final String url =webapi.target+"QuizsApi/PostAnswer";
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        ///


        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(OnQuiz.this,uploadId, Constants.UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", "myname") //Adding text parameter to the request
                    .addParameter("qid",qid)
                    .addParameter("token",token)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            return "Success";
        } catch (Exception exc) {

            return exc.getMessage();
        }
        ///
    }
    void askPermission()
    {

        int permission = ActivityCompat.checkSelfPermission(OnQuiz.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    OnQuiz.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
