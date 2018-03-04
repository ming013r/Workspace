package com.example.a409lab00.interactiveclassroom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

import static android.widget.Toast.LENGTH_SHORT;

public class MainView extends AppCompatActivity {

    private static final String HUB_URL = "http://"+ConfigFile.IP;///no need to +/signalr
    private static final String HUB_NAME = "eventHub";//hub class name(at asp.net file)
    private static final String HUB_EVENT_NAME = "addNewMessageToPage2";

    private SignalRFuture<Void> mSignalRFuture;
    private HubProxy mHub;
    private String mName;

    private Handler handler = new Handler();

    private Intent serviceIntent;
    WebSocket websocket=new WebSocket();
    WebApi webapi=new WebApi();


    private BluetoothAdapter mBluetoothAdapter;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    BluetoothManager bluetoothManager;

    String token;
    int cid;
    int counter=0;
    Boolean scanned=false;
    int beaconstatus=1;
    List<classQuestion> QuestionQueue;

    TextView btn_status;
    int sec=0;

    boolean loginSent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        btn_status=(TextView)findViewById(R.id.destroy);



        Intent it =getIntent();
        token = it.getStringExtra("token");
        cid=it.getIntExtra("cid",0);


        serviceIntent = new Intent(this, BeaconService.class);
        serviceIntent.putExtra("cid",cid);
        serviceIntent.putExtra("token",token);
        this.startService(serviceIntent);


        if(ConfigFile.enableBLE)
        {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }



        ///////////
        Runnable myRunnable =BeaconRunnable();
        handler.removeCallbacks(myRunnable);
        // 設定間隔的時間
        handler.postDelayed(myRunnable, 3000);
        //////////
        /*
        new Thread(new Runnable() {
            public void run() {
                btn_status.post(new Runnable() {
                    public void run() {
                        btn_status.setText("Counter:"+counter);
                        logtoServer();
                        btn_status.postDelayed(this, 1000);/////by api better
                    }
                });
            }
        }).start();
*/
        //////////





/*  Stop service trigger

        btn_status.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopService(serviceIntent);
            }
        });
*/

        String broadcastJson=webapi.GET("Broadcast_reply/getBroadcast?cid="+cid);
        List<Broadcast> broadcasts=getBroadcast(broadcastJson);


        Button btnTest=(Button)findViewById(R.id.getQuestion);
        List<QGroup> ques=currentQuestions(cid);
        btnTest.setText("作答("+ques.size()+")");
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it =new Intent();
                it.setClass(MainView.this,GroupEntry.class);
                it.putExtra("way",1);
                it.putExtra("cid",cid);
                it.putExtra("token",token);
                startActivity(it);
            }
        });
        Button btnReview=(Button)findViewById(R.id.review);
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it =new Intent();
                it.setClass(MainView.this,ReviewGroup.class);
                it.putExtra("way",2);
                it.putExtra("cid",cid);
                it.putExtra("token",token);
                startActivity(it);
            }
        });
        Button btnBroad=(Button)findViewById(R.id.checkbroad);
        btnBroad.setText("公告("+broadcasts.size()+")");
        btnBroad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jsonQuestion=webapi.GET("Broadcast_reply/getBroadcast?cid="+cid);
                Intent ITquestion=new Intent();
                ITquestion.setClass(MainView.this,BroadNav.class);
                ITquestion.putExtra("jsonQ",jsonQuestion);
                ITquestion.putExtra("token",token);
                startActivity(ITquestion);
            }
        });


        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        //可以理解為訊息or事件監聽器
        mHub.on(HUB_EVENT_NAME, new SubscriptionHandler1<String>() {
            @Override
            public void run(String name) {
                //使用AsyncTask來更新畫面
                new AsyncTask<String,Void,String>(){
                    @Override
                    protected String doInBackground(String... param) {
                        String questionData =param[0];
                        return questionData;
                    }
                    @Override
                    protected void onPostExecute(String jsonQuestion) {
                       if(cid==websocket.getCid(jsonQuestion))
                       {
                           Intent ITquestion=new Intent();
                           ITquestion.setClass(MainView.this,PopQuestion.class);
                           ITquestion.putExtra("way",0);
                           ITquestion.putExtra("jsonQ",jsonQuestion);
                           ITquestion.putExtra("Gtitle",websocket.getTitle(jsonQuestion));
                           ITquestion.putExtra("token",token);
                           startActivity(ITquestion);
                       }





                        super.onPostExecute(jsonQuestion);
                    }
                }.execute(name);
            }
        }, String.class);

        //開啟連線
        try {
            mSignalRFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }

    private Runnable BeaconRunnable()
    {
        Runnable aRunnable = new Runnable(){

            public void run(){
                counter+=10;
                if(counter%30==0)
                {
                    if(scanned)
                    {
                        changeBeaconStatus();
                        btn_status.setText(BeaconStatus());
                        if(!loginSent)
                        {
                            webapi.GET("LogAPI/SaveLog?name=Login"+"&content=BeaconSuccess"+"&token="+token);
                            loginSent=true;
                        }
                    }
                    else
                    {
                        if(!loginSent)
                        {
                            webapi.GET("LogAPI/SaveLog?name=Login"+"&content=BeaconNotFound"+"&token="+token);
                            loginSent=true;
                        }
                    }
                    scanned=false;
                }

                handler.postDelayed(this, 1000);/////by api better


            }
        };

        return aRunnable;
    };
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            int startByte = 2;
            boolean patternFound = false;
            // 寻找ibeacon
            while (startByte <= 5) {

                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 &&
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) {
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);
                int major =(scanRecord[startByte + 20] & 0xff) * 0x100
                        + (scanRecord[startByte + 21] & 0xff);
                //在掃描過程中，判斷是否有掃到
                scanned=true;

            }
        }
    };
    private static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private  void logtoServer()
    {
        counter+=10;
        if(counter%30==0)
        {
            if(scanned)
            {
                changeBeaconStatus();
                btn_status.setText(BeaconStatus());
                if(!loginSent)
                {
                    webapi.GET("LogAPI/SaveLog?name=Login"+"&content=BeaconSuccess"+"&token="+token);
                    loginSent=true;
                }
            }
            else
            {
                if(!loginSent)
                {
                    webapi.GET("LogAPI/SaveLog?name=Login"+"&content=BeaconNotFound"+"&token="+token);
                    loginSent=true;
                }
            }
            scanned=false;
        }


    }
    @Override
    protected void onDestroy() {
        //關閉連線
        mSignalRFuture.cancel();
        super.onDestroy();
    }
    public void changeBeaconStatus()
    {
        beaconstatus++;
        if(beaconstatus>4)
            beaconstatus=1;
    }
    public String BeaconStatus()
    {
        String returnVal="|";
        switch (beaconstatus)
        {
            case 1:
                returnVal="|";
                break;
            case 2:
                returnVal="/";
                break;
            case 3:
                returnVal="-";
                break;
            case 4:
                returnVal="\\";
                break;
        }

        return returnVal;
    }
    public List<Broadcast> getBroadcast(String jsonstring)
    {

        List<Broadcast> broadcasts=new ArrayList<Broadcast>();
        try{
            JSONArray jsonArray =new JSONArray(jsonstring);

            for(int i=0;i<jsonArray.length();i++)
            {
                Broadcast broadcast=new Broadcast();
                JSONObject broad =jsonArray.getJSONObject(i);
                broadcast.id=Integer.parseInt(broad.getString("id"));
                broadcast.title=broad.getString("title");
                broadcast.content=broad.getString("content");
                broadcast.type=broad.getString("type");

                broadcasts.add(broadcast);
            }

        }catch(JSONException e){
            e.printStackTrace();

        }


        return broadcasts;
    }
    List<QGroup> currentQuestions(int cid)
    {
        WebApi webapi =new WebApi();



        String  jsonGroups=webapi.GET("QuestionAPI/GetQuestionGroup?cid="+cid);

        List<QGroup> groups =new ArrayList<QGroup>();
        try{
            JSONArray jsonArray =new JSONArray(jsonGroups);
            for(int i=0;i<jsonArray.length();i++)
            {
                QGroup group=new QGroup();
                JSONObject que =jsonArray.getJSONObject(i);

                group.id=Integer.parseInt(que.getString("id"));
                group.title=que.getString("title");
                group.stop=que.getString("stop");

                groups.add(group);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }


        return groups;
    }
    class QGroup
    {
        public int id;
        public String title;
        public  String stop;
        @Override
        public String toString()
        {
            return "["+title+"]"+"　　結束日期："+stop;
        }
    }
}
