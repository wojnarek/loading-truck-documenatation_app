package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AppSettings extends AppCompatActivity {

    public static final String serverConfig = "serverAddressCfg";

    public String newNetworkDriveAddress = null;

    public String newNetworkDriveDestinationFolder = null;

    TextView tvNetworkDriveAddres, tvNetowrkDriveDestinationFolder;
    Button networkDriveAddress, netowrkDriveDestinationFolder, saveBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appsettings_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        tvNetowrkDriveDestinationFolder = (TextView) findViewById(R.id.tvNetowrkDriveDestinationFolder);
        tvNetworkDriveAddres = (TextView) findViewById(R.id.tvNetworkDriveAddress);

        netowrkDriveDestinationFolder = (Button) findViewById(R.id.changeBtn2);
        networkDriveAddress = (Button) findViewById(R.id.changeNetoworkDriveAddressBtn);

        saveBtn = findViewById(R.id.saveBtn);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(serverConfig, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();


        tvNetworkDriveAddres.setText(pref.getString("serverAddress", null));
        tvNetowrkDriveDestinationFolder.setText(pref.getString("destinationFolder", null));


        networkDriveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                promptForResult("Zmiana adresu dysku sieciowego", "Nowy adres:", new DialogInputInterface() {
                    EditText newServerAddress;

                    @Override
                    public View onBuildDialog() {
                        newServerAddress = new EditText(AppSettings.this);
                        View v = (View) newServerAddress;
                        return v;
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onResult(View v) {
                        newNetworkDriveAddress = newServerAddress.getText().toString();
                        System.out.println(newNetworkDriveAddress);

                        edit.putString("serverAddress", newNetworkDriveAddress);
                        edit.apply();
                        edit.commit();

                        tvNetworkDriveAddres.setText(pref.getString("serverAddress", null));


                        Toast.makeText(AppSettings.this, "Zaktualizowano adres dysku sieciowego!", Toast.LENGTH_LONG).show();

                    }
                });


            }
        });


        netowrkDriveDestinationFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForResult("Zmiana domyslnego folderu", "Nowy folder:", new DialogInputInterface() {
                    EditText newDestinationFolder;

                    @Override
                    public View onBuildDialog() {
                        newDestinationFolder = new EditText(AppSettings.this);
                        View v = (View) newDestinationFolder;
                        return v;
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onResult(View v) {
                        newNetworkDriveDestinationFolder = newDestinationFolder.getText().toString();
                        System.out.println(newNetworkDriveDestinationFolder);

                        edit.putString("destinationFolder", newNetworkDriveDestinationFolder);
                        edit.apply();
                        edit.commit();

                        tvNetowrkDriveDestinationFolder.setText(pref.getString("destinationFolder", null));

                        Toast.makeText(AppSettings.this, "Zaktualizowano domyslny folder!", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    void promptForResult(String dlgTitle, String dlgMessage, final DialogInputInterface dlg) {
        // replace "MyClass.this" with a Context object. If inserting into a class extending Activity,
        // using just "this" is perfectly ok.
        AlertDialog.Builder alert = new AlertDialog.Builder(AppSettings.this);
        alert.setTitle(dlgTitle);
        alert.setMessage(dlgMessage);
        // build the dialog
        final View v = dlg.onBuildDialog();
        // put the view obtained from the interface into the dialog
        if (v != null) {
            alert.setView(v);
        }
        // procedure for when the ok button is clicked.
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dlg.onResult(v);
                dialog.dismiss();
                return;
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dlg.onCancel();
                dialog.dismiss();
                return;
            }
        });
        alert.show();
    }
}
