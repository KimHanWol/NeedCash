package kr.ac.jbnu.se.mobile.needCash.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import kr.ac.jbnu.se.mobile.needCash.Service.MatchingService;
import kr.ac.jbnu.se.mobile.needCash.Manager.PermissionManager;
import kr.ac.jbnu.se.mobile.needCash.R;
import kr.ac.jbnu.se.mobile.needCash.Manager.Setting;

public class StartActivity extends AppCompatActivity {

    private Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        activity = this;

        loading();

        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PermissionManager permissionManager = new PermissionManager(getApplicationContext());
                if(permissionManager.permissionCheck(activity)){
                    nextActivity();
                }
            }
        }, 500);
    }

    public boolean isMatchingServiceWorking(){
        ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(MatchingService.class.getName().equals(service.service.getClassName())) return true;
        }
        return false;
    }

    private void loading(){
    }

    private void nextActivity(){
        if(new Setting(getApplicationContext()).getNoti() && !isMatchingServiceWorking()) {
            Intent matchingServiceIntent = new Intent(this, MatchingService.class);
            startForegroundService(matchingServiceIntent);
        }

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++){
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "권한 미 동의시 앱을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
            }
        }

        nextActivity();
    }
}