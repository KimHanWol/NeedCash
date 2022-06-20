package kr.ac.jbnu.se.mobile.needCash.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

public class ForcedTerminationService extends Service {

    Intent intent;

    String id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        if(!id.equals("")){
            FirebaseDatabase.getInstance().getReference().child(String.valueOf(id)).setValue(null);
        }
        stopSelf();

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;

        if(intent == null) return super.onStartCommand(intent, flags, startId);
        id = intent.getStringExtra("id");
        
        return super.onStartCommand(intent, flags, startId);
    }
}
