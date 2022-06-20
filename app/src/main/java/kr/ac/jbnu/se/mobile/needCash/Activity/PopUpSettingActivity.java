package kr.ac.jbnu.se.mobile.needCash.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import kr.ac.jbnu.se.mobile.needCash.R;

public class PopUpSettingActivity extends AppCompatActivity {

    private int myProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pop_up_setting);

        TextView tv_title = findViewById(R.id.tv_title);
        String data = getIntent().getStringExtra("data");
        if(data == null) finish();

        SeekBar sb = findViewById(R.id.sb);
        sb.setMin(10);

        TextView tv = findViewById(R.id.tv);

        if(data.equals("gratuity")){
            tv_title.setText("사례금");
            sb.setProgress(getIntent().getIntExtra("progress", 10));
            tv.setText(getIntent().getIntExtra("progress", 10) + "%");
        }
        else if(data.equals("distance")){
            tv_title.setText("거리 한도");
            sb.setProgress(getIntent().getIntExtra("progress", 30));
            tv.setText(getIntent().getIntExtra("progress", 10) * 10 + "m");
        }

        myProgress = sb.getProgress();
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myProgress = (progress / 10) * 10;
                if(data.equals("gratuity")){
                    tv.setText(myProgress + "%");
                }
                else if(data.equals("distance")) {
                    tv.setText(myProgress * 10 + "m");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onCloseButtonClick(View view){
        Intent intent = new Intent();
        intent.putExtra("progress", myProgress);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}