package kr.ac.jbnu.se.mobile.needCash.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import kr.ac.jbnu.se.mobile.needCash.Service.MatchingService;
import kr.ac.jbnu.se.mobile.needCash.R;
import kr.ac.jbnu.se.mobile.needCash.Manager.Setting;

public class SettingActivity extends AppCompatActivity {

    Setting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting = new Setting(getApplicationContext());
        uiSetting();
    }

    private void uiSetting(){
        Button btn_gratuity = findViewById(R.id.btn_gratuity);
        btn_gratuity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopUpSettingActivity.class);
                intent.putExtra("data", "gratuity");
                intent.putExtra("progress", setting.getGratuityRatio());
                startActivityForResult(intent, 1);
            }
        });

        Button btn_distance = findViewById(R.id.btn_distance);
        btn_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopUpSettingActivity.class);
                intent.putExtra("data", "distance");
                intent.putExtra("progress", setting.getDistanceLimit()/10);
                startActivityForResult(intent, 2);
            }
        });
/*        EditText et_account = findViewById(R.id.et);
        et_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setting.setValue("account", s.toString());
            }
        });

        Spinner sp_bank = findViewById(R.id.sp_bank);
        ArrayAdapter bankNameAdapter = ArrayAdapter.createFromResource(this, R.array.bankName, android.R.layout.simple_spinner_dropdown_item);
        bankNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_bank.setAdapter(bankNameAdapter);
        sp_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setting.setValue("bank", Account.Bank.values()[position].toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        SwitchCompat sw_noti = findViewById(R.id.sw_noti);
        sw_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setting.setValue("noti", isChecked);

                if(isChecked){
                    ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
                    for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
                        if(MatchingService.class.getName().equals(service.service.getClassName())) return;
                    }
                    Intent matchingServiceIntent = new Intent(getApplicationContext(), MatchingService.class);
                    startForegroundService(matchingServiceIntent);
                }
                else{
                    getApplicationContext().stopService(new Intent(getApplicationContext(), MatchingService.class));
                }
            }
        });
/*        et_account.setText(setting.getAccount());
        sp_bank.setSelection(setting.getBankNum());*/
        sw_noti.setChecked(setting.getNoti());
    }

    public void onBackButtonClick(View view){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                setting.setValue("gratuity", data.getIntExtra("progress", 10));
                break;
            case 2:
                setting.setValue("distance", data.getIntExtra("progress", 30) * 10);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}