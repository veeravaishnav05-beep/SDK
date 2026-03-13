package com.zenin;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.zenin.utils.FPrefs;

import java.io.File;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback;
import top.niunaijun.blackbox.app.configuration.ClientConfiguration;
import top.niunaijun.blackbox.core.system.api.WorkConfirmed;

public class BoxApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        FPrefs prefs = FPrefs.with(base);

        boolean ok = WorkConfirmed.activateSdk("BBOXSDKBYZENIN1MON");
        if (!ok) {
            throw new RuntimeException("SDK activation failed or expired");
        }

        try {
            BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {

                @Override
                public String getHostPackageName() {
                    return base.getPackageName();
                }

                @Override
                public boolean isHideRoot() {
                    return true;
                }

                @Override
                public boolean isHideXposed() {
                    return true;
                }

                @Override
                public boolean isEnableDaemonService() {
                    return false;
                }

                @Override
                public boolean requestInstallPackage(File file) {
                    PackageInfo packageInfo = base.getPackageManager()
                            .getPackageArchiveInfo(file.getAbsolutePath(), 0);
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BlackBox doAttachBaseContext failed", e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BlackBoxCore.get().doCreate();

        BlackBoxCore.get().addAppLifecycleCallback(new AppLifecycleCallback() {
            @Override
            public void beforeCreateApplication(String packageName, String processName,
                                                Context context, int userId) {
            }

            @Override
            public void beforeApplicationOnCreate(String packageName, String processName,
                                                  Application application, int userId) {
            }

            @Override
            public void afterApplicationOnCreate(String packageName, String processName,
                                                 Application application, int userId) {
            }
        });
    }
}