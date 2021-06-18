package kr.ac.jbnu.se.mobile.needCash.Manager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.jbnu.se.mobile.needCash.Data.UploadData;

public class DatabaseManager {

    private DatabaseReference databaseReference;
    private UploadData uploadData;

    public DatabaseManager(String id){
        uploadData = new UploadData();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(id);
    }

    public void addValueEventListener(ValueEventListener valueEventListener){
        FirebaseDatabase.getInstance().getReference().addValueEventListener(valueEventListener);
    }

    public void removeValueEventListener(ValueEventListener valueEventListener){
        FirebaseDatabase.getInstance().getReference().removeEventListener(valueEventListener);
    }

    public UploadData getLocationData(){
        return uploadData;
    }

    public void writeData(UploadData uploadData, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener){
        this.uploadData = uploadData;
        databaseReference.setValue(uploadData).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void writeData(UploadData uploadData){
        this.uploadData = uploadData;
        databaseReference.setValue(uploadData);
    }

    public void deleteData(){
        databaseReference.setValue(null);
    }
}
