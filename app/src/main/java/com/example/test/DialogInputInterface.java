package com.example.test;

import android.view.View;

public interface DialogInputInterface {
    View onBuildDialog();
    void onCancel();
    void onResult(View v);
}
