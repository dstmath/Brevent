package me.piebridge.brevent.ui;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import me.piebridge.brevent.BuildConfig;
import me.piebridge.brevent.R;
import me.piebridge.brevent.override.HideApiOverride;
import me.piebridge.brevent.override.HideApiOverrideN;
import me.piebridge.brevent.protocol.BreventStatus;

/**
 * Created by thom on 2017/2/7.
 */
public class BreventApplication extends Application {

    private UUID mToken;

    private boolean allowRoot;

    private boolean mSupportStopped = true;

    private boolean mSupportStandby = false;

    private boolean copied;

    public long mDaemonTime;

    public long mServerTime;

    public int mUid;

    public boolean started;

    public static boolean IS_OWNER = HideApiOverride.getUserId() == getOwner();

    @Override
    public void onCreate() {
        super.onCreate();
        mToken = UUID.randomUUID();
    }

    public UUID getToken() {
        return mToken;
    }

    public void setAllowRoot() {
        allowRoot = true;
    }

    public boolean allowRoot() {
        return allowRoot;
    }

    private void setSupportStopped(boolean supportStopped) {
        if (mSupportStopped != supportStopped) {
            mSupportStopped = supportStopped;
        }
    }

    public boolean supportStopped() {
        return mSupportStopped;
    }

    private File getBootstrapFile() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    BuildConfig.APPLICATION_ID, 0);
            File file = new File(applicationInfo.nativeLibraryDir, "libbrevent.so");
            if (file.exists()) {
                return file;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // ignore
        }
        return null;
    }

    public String copyBrevent() {
        File file = getApplicationContext().getExternalFilesDir(null);
        File brevent = getBootstrapFile();
        if (file == null || brevent == null) {
            return null;
        }
        String sdcard = "/" + "sdcard";
        String path = buildPath(new File(sdcard),
                "Android", "data", BuildConfig.APPLICATION_ID, "brevent.sh").getAbsolutePath();
        if (!copied) {
            try {
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                File output = buildPath(externalStorageDirectory,
                        "Android", "data", BuildConfig.APPLICATION_ID, "brevent.sh");
                try (
                        InputStream is = getResources().openRawResource(R.raw.brevent);
                        OutputStream os = new FileOutputStream(output);
                        PrintWriter pw = new PrintWriter(os)
                ) {
                    pw.println("#!/system/bin/sh");
                    pw.println();
                    pw.println("path=" + brevent);
                    pw.println("abi64=" + brevent.getPath().contains("64"));
                    pw.println();
                    pw.flush();
                    byte[] bytes = new byte[0x400];
                    int length;
                    while ((length = is.read(bytes)) != -1) {
                        os.write(bytes, 0, length);
                        if (length < 0x400) {
                            break;
                        }
                    }
                }
                copied = true;
            } catch (IOException e) {
                UILog.d("Can't copy brevent", e);
                return null;
            }
        }
        return path;
    }

    private File buildPath(File base, String... children) {
        File path = base;
        for (String child : children) {
            if (child != null) {
                if (path == null) {
                    path = new File(child);
                } else {
                    path = new File(path, child);
                }
            }
        }
        return path;
    }

    private void setSupportStandby(boolean supportStandby) {
        mSupportStandby = supportStandby;
    }

    public boolean supportStandby() {
        return mSupportStandby;
    }

    public void updateStatus(BreventStatus breventStatus) {
        mDaemonTime = breventStatus.mDaemonTime;
        mServerTime = breventStatus.mServerTime;
        mUid = breventStatus.mUid;
        setSupportStandby(breventStatus.mSupportStandby);
        setSupportStopped(breventStatus.mSupportStopped);
    }

    private static int getOwner() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? HideApiOverrideN.USER_SYSTEM
                : HideApiOverride.USER_OWNER;
    }

}
