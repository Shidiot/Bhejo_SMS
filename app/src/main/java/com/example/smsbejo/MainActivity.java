package com.example.smsbejo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button openfilebtn, excelbtn;
    static TextView pathf;
    Intent openf;
    public Uri address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openfilebtn = (Button)findViewById(R.id.button);
        pathf = (TextView)findViewById(R.id.textView);
        excelbtn = (Button)findViewById(R.id.button2);
        excelbtn.setClickable(false);

        int permcheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck + permcheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.SEND_SMS},
                    1);
        }
    /*    if (permcheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    1);
        } */

        pathf.setMovementMethod(new ScrollingMovementMethod());

        openfilebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                excelbtn.setClickable(true);
                excelbtn.setText("Send SMS from above CSV file");
                openf = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                openf.addCategory(Intent.CATEGORY_OPENABLE);
                openf.setType("*/*");
                startActivityForResult(openf,10);
            }
        });

        excelbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                excelbtn.setClickable(false);
                excelbtn.setText("Wait...");
                if(address == null) {
                    pathf.setText("Select file first");
                    excelbtn.setText("No file selected");
                }
                else{
                    try {
                        readcsv(address);
                        excelbtn.setText("Success");
                        //simple(address);
                    } catch (IOException e) {
                        pathf.setText("Error Occurred\nContact Developer");
                        excelbtn.setText("Operation failed successfully");
                        e.printStackTrace();
                    }
                }
                address = null;
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode == 10){
            Uri path = null;
            if (data !=null)
                path = data.getData();
            String ss = path.getPath().split(":")[1];
            /*
            if (!ss.contains("/storage/emulated/0/")) {
                ss = "/storage/emulated/0/" + ss;
                pathf.setText(ss+" |");
            }else{
                pathf.setText(ss);
            }
             */
            pathf.setText(ss);
            address = path;
        }
    }

    public void readcsv(Uri path) throws IOException {
        String ss = path.getPath().split(":")[1];
        if (!ss.contains("/storage/emulated/0/"))
            ss = "/storage/emulated/0/" + ss;
        CSVReader csv = new CSVReader(new FileReader(ss));
        List<String[]> lst = csv.readAll();
        int i;
        for (i = 0; i < lst.size(); i++)
            sendbulk(lst.get(i)[0], lst.get(i)[1]);

        pathf.setText("Total: " + lst.size() + " Sent: " + (i) + " Baki "+(lst.size() - i));
        Toast.makeText(getApplicationContext(), "Total: " + lst.size() + " Gye: " + (i) + " Baki: " + (lst.size() - i),
                Toast.LENGTH_LONG).show();
        csv.close();
    }

    public void sendbulk(String no, String ms){

        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //PendingIntent pi = getActivity(getApplicationContext(), 0, intent,0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(no,null, ms, null, null);
    }

    /*
    public void simple(Uri path){
        String ss = path.getPath().split(":")[1];
        if(!ss.contains("/storage/emulated/0/"))
            ss = "/storage/emulated/0/" + ss;
        String content[]=null, no, msg;
        try {
            content = new String(Files.readAllBytes(Paths.get(ss))).split("\n");
        } catch (IOException e) {
            pathf.setText("file not found"+"\n"+"Path used: ");
            //excelbtn.setVisibility(View.VISIBLE);
            excelbtn.setClickable(false);
            excelbtn.setText("Contact Developer");
            e.printStackTrace();
        }
        int i;
        String txt="";
        for(i=0; i<content.length; i++){
            no = content[i].split(",")[0];
            msg = content[i].split(",")[1];
            sendbulk(no, msg);
            //txt += no+" "+msg+"\n";
        }
        pathf.setText("Total: " + content.length + " Sent: " + (i));
        //pathf.setText(txt);
    }
    */

}
