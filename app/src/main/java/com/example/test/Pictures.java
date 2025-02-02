package com.example.test;

import java.io.File;

public class Pictures {
    public String pictureUris;
    public String fileName;
    public String smbUrl;


    public Pictures(String pictureUris, String fileName, String smbUrl) {
        String temp = pictureUris.substring(7);
        File f = new File(temp);
        fileName = f.getName();
        this.pictureUris = pictureUris;
        this.fileName = fileName;
        this.smbUrl = smbUrl;
    }


    public String getPictureUris() {
       pictureUris = pictureUris.substring(7);
        return pictureUris;
    }


    public String getUritest(){
        return pictureUris;
    }


    public void setPictureUris(String pictureUris) {
        pictureUris = pictureUris.substring(7);
        this.pictureUris = pictureUris;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String pictureUris, String fileName) {
        pictureUris = pictureUris.substring(7);
        File f = new File(pictureUris);
        fileName = f.getName();
        this.fileName = fileName;
    }

    public String getSmbUrl() {
        return smbUrl;
    }

    public void setSmbUrl(String smbUrl) {
        this.smbUrl = smbUrl;
    }
}
