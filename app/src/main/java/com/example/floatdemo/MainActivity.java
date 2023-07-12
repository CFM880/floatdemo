package com.example.floatdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FloatDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.test_float).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFloatPermission();
            }
        });
        checkFloatPermission();
    }

    private void checkFloatPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, FloatService.class));
                } else {
                    startService(new Intent(this, FloatService.class));
                }
                finish();
            } else {
                showConfirmDialog(this, "请允许" + getResources().getString(R.string.app_name) + "在其他app上显示");
            }
        } else {
            startService(new Intent(this, FloatService.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showConfirmDialog(Context context, String message) {
        Dialog dialog = new AlertDialog.Builder(context).setCancelable(true).setTitle("")
                .setMessage(message)
                .setPositiveButton("现在去开启",
                        (dialog12, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("package:" + context.getPackageName()));
                            context.startActivity(intent);
                            dialog12.dismiss();
                        }).setNegativeButton("暂不开启",
                        (dialog1, which) -> dialog1.dismiss()).create();
        dialog.show();
    }

}