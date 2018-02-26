package com.example.a409lab00.interactiveclassroom;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    LiteAdapter helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn =(Button)findViewById(R.id.btn_login);
        helper = new LiteAdapter(this);
        setUser();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    200);
        }


        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);

        }





        //btnclock
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                EditText email=(EditText)findViewById(R.id.email);
                String Email=email.getText().toString();
                EditText pswd=(EditText)findViewById(R.id.pswd);
                String password=pswd.getText().toString();


                CheckBox rememberMe =(CheckBox)findViewById(R.id.remem);
                if(rememberMe.isChecked())
                    helper.updateUser(Email,password);
                else
                    helper.delete(Email);
                WebApi webapi=new WebApi();


                String token=webapi.POST("AccountApi/getToken","email="+Email+"&pswd="+password);

                if(token.length()==8)
                {
                    Intent it =new Intent();
                    it.setClass(Login.this,CourseEntry.class);
                    it.putExtra("token",token);
                    startActivity(it);
                }
                else
                {
                    Toast.makeText(Login.this, token , Toast.LENGTH_SHORT).show();
                }

            }
        });


        final Button btn_autologin=(Button)findViewById(R.id.btn_loginauto);
        btn_autologin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                EditText email=(EditText)findViewById(R.id.email);
                Button btnn=(Button)findViewById(R.id.btn_loginauto);
                EditText pswd=(EditText)findViewById(R.id.pswd);
                // btnn.setText("UselessButton");
                email.setText("teacher@gmail.com");
                pswd.setText("swater0");
            }
        });

    }


    void setUser()
    {
        if(helper.NameList().size()!=0)
        {
            String info=helper.getData();
            String[] userInfo = info.split(",");
            EditText email=(EditText)findViewById(R.id.email);
            EditText pswd=(EditText)findViewById(R.id.pswd);

            email.setText(userInfo[0]);
            pswd.setText(userInfo[1]);
            CheckBox rememberMe =(CheckBox)findViewById(R.id.remem);
            rememberMe.setChecked(true);
        }
    }

}
