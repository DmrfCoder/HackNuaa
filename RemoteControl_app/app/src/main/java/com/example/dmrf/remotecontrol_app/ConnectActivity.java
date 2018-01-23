package com.example.dmrf.remotecontrol_app;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class ConnectActivity extends Activity {
    EditText ipET;
    EditText socketET;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ipET = (EditText) findViewById(R.id.et_ip);
        socketET = (EditText) findViewById(R.id.et_port);
        button = (Button) findViewById(R.id.bt_connect);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipnum = ipET.getText().toString();
                int socketnum = Integer.parseInt(socketET.getText().toString());
                Settings.ipnum = ipnum;
                Settings.scoketnum = socketnum;
                try {
                    //首先创建一个DatagramSocket对象
                    DatagramSocket socket = new DatagramSocket();
                    //创建一个InetAddree
                    InetAddress serverAddress = InetAddress.getByName(ipnum);
                    Intent intent = new Intent(ConnectActivity.this, ControlActivity.class);
                    ConnectActivity.this.startActivity(intent);
                    ConnectActivity.this.finish();
                    Toast.makeText(ConnectActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }


}