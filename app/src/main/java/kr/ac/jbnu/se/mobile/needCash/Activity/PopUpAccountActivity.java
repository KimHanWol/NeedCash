package kr.ac.jbnu.se.mobile.needCash.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import kr.ac.jbnu.se.mobile.needCash.Data.Account;
import kr.ac.jbnu.se.mobile.needCash.R;

public class PopUpAccountActivity extends AppCompatActivity {

    private String accountNum;
    private int bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_account);

        accountNum = getIntent().getStringExtra("account");
        String bankString = getIntent().getStringExtra("bank");

        Account.Bank[] banks = Account.Bank.values();
        for(int i = 0;i< banks.length;i++){
            if(banks[i].toString().equals(bankString)){
                bank = i;
                break;
            }
        }

        Spinner sp_bank = findViewById(R.id.sp_bank);
        sp_bank.setSelection(bank);

        ArrayAdapter bankNameAdapter = ArrayAdapter.createFromResource(this, R.array.bankName, android.R.layout.simple_spinner_dropdown_item);
        bankNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_bank.setAdapter(bankNameAdapter);
        sp_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bank = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_bank.setSelection(bank);

        EditText et_account = findViewById(R.id.et_account);
        et_account.setText(accountNum);
        et_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                accountNum = s.toString();
            }
        });
    }

    public void onCloseButtonClick(View view){
        Intent intent = new Intent();
        intent.putExtra("account", accountNum);
        intent.putExtra("bank", Account.Bank.values()[bank].toString());
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