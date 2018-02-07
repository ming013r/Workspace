package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class WatchVideo extends AppCompatActivity {
    WebView displayYoutubeVideo;
    @Override
    public void onPause() {
        super.onPause();
        displayYoutubeVideo.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayYoutubeVideo.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        Intent getEX=getIntent();

        String Name = getEX.getStringExtra("Name");
        final String URL = getEX.getStringExtra("URL");


        TextView name=(TextView)findViewById(R.id.vidName);
        name.setText(Name);

        String frameVideo = "<html><body>myVIdeo<br><iframe width=\"349\" height=\"256\" src=\""+URL+"\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        displayYoutubeVideo = (WebView) findViewById(R.id.mWebView);

        displayYoutubeVideo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = displayYoutubeVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        displayYoutubeVideo.loadData(frameVideo, "text/html", "utf-8");


        Button toYoutube =(Button )findViewById(R.id.toYoutube);
        toYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri= Uri.parse(URL);
                Intent i=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
            }
        });
    }

}
