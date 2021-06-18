package kr.ac.jbnu.se.mobile.needCash.Manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    public final static int LOCATION_PERMISSION = 1;

    private String[] permissions;
    private Context context;

    public PermissionManager(Context context){
        this.context = context;
        permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
        };
    }

    public boolean isAllCheck(){
        for(int i = 0;i<permissions.length;i++){
            if(ContextCompat.checkSelfPermission(context,permissions[i]) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public boolean permissionCheck(Activity activity){
        if(!isAllCheck()) ActivityCompat.requestPermissions(activity, permissions ,LOCATION_PERMISSION);
        return isAllCheck();
    }
}
