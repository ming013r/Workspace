package com.example.a409lab00.interactiveclassroom;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class BeaconService extends Service {
    private static final String HUB_URL = "http://"+ConfigFile.IP;///no need to +/signalr
    private static final String HUB_NAME = "eventHub";//hub class name(at asp.net file)
    private static final String HUB_EVENT_NAME = "addNewMessageToPage2";

    private SignalRFuture<Void> mSignalRFuture;
    private HubProxy mHub;
    private String mName;
    int sec=0;





    public static final String TAG = "MyService";
    private Handler handler = new Handler();
    private MyBinder mBinder = new MyBinder();
    private BluetoothAdapter mBluetoothAdapter;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    BluetoothManager bluetoothManager;
    TelephonyManager telephonyManager ;
    int counter =0;
    List<Device> beaconlist = new ArrayList<Device>();
    WebApi webapi;
    WebSocket websocket;
    int cid=-1;
    String token;
    String IMEI;
    Uri uri;
    Boolean error =false;
    @Override
    public void onCreate() {
        super .onCreate();
        webapi=new WebApi();
        websocket=new WebSocket();
        uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(ConfigFile.enableBLE)
        {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        }

        ///////////
        Runnable myRunnable =BeaconRunnable("token");
        handler.removeCallbacks(myRunnable);
        // 設定間隔的時間
        handler.postDelayed(myRunnable, 3000);
        //////////

        ///////////
        Runnable LERunnable =BLERunnable();
        handler.removeCallbacks(LERunnable);
        // 設定間隔的時間
        handler.postDelayed(LERunnable, 3000);
        //////////

        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted
        } else {
            // READ_PHONE_STATE permission is already been granted.
            telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
           IMEI= telephonyManager.getDeviceId();
        }





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
                    protected void onPostExecute(String jsonBroadcast) {
                        WebSocket.SocketBroadcastModel broadcast= websocket.getBroadcast(jsonBroadcast);
                        if(cid==websocket.getBroadcast(jsonBroadcast).cid)
                        {
                            Notify(broadcast.title+""+broadcast.type,broadcast.content);
                        }
                        super.onPostExecute(jsonBroadcast);
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
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            int startByte = 2;

            boolean patternFound = false;
            // 寻找ibeacon
            while (startByte <= 5) {

                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && // Identifies
                        // an
                        // iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { // Identifies
                    // correct
                    // data
                    // length
                    patternFound = true;
                    break;
                }
                startByte++;

            }

            if (patternFound) {
                Log.d("service","scanning");
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);


                String uuid = hexString.substring(0, 8) + "-"
                        + hexString.substring(8, 12) + "-"
                        + hexString.substring(12, 16) + "-"
                        + hexString.substring(16, 20) + "-"
                        + hexString.substring(20, 32);

                int major =(scanRecord[startByte + 20] & 0xff) * 0x100
                        + (scanRecord[startByte + 21] & 0xff);
                //在掃描過程中，判斷是否有掃到
                if (!Hasbeacon(major))
                {
                    Device newbeacon=new Device();
                    newbeacon.UUID=uuid;
                    newbeacon.major=major;
                    newbeacon.Name =device.getName();
                    newbeacon.minor=(scanRecord[startByte + 22] & 0xff) * 0x100
                            + (scanRecord[startByte + 23] & 0xff);
                    newbeacon.mac = device.getAddress();
                    newbeacon.TXPower =(scanRecord[startByte + 24]);
                    newbeacon.RSSI=rssi;
                    Log.d(TAG, "beacon UUID : "+newbeacon.UUID);

                    beaconlist.add(newbeacon);
                }




            }
        }
    };

    public boolean Hasbeacon(int major)
    {
        boolean has=false;
        for(int i = 0;i<beaconlist.size();i++)
        {
            if(beaconlist.get(i).major==major)
            {
                has=true;
            }

        }
        return has;
    }
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
    private Runnable BLERunnable()
    {

        Runnable aRunnable = new Runnable(){

            public void run(){
                sec++;
                if(sec%300==0)
                {
                    Log.d("restart","restarted");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
                handler.postDelayed(this, 3000);/////by api better
            }
        };

        return aRunnable;
    };
    private Runnable BeaconRunnable(final String token)
    {

        Runnable aRunnable = new Runnable(){

            public void run(){
                Log.d(TAG, "on"+cid);

                if(counter>=180)
                {
                    if(cid!=-1)
                    {
                        feedtoserver(token,IMEI);
                    }

                    counter=0;
                }
                counter+=3;
                if(error)
                {
                    onDestroy();
                }
                else
                {
                    handler.postDelayed(this, 3000);/////by api better
                }

            }
        };

        return aRunnable;
    };
    private void feedtoserver(String token,String IMEI)
    {

        for(int i =0 ;i<beaconlist.size();i++) {

           String response= webapi.POST("beacons/Postbeacon", "name=" + beaconlist.get(i).Name
                    + "&uuid=" + beaconlist.get(i).UUID
                    + "&rssi=" + beaconlist.get(i).RSSI
                    + "&major=" + beaconlist.get(i).major
                    + "&minor=" + beaconlist.get(i).minor
                    + "&token=" + token
                    +"&deviceAddress="+IMEI
                    + "&cid=" + cid);
        }
        beaconlist.clear();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try
        {
            cid=intent.getIntExtra("cid",-1);
            token=intent.getStringExtra("token");
        }
        catch (Exception e)
        {
            error=true;
            e.getMessage();
            Log.d(TAG, e.getMessage());
        }


        Log.d(TAG, "onStartCommand() executed-------------------------------------------------");
        // 執行任務
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MyBinder extends Binder {
        public void startDownload() {
            Log.d("TAG", "startDownload() executed");
            // 執行任務
        }
    }
    public void Notify(String title,String Message)
    {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle(title)
                        .setSound(uri)
                        .setContentText(Message);
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, BeaconService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }
}
