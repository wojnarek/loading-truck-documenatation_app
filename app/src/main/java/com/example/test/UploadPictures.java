package com.example.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class UploadPictures extends AsyncTask<ArrayList<Pictures>, String, String> {
    Context context;
    ProgressDialog pb;
    SmbFile smb;

    public boolean TRANSFER_COMPLETE = true;

    public UploadPictures(Context c) {
        this.context = c;
    }

    @Override
    protected String doInBackground(ArrayList<Pictures>... p) {

        ArrayList<Pictures> x = new ArrayList<>();
        x = p[0];


        x.forEach(k ->{

            System.out.println("uris: "+k.getPictureUris());
            System.out.println("file name: "+k.getFileName());

        });



        try {
            smb = new SmbFile(x.get(0).getSmbUrl());
            if (!smb.exists()) {
                smb.mkdirs();
            }
        } catch (MalformedURLException | SmbException e) {
            e.printStackTrace();
        }

        ArrayList<Pictures> finalX = x;
        x.forEach(pictures -> {

            try {
                //autoryzacja smb - akutalnie uzytkownik anonimowy
                //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, null, null);
                //SmbSession.logon(UniAddress.getByName("192.168.1.11"),auth);

                System.out.println(smb.getCanonicalPath());

                //sciezka pliku do przeslania z androida
                File sourceFile = new File(pictures.getPictureUris());

                //docelowy plik na serwerze smb
                SmbFile smbFileTarget = new SmbFile(smb.getCanonicalPath(), pictures.getFileName());

                //cos tam do przeslania
                FileInputStream fis = new FileInputStream(sourceFile);

                //obiekt smb do przeslania
                SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFileTarget);

                //metoda do przeslania
                try {
                    final byte[] b = new byte[16 * 1024];
                    int read = 0;
                    while ((read = fis.read(b, 0, b.length)) > 0) {
                        smbfos.write(b, 0, read);

                    }
                } finally {
                    fis.close();
                    smbfos.close();
                   // File f = new File(pictures.getPictureUris());
                   // f.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
                TRANSFER_COMPLETE = false;
            }

        });


        if(!TRANSFER_COMPLETE){
            x.forEach(k->{
                System.out.println("/storag"+k.getUritest());
                File f = new File("/storag"+k.getFileName());
                f.delete();
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            });

        }



        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pb.dismiss();

        if (TRANSFER_COMPLETE) {
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(context)
                            .setTitle("Status operacji")
                            .setMessage("Wysyłanie zakończone pomyślnie!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.show();


        } else {
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(context)
                            .setTitle("Status operacji")
                            .setMessage("Wysyłanie nie powiodło się! :(")
                            .setCancelable(false)
                            .setPositiveButton("Spróbuj ponownie", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    TRANSFER_COMPLETE = true;
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.show();
        }

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pb = new ProgressDialog(context);
        pb.setTitle("Wysyłanie na serwer..");
        pb.setCancelable(false);
        pb.setMessage("proszę czekać..");
        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pb.show();

    }


    public void deletePicturesFromGallery(ArrayList<Pictures> p){
        for (Pictures x : p){
            System.out.println(x.getPictureUris());
            File f = new File(x.getPictureUris());
            f.delete();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }


}
