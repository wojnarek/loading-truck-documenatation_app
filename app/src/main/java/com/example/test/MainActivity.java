package com.example.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private static final String adminpasswd = "zebra2022";


    public static final int MAX_PHOTO_NUMBER = 3;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    Button cameraBtn, settingsBtn;
    String currentPhotoPath;
    String zamowienieName = null;
    public ArrayList<String> images = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        settingsBtn = (Button) findViewById(R.id.settingsBtn);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));


        images.clear();

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                jezeli brak numeru pobrania (zamowienieName) to oznacze ze proces jest nie rozpoczety i aplikacji prosi o numer pobrania, w przeciwnym przypadku kontynuuje rozpoczety proces
                ale to juz nie ma sensu bo gdy przerwiemy proces w trakcie to wszystko sie resetuje wiec else sie nigdy nie wykona
                 */

                if (zamowienieName == null) {

                    //dialog window z pole do wpisania numberu pobrania ograniczony do samych cyfr i max 1 lini, dziala takze ze skanerem

                    promptForResult("Numer pobrania", "Użyj skanera lub wpisz ręcznie", new DialogInputInterface() {

                        EditText editTextBarcode;

                        @Override
                        public View onBuildDialog() {

                            editTextBarcode = new EditText(MainActivity.this);
                            editTextBarcode.setMaxLines(1);
                            editTextBarcode.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

                            View v = (View) editTextBarcode;

                            return v;
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onResult(View v) {
                            if (editTextBarcode.getText().toString().isEmpty()) {
                                Toast.makeText(MainActivity.this, "Numer pobrania nie może być pusty!", Toast.LENGTH_LONG).show();
                            } else {
                                zamowienieName = editTextBarcode.getText().toString();
                                System.out.println(zamowienieName);
                                verifyPermissions();
                            }
                        }
                    });
                } else {
                    verifyPermissions();
                }

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                promptForResult("Podaj poświadczenia", null, new DialogInputInterface() {

                    EditText haslo;

                    @Override
                    public View onBuildDialog() {

                        // login = new EditText(MainActivity.this);
                        haslo = new EditText(MainActivity.this);
                        haslo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        View v = (View) haslo;

                        return v;
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onResult(View v) {
                        if (!haslo.getText().toString().equals(adminpasswd)) {
                            Toast.makeText(MainActivity.this, "BLEDNE HASLO ADMINISTRATORA!", Toast.LENGTH_LONG).show();
                        } else {
                            Intent i = new Intent(MainActivity.this, AppSettings.class);
                            startActivity(i);
                        }


                    }
                });


            }
        });


    }



    //weryfikacja uprawnien do aparatu ktora jednoczesnie uruchamia intencje
    public void verifyPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    CAMERA_PERM_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permisje dotyczące kamery są wymagane do jej używania!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST_CODE) {

            //wywolanie aktywnosci pozytywne, aktywnosc przerwana wszystko sie zeruje i proces zaczynany jest od nowa

            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(MainActivity.this,"PRZERWANO PROCES",Toast.LENGTH_LONG).show();
                    images.clear();
                    zamowienieName = null;
            }


            //aktywnosc zakonczona pomyslnie, stworzenie zdjecia, uruchomienie aktywnosci zwiazanej z wysylaniem, przeslanie listy zdjec
            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(this, images.size() + 1 + "/3 ", Toast.LENGTH_LONG).show();

                File f = new File(currentPhotoPath);

                Log.d("tag", "Sciezka absolutna zdjecia -> " + Uri.fromFile(f));

                String imageuri = String.valueOf(Uri.fromFile(f));
                images.add(imageuri);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                //gdy lista uri zdjec osiaga rozmiar 3 uruchamiana jest aktywnosc SendPictures, w przeciwnym wypadku uruchamiana jest jest aktywnosc zdjec
                if (images.size() == MAX_PHOTO_NUMBER) {
                    Intent i = new Intent(this, SendPictures.class);
                    i.putExtra("imguris", images);
                    i.putExtra("nrpobrania", zamowienieName);
                    startActivity(i);

                } else {
                    dispatchTakePictureIntent();
                }
            }


        }
    }


    //tworzenie zdjecia
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("ddMMyyy").format(new Date());
        String imageFileName = zamowienieName + "_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* katalog */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    //uruchomienie aktywnosci aparatu
    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            //zdjecie istnieje -> continue
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }



    //metoda do wyswietlania window dialogu do odebrania numeru pobrania
    void promptForResult(String dlgTitle, String dlgMessage, final DialogInputInterface dlg) {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(dlgTitle);
        alert.setMessage(dlgMessage);
        alert.setCancelable(false);

        final View v = dlg.onBuildDialog();

        if (v != null) {
            alert.setView(v);
        }

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dlg.onResult(v);
                dialog.dismiss();
                return;
            }
        });

        alert.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dlg.onCancel();
                dialog.dismiss();
                return;
            }
        });
        alert.show();
    }



}