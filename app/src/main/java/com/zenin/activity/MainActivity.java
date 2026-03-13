package com.zenin.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.zenin.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Handler;

import androidx.cardview.widget.CardView;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import com.zenin.libhelper.FileCopyTask;


public class MainActivity extends Activity {

    private static final String BGMI_PACKAGE = "com.pubg.imobile";
    private static final int USER_ID = 0;

    private BlackBoxCore blackBoxCore;
    private Button starthack;
    private TextView Enc;
    private ImageView myIcon;
    private ProgressDialog progressDialog;
    private FileCopyTask fileCopyTask;
    public static native String exdate();

    @SuppressLint("NewApi")
  @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize BlackBoxCore
    blackBoxCore = BlackBoxCore.get();
    blackBoxCore.doCreate();

    // Initialize UI elements
    starthack = findViewById(R.id.starthack);
    Enc = findViewById(R.id.Enc);
    myIcon = findViewById(R.id.myIcon);

    // Initialize Twitter and Browser buttons
    ImageView twitterButton = findViewById(R.id.btn_twitter);
    ImageView browserButton = findViewById(R.id.btn_via_browser);
        
        


    // Setup Progress Dialog
    fileCopyTask = new FileCopyTask(this);

    // Set button listeners
    if (starthack != null) {
        starthack.setOnClickListener(view -> handleStart());
    } else {
        Toast.makeText(this, "Start button not found!", Toast.LENGTH_SHORT).show();
    }

    // Set Twitter button listener to launch cloned Twitter
    if (twitterButton != null) {
        twitterButton.setOnClickListener(view -> launchClonedApp("com.twitter.android"));
    } else {
        Toast.makeText(this, "Twitter button not found!", Toast.LENGTH_SHORT).show();
    }

   
    // Set Browser button listener to launch cloned Via Browser
    if (browserButton != null) {
    browserButton.setOnClickListener(view -> launchClonedApp("mark.via"));
    } else {
    Toast.makeText(this, "Browser button not found!", Toast.LENGTH_SHORT).show();
    }
    
    countDownStart();

}

private void launchClonedApp(String packageName) {
    // Check if the app is already cloned inside BlackBoxCore
    if (!blackBoxCore.isInstalled(packageName, USER_ID)) {
        // Clone the app before launching
        Toast.makeText(this, "Cloning " + packageName + "...", Toast.LENGTH_SHORT).show();
        InstallResult installResult = blackBoxCore.installPackageAsUser(packageName, USER_ID);

        if (!installResult.success) {
            Toast.makeText(this, "Cloning failed: " + installResult.msg, Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, packageName + " cloned successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    // Launch the cloned app
    try {
        boolean success = blackBoxCore.launchApk(packageName, USER_ID);
        if (!success) {
            Toast.makeText(this, "Failed to launch cloned app: " + packageName, Toast.LENGTH_SHORT).show();
        }
    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Error launching cloned app: " + packageName, Toast.LENGTH_SHORT).show();
    }
}


    
    private void copyObbFilesAndLaunch() {
        fileCopyTask.copyObbFolderAsync(BGMI_PACKAGE, success -> {
            if (success) {
                Toast.makeText(this, "OBB copied successfully!", Toast.LENGTH_SHORT).show();
                launchGame();
            } else {
                Toast.makeText(this, "Failed to copy OBB!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void handleStart() {
    if (blackBoxCore.isInstalled(BGMI_PACKAGE, USER_ID)) {
        // If already installed, just copy OBB and launch the game
        copyObbFilesAndLaunch();
    } else {
        // If not installed, install it first
        installGame();
    }
}


    private void installGame() {
        Toast.makeText(this, "Installing BGMI...", Toast.LENGTH_SHORT).show();
        InstallResult installResult = blackBoxCore.installPackageAsUser(BGMI_PACKAGE, USER_ID);

        if (installResult.success) {
            Toast.makeText(this, "BGMI Installed Successfully!", Toast.LENGTH_SHORT).show();
            copyObbFilesAndLaunch();
        } else {
            Toast.makeText(this, "Installation Failed: " + installResult.msg, Toast.LENGTH_SHORT).show();
        }
    }


    private void launchGame() {
        boolean success = blackBoxCore.launchApk(BGMI_PACKAGE, USER_ID);
        if (success) {
            Toast.makeText(this, "BGMI launched successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to launch BGMI!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void countDownStart() {
    Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date expiryDate = dateFormat.parse(exdate());
                    long now = System.currentTimeMillis();
                    long distance = expiryDate.getTime() - now;
                    long days = distance / (24 * 60 * 60 * 1000);
                    long hours = distance / (60 * 60 * 1000) % 24;
                    long minutes = distance / (60 * 1000) % 60;
                    long seconds = distance / 1000 % 60;
                    if (distance < 0) {
                    } else {
                        TextView Hari = findViewById(R.id.tv_d);
                        TextView Jam = findViewById(R.id.tv_h);
                        TextView Menit = findViewById(R.id.tv_m);
                        TextView Detik = findViewById(R.id.tv_s);
                        if (days > 0) {
                            Hari.setText(String.format("%02d", days));
                        }
                        if (hours > 0) {
                            Jam.setText(String.format("%02d", hours));
                        }
                        if (minutes > 0) {
                            Menit.setText(String.format("%02d", minutes));
                        }
                        if (seconds > 0) {
                            Detik.setText(String.format("%02d", seconds));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   blackBoxCore.uninstallPackageAsUser(BGMI_PACKAGE, USER_ID);
    }
}
