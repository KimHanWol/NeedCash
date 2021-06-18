package kr.ac.jbnu.se.mobile.needCash.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import kr.ac.jbnu.se.mobile.needCash.Data.Account;
import kr.ac.jbnu.se.mobile.needCash.Data.CalculateCash;
import kr.ac.jbnu.se.mobile.needCash.Manager.DatabaseManager;
import kr.ac.jbnu.se.mobile.needCash.Service.FindingThread;
import kr.ac.jbnu.se.mobile.needCash.Service.ForcedTerminationService;
import kr.ac.jbnu.se.mobile.needCash.Manager.LocationCheck;
import kr.ac.jbnu.se.mobile.needCash.R;
import kr.ac.jbnu.se.mobile.needCash.Manager.Setting;
import kr.ac.jbnu.se.mobile.needCash.Service.TossThread;
import kr.ac.jbnu.se.mobile.needCash.Data.UploadData;


public class FindingActivity extends AppCompatActivity implements OnMapReadyCallback{

   CalculateCash cash;
   Setting setting;
   Account cashAccount;

   private String id;
   private boolean isUser;

   private boolean success;

   public FindingThread findingThread;

   private DatabaseManager databaseManager;
   private LocationCheck locationCheck;

   private UploadData uploadData;

   private GoogleMap googleMap;

   private Handler mainHandler;

   private Animation loadingAnimation;

   private Button btn_retry;
   private Button btn_cancel;
   private RelativeLayout rl_btn_toss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding);

        uploadData = new UploadData();

        btn_retry = findViewById(R.id.btn_retry);
        btn_cancel = findViewById(R.id.btn_cancel);
        rl_btn_toss = findViewById(R.id.rl_btn_toss);

        btn_retry.setVisibility(View.GONE);

        setting = new Setting(getApplicationContext());

        id = getIntent().getStringExtra("id");
        if(id != null){
            isUser = false;
            databaseManager = new DatabaseManager(id);
            UploadData uploadData = getIntent().getParcelableExtra("uploadData");
            uploadData.setAccountNum(setting.getAccount());
            uploadData.setBank(setting.getBank().toString());
            uploadData.setMatch(true);
            databaseManager.writeData(uploadData);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Integer.parseInt(id));
            matchingSuccess();
        }
        else{
            isUser = true;
            id = String.valueOf((int) (Math.random() * 100000000));
            cash = new CalculateCash(
                    getIntent().getIntExtra("100", 0),
                    getIntent().getIntExtra("500", 0),
                    getIntent().getIntExtra("1000", 0),
                    getIntent().getIntExtra("10000", 0),
                    getIntent().getIntExtra("50000", 0));
            uploadData.amountSetting(cash);
            uploadData.setGratuity(setting.getGratuityRatio());
            uploadData.setDistanceLimit(setting.getDistanceLimit());
            cashAccount = new Account(setting.getAccount(), setting.getBank());

            databaseManager = new DatabaseManager(id);

            rl_btn_toss.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.VISIBLE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fm_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.getKey().equals(id)){
                        uploadData = dataSnapshot.getValue(UploadData.class);
                    }
                }
                changeMyLocation(uploadData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseManager.addValueEventListener(valueEventListener);

        locationCheck = new LocationCheck(this);
        locationCheck.addLocationListener(locationListener);

        if(isUser) {
            messageReceive();
            loadingAnimation = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
            loadingAnimation.setDuration(1000);
            findViewById(R.id.iv_loading).startAnimation(loadingAnimation);

            startFindingThread();
            startForcedTerminationService();
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if(isUser){
                uploadData.setLatitude_user(location.getLatitude());
                uploadData.setLongitude_user(location.getLongitude());
            }
            else{
                uploadData.setLatitude_cash(location.getLatitude());
                uploadData.setLongitude_cash(location.getLongitude());
            }
            databaseManager.writeData(uploadData);
        }
    };

    private void messageReceive() {
        ProgressBar pb_finding = findViewById(R.id.pb_finding);
        TextView tv_finding = findViewById(R.id.tv_finding);
        mainHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        //거리 데이터 조정
                        int searchingDistance = msg.arg1;
                        int progress = searchingDistance * 100 / setting.getDistanceLimit();
                        pb_finding.setProgress(progress);
                        tv_finding.setText(searchingDistance + "m");

                        // if database is too slow, u have to get locationData to msg

                        uploadData.setDistanceSearching(searchingDistance);
                        changeMyLocation(uploadData);
                        break;
                    case 2:
                        //매칭 성공
                        if (msg.arg1 == 1) {
                            if(success) break;
                            success = true;
                            matchingSuccess();
                            changeMyLocation(uploadData);
                        }
                            //display distance
                        //매칭 실패
                        else {
                            locationCheck.deleteLocationListener();
                            matchingFailed(tv_finding);
                        }
                        break;
                    case 3:
                        //transmit complete
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void matchingSuccess() {

        ImageView iv_loading = findViewById(R.id.iv_loading);
        iv_loading.clearAnimation();
        iv_loading.setImageDrawable(getResources().getDrawable(R.drawable.iconfinder_mood_happy));

        if(isUser) {
            rl_btn_toss.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.GONE);
        }
        else{
            rl_btn_toss.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setText("매칭 종료하기");
        }

        TextView tv_finding = findViewById(R.id.tv_finding);
        tv_finding.setText("매칭 성공!");


        ProgressBar pb_finding = findViewById(R.id.pb_finding);
        pb_finding.setVisibility(View.INVISIBLE);
    }

    private void matchingFailed(TextView tv_finding){

        ImageView iv_loading = findViewById(R.id.iv_loading);
        iv_loading.clearAnimation();
        iv_loading.setImageDrawable(getResources().getDrawable(R.drawable.iconfinder_mood_sad));

        tv_finding.setText("매칭에 실패했습니다. 잠시 후 다시 시도해주세요.");

        btn_retry.setVisibility(View.VISIBLE);
    }


    private void startFindingThread(){
        findingThread = new FindingThread(this, mainHandler, databaseManager, id, uploadData);
        findingThread.start();
    }

    private void startForcedTerminationService(){
        Intent forcedTerminationIntent = new Intent(this, ForcedTerminationService.class);
        forcedTerminationIntent.putExtra("id", id);
        startService(forcedTerminationIntent);
    }

    @Override
    public void onBackPressed() {
        if(success) return;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if(findingThread != null) databaseManager.deleteData();
        super.onDestroy();
    }

    public void onCancelButtonClick(View view){
        locationCheck.deleteLocationListener();
        if(findingThread != null) findingThread.interrupt();
        finish();
    }

    public void onTossButtonClick(View view){
        //toss method
/*        cashAccount = new Account("94450200262883", Account.Bank.국민);*/
        cashAccount = new Account(uploadData.getAccountNum(), uploadData.getBankToBank());

        Thread tossThread = new TossThread(getApplicationContext(), mainHandler, cashAccount, cash, setting.getGratuityRatio());
        tossThread.start();
        onCancelButtonClick(view);
    }

    public void onRetryButtonClick(View view){
        btn_retry.setVisibility(View.GONE);

        ImageView iv_loading = findViewById(R.id.iv_loading);

        iv_loading.setImageDrawable(getResources().getDrawable(R.drawable.loading));
        iv_loading.startAnimation(loadingAnimation);

        TextView tv_finding = findViewById(R.id.tv_finding);

        tv_finding.setText("다시 시작하는 중입니다...");
        locationCheck.deleteLocationListener();
        locationCheck.addLocationListener(locationListener);
        startFindingThread();
        databaseManager.deleteData();
    }

    MarkerOptions userLocationOptions;
    CircleOptions circleOptions;
    MarkerOptions otherLocationOptions;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Toast.makeText(this, "구글 맵 준비됨", Toast.LENGTH_SHORT).show();

        otherLocationOptions = new MarkerOptions();
        otherLocationOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        userLocationOptions = new MarkerOptions();
        userLocationOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        changeMyLocation(uploadData);
    }

    private void changeMyLocation(UploadData uploadData){

        int zoom = 15;
        //zoom 계산법 찾아야함
        
        if(googleMap == null || uploadData == null) return;
        googleMap.clear();

        LatLng userPosition = new LatLng(uploadData.getLatitude_user(), uploadData.getLongitude_user());
        LatLng otherPosition = new LatLng(0, 0);
        //add cash marker
        if(uploadData.isMatch()){
            otherPosition = new LatLng(uploadData.getLatitude_cash(), uploadData.getLongitude_cash());
            otherLocationOptions.position(otherPosition).title("전달자").snippet("금방 갈게요!");
            googleMap.addMarker(otherLocationOptions);
        }
        else{
            //범위 원 추가
            circleOptions = new CircleOptions().center(userPosition)
                    .strokeWidth(0f)
                    .fillColor(Color.parseColor("#701EA316"))
                    .radius(uploadData.getDistanceSearching());
            googleMap.addCircle(circleOptions);
        }

        //add user marker
        userLocationOptions.position(userPosition).title("요청자").snippet("여기에요!");
        googleMap.addMarker(userLocationOptions);

        if(isUser) googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, zoom));
        else googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(otherPosition, zoom));
    }
}