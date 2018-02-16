package com.example.yongyouming.hwritequiz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    LiteAdapter helper;
    WebApi webapi;


    ////View declare
    EditText email,pswd;
    Button loginbtn;
    CheckBox remember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Permission_Ask();
        webapi =new WebApi();
        webapi.startInternet();
        helper = new LiteAdapter(this);

        email=(EditText)findViewById(R.id.email);
        pswd=(EditText)findViewById(R.id.password);
        loginbtn=(Button)findViewById(R.id.logingbtn);
        remember=(CheckBox)findViewById(R.id.rememberme);
        setUser();



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email= email.getText().toString();
                String password =pswd.getText().toString();

                if(remember.isChecked())
                    helper.updateUser(Email,password);
                else
                    helper.delete(Email);

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







    }



    void Permission_Ask()
    {
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
    }
    void setUser()
    {
        if(helper.NameList().size()!=0)
        {
            String info=helper.getData();
            String[] userInfo = info.split(",");

            email.setText(userInfo[0]);
            pswd.setText(userInfo[1]);
            remember.setChecked(true);
        }
    }
}
