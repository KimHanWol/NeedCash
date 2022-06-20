package kr.ac.jbnu.se.mobile.needCash.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.jbnu.se.mobile.needCash.Activity.FindingActivity;
import kr.ac.jbnu.se.mobile.needCash.Data.PositionData;
import kr.ac.jbnu.se.mobile.needCash.Data.UploadData;
import kr.ac.jbnu.se.mobile.needCash.Manager.LocationCheck;
import kr.ac.jbnu.se.mobile.needCash.R;
import kr.ac.jbnu.se.mobile.needCash.Manager.Setting;

public class MatchingService extends Service{

    Setting setting;

    private DatabaseReference databaseReference;
    private NotificationManager notificationManager;

    private boolean isBatteryLow;

    private UploadData uploadData;

    private PositionData myPositionData;

    private ArrayList<String> idList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        foregroundActive(startId);

        myPositionData = new PositionData(0,0);
        LocationCheck locationCheck = new LocationCheck(getApplicationContext());
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                myPositionData.setLatitude(location.getLatitude());
                myPositionData.setLongitude(location.getLongitude());
                Log.e("MatchingService.locationCheck", "latitude :" + location.getLatitude() + "/ longitude : " + location.getLongitude());
            }
        };
        locationCheck.addLocationListener(locationListener);

        setting = new Setting(getApplicationContext());
        if(!setting.getNoti()) stopSelf();

        idList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        ValueEventListener dataChangeEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!setting.getNoti()){
                    return;
                }
                if(isBatteryLow) return;

                ArrayList<String> idTestList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    uploadData = dataSnapshot.getValue(UploadData.class);
                    idTestList.add(dataSnapshot.getKey());

                    uploadData.setLatitude_cash(myPositionData.getLatitude());
                    uploadData.setLongitude_cash(myPositionData.getLongitude());

                    checkData(uploadData, dataSnapshot.getKey());
                }
                for(int i = 0;i<idList.size();i++){
                    if(!idTestList.contains(idList.get(i))){
                        notificationManager.cancel(Integer.parseInt(idList.get(i)));
                        idList.remove(idList.get(i));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(dataChangeEvent);

/*        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isBatteryLow = intent.getAction().equals("android.intent.action.BATTERY_LOW");

            }
        };

        if(isBatteryLow){
            databaseReference.removeEventListener(dataChangeEvent);
            Toast.makeText(getApplicationContext(), "battery is low, stop notification!", Toast.LENGTH_SHORT).show();
        }
        else {
            databaseReference.addValueEventListener(dataChangeEvent);
            Toast.makeText(getApplicationContext(), "battery is okay, start notification!", Toast.LENGTH_SHORT).show();
        }*/

        if(setting.getNoti()) return START_STICKY;
        else return START_NOT_STICKY;
    }

    private void checkData(UploadData uploadData, String id){
        if(uploadData.isMatch()) {
            idList.remove(id);
            return;
        }

        if(uploadData.getDistance() < 2) idList.add(id);

        if(uploadData.getDistance() <= uploadData.getDistanceLimit() && !idList.contains(id)) {
            idList.add(id);
            //notification
            Intent intent = new Intent(getApplicationContext(), FindingActivity.class);
            //check plz
            /*                    intent.putExtra("id", snapshot.getKey().toString());*/
            intent.putExtra("id", id);
            intent.putExtra("uploadData", (Parcelable) uploadData);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationChannel channel = new NotificationChannel("cashRequestChannel", "channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "cashRequestChannel")
                    .setContentTitle("사례금 : " + uploadData.getGratuity() * uploadData.calculateSum() / 100 + "원 (" + String.format("%.2f", uploadData.getDistance()) + "m)")
                    .setContentText("두 손으로 내려 자세히 보기")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(
                            "100원 : " + uploadData.get_hundred() + "개 \n" +
                                    "500원 : " + uploadData.get_fiveHundred() + "개 \n" +
                                    "1000원 : " + uploadData.get_thousand() + "장 \n" +
                                    "10000원 : " + uploadData.get_tenThousand() + "장 \n" +
                                    "50000원 : " + uploadData.get_fiftyThousand() + "장"))
                    .addAction(0, "수락", pendingIntent)
                    .setSmallIcon(R.drawable.app_icon);

            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(Integer.parseInt(id), notificationBuilder.build());

            if (uploadData.getDistanceSearching() == uploadData.getDistanceLimit()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notificationManager.cancel(Integer.parseInt(id));
                        idList.remove(id);
                    }
                }, 4500);
            }
        }
    }

/*    private void removeForeground(){
        stopForeground(foregroundId, notificationBuilder.build());
    }*/

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "foregroundChannel");
    int foregroundId;

    private void foregroundActive(int startId){

        NotificationChannel serviceChannel = new NotificationChannel(
                "foregroundChannel",
                "matching service channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationManager.createNotificationChannel(serviceChannel);

        Intent notiBroadcastIntent = new Intent(this, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                notiBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new Notification.Builder(this, "foregroundChannel")
                .setContentTitle("매칭 서비스 활성화 됨")
                .setContentText("터치 후 \"알림 표시\" 체크 해제로 알림 제거")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_icon)
                .build();

/*        notificationManager.notify(1, notificationBuilder.build());*/

        foregroundId = startId;
        startForeground(startId, notification);
    }
}
