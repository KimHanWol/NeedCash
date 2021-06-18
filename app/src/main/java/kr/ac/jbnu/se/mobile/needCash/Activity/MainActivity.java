package kr.ac.jbnu.se.mobile.needCash.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.jbnu.se.mobile.needCash.Data.Account;
import kr.ac.jbnu.se.mobile.needCash.Data.CalculateCash;
import kr.ac.jbnu.se.mobile.needCash.R;
import kr.ac.jbnu.se.mobile.needCash.Manager.Setting;


public class MainActivity extends AppCompatActivity{

    private CalculateCash myCash;
    private TextView tv_total;

    private Setting setting;

    private TextView tv_bank;
    private TextView tv_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCash = new CalculateCash();
        setting = new Setting(getApplicationContext());

        uiSetting();
    }
    private void uiSetting(){
        tv_total = findViewById(R.id.tv_total);
        itemUISetting(findViewById(R.id.item_hundred), 100, CalculateCash.cashUnit.hundred);
        itemUISetting(findViewById(R.id.item_fiveHundred), 500, CalculateCash.cashUnit.fiveHundred);
        itemUISetting(findViewById(R.id.item_thousand), 1000, CalculateCash.cashUnit.thousand);
        itemUISetting(findViewById(R.id.item_tenThousand), 10000, CalculateCash.cashUnit.tenThousand);
        itemUISetting(findViewById(R.id.item_fiftyThousand), 50000, CalculateCash.cashUnit.fiftyThousand);

        tv_bank = findViewById(R.id.tv_bank);
        tv_account = findViewById(R.id.tv_account);
        if(setting.getBank() != Account.Bank.은행선택 && !setting.getAccount().equals("")){
            tv_bank.setText(setting.getBank().toString());
            tv_account.setText(setting.getAccount());
        }
        else{
            tv_bank.setText("계좌 정보 없음");
            tv_account.setText("계좌 정보를 등록해 주세요.");
        }
    }

    private void itemUISetting(View view, int intUnit, CalculateCash.cashUnit unit){
        TextView tv_unit = view.findViewById(R.id.tv_unit);
        tv_unit.setText(CalculateCash.intToStringCash(intUnit) + "원");

        if(intUnit >= 1000){
            ImageView iv_cash = view.findViewById(R.id.iv_cash);
            iv_cash.setImageResource(R.drawable.iconfinder_moneys);
            TextView tv_calUnit = view.findViewById(R.id.tv_calUnit);
            tv_calUnit.setText("장");
        }

        TextView tv_unitAmount = view.findViewById(R.id.tv_unitAmount);
        EditText editText = view.findViewById(R.id.et_amount);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int amount = 0;
                if(!s.toString().equals("")) amount = Integer.parseInt(s.toString());
                myCash.amountSetting(amount, unit);
                tv_unitAmount.setText("총 " + CalculateCash.intToStringCash(intUnit * amount) + "원");
                tv_total.setText(CalculateCash.intToStringCash(myCash.calculateSum()) + " 원");
            }
        });
    }

    public void onAccountButtonClick(View view){
        Intent intent = new Intent(this, PopUpAccountActivity.class);
        intent.putExtra("account", setting.getAccount());
        intent.putExtra("bank", setting.getBank().toString());
        startActivityForResult(intent ,1);
    }

    public void onSettingButtonClick(View view){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void onCashPleaseButtonClick(View view){
        if(myCash.calculateSum() == 0){
            Toast.makeText(this, "금액을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(setting.getAccount().equals("") || setting.getBank() == Account.Bank.은행선택){
            Toast.makeText(this, "계좌를 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, FindingActivity.class);
        intent.putExtra("100", myCash.get_hundred());
        intent.putExtra("500", myCash.get_fiveHundred());
        intent.putExtra("1000", myCash.get_thousand());
        intent.putExtra("10000", myCash.get_tenThousand());
        intent.putExtra("500000", myCash.get_fiftyThousand());
        intent.putExtra("setting", (Parcelable) setting);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1){
            setting.setValue("account", data.getStringExtra("account"));
            setting.setValue("bank", data.getStringExtra("bank"));

            if(setting.getBank() != Account.Bank.은행선택 && !setting.getAccount().equals("")){
                tv_bank.setText(setting.getBank().toString());
                tv_account.setText(setting.getAccount());
            }
            else{
                tv_bank.setText("계좌 정보 없음");
                tv_account.setText("계좌 정보를 등록해 주세요.");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}