package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SendPictures extends AppCompatActivity {

    ImageView cargoImage1, cargoImage2,cargoImage3;
    Button sendB;
    TextView nrPobrania, tv2;
    ScrollView sv;
    LinearLayout ll;
    public static final String serverConfig = "serverAddressCfg";

    public ArrayList<Pictures> picturesArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ll = findViewById(R.id.LinearLayout1);

        nrPobrania = (TextView) findViewById(R.id.nrPobrania);
        tv2 = findViewById(R.id.textView2);

        sendB = (Button) findViewById(R.id.sendButton);


        cargoImage1 = (ImageView) findViewById(R.id.cargoPicture1);
        cargoImage2 = (ImageView) findViewById(R.id.cargoPicture2);
        cargoImage3 = (ImageView) findViewById(R.id.cargoPicture3);
        sv = (ScrollView) findViewById(R.id.ScrollView1);

        Intent intent = getIntent();
        ArrayList<String> pictureUris = intent.getStringArrayListExtra("imguris");
        String pobranie = intent.getStringExtra("nrpobrania");
        System.out.println(pictureUris);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        nrPobrania.setText(pobranie);

        cargoImage1.setImageURI(Uri.parse(pictureUris.get(0)));
        cargoImage2.setImageURI(Uri.parse(pictureUris.get(1)));
        cargoImage3.setImageURI(Uri.parse(pictureUris.get(2)));



        SharedPreferences pref = getSharedPreferences(serverConfig,MODE_PRIVATE);
        System.out.println("ADRES SERVERA"+pref.getString("serverAddress",null));

        picturesArrayList = new ArrayList<>();





        sendB.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {

              //  ll.setGravity(Gravity.CENTER);
             //   sendB.setVisibility(View.GONE);
            //    sv.setVisibility(View.GONE);
             //   nrPobrania.setVisibility(View.GONE);
            //    tv2.setVisibility(View.GONE);

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                timeStamp = pobranie + "_"+timeStamp;

                //url do samby
                String serverIp = pref.getString("serverAddress",null);
                String destinationFolder = pref.getString("destinationFolder",null);

                String url = "smb://"+serverIp+"/"+destinationFolder+"/" + timeStamp + "/";

                for (String x : pictureUris) {
                    picturesArrayList.add(new Pictures(x,x,url));
                }

                new UploadPictures(SendPictures.this).execute(picturesArrayList);

                for (String x : pictureUris) {
                    File f = new File(x);
                    f.delete();
                    deleteCapturedImages(x,f);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                }
            }
        });





    }

    public void deleteCapturedImages(String photoUri, File file) {
        file.delete();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }


}

