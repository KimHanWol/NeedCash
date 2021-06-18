package kr.ac.jbnu.se.mobile.needCash.Service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import kr.ac.jbnu.se.mobile.needCash.Data.UploadData;
import kr.ac.jbnu.se.mobile.needCash.Manager.DatabaseManager;

public class FindingThread extends Thread{

    private int searchingDistance;

    private String id;

    private Handler mainHandler;

    private final DatabaseManager databaseManager;

    private boolean match;

    private int limitDistance;

    private UploadData uploadData;

    private boolean end;

    public FindingThread(Context context, Handler mainHandler, DatabaseManager databaseManager, String id, UploadData uploadData){
        this.databaseManager = databaseManager;
        searchingDistance = 100;
        match = false;
        this.mainHandler = mainHandler;
        limitDistance = uploadData.getDistanceLimit();
        this.uploadData = uploadData;
        uploadData.setDistanceSearching(searchingDistance);
        this.id = id;
    }

    ValueEventListener valueEventListener;

    public void run(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(match) return;

                for (DataSnapshot messageData : snapshot.getChildren()) {
                    if (messageData.getKey().equals(id)) {
                        uploadData = messageData.getValue(UploadData.class);
                        if (uploadData == null) return;
                        if (!uploadData.isMatch()) break;
                        if(match) return;
                        match = true;
                        Message message = Message.obtain();
                        message.what = 2;
                        message.arg1 = 1;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("location", (Parcelable) uploadData);
                        message.setData(bundle);
                        mainHandler.sendMessage(message);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseManager.addValueEventListener(valueEventListener);

        while(uploadData.getLatitude_user() == 0){
            try {
                FindingThread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(!Thread.currentThread().isInterrupted() && searchingDistance <= limitDistance && !match){

            uploadData.setDistanceSearching(searchingDistance);
            if(!end)databaseManager.writeData(uploadData, new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            //성공
                            if(match) return;
                            Message distanceMessage = Message.obtain();
                            distanceMessage.what = 1;
                            distanceMessage.arg1 = searchingDistance;
                            mainHandler.sendMessage(distanceMessage);
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            if(match) break;

            try {
                FindingThread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(searchingDistance >= limitDistance){
                Message matchMessage = Message.obtain();
                matchMessage.what = 2;
                if(match){
                    matchMessage.arg1 = 1;
                }
                else{
                    matchMessage.arg1 = 0;
                }
                mainHandler.sendMessage(matchMessage);
            }
            searchingDistance += 100;
        }

        if(!match) databaseManager.deleteData();
        databaseManager.removeValueEventListener(valueEventListener);
    }

    @Override
    public void interrupt() {
        end = true;
        databaseManager.removeValueEventListener(valueEventListener);
        databaseManager.deleteData();
        super.interrupt();
    }

}
