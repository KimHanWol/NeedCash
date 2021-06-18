package kr.ac.jbnu.se.mobile.needCash.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import kr.ac.jbnu.se.mobile.needCash.Data.Account;

public class Setting implements Parcelable {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public Setting(Context context){
        sharedPreferences = context.getSharedPreferences("cash_please_setting", context.MODE_PRIVATE);
        assert sharedPreferences != null;
    }

    protected Setting(Parcel in) { }

    public static final Creator<Setting> CREATOR = new Creator<Setting>() {
        @Override
        public Setting createFromParcel(Parcel in) {
            return new Setting(in);
        }

        @Override
        public Setting[] newArray(int size) {
            return new Setting[size];
        }
    };

    public void setValue(String key, int value){
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setValue(String key, boolean value)
    {
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setValue(String key, String value){
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int getGratuityRatio() {
        return sharedPreferences.getInt("gratuity", 10);
    }

    public int getDistanceLimit() {
        return sharedPreferences.getInt("distance", 300);
    }

    public String getAccount() {
        return sharedPreferences.getString("account", "");
    }

    public Account.Bank getBank() {
        return Account.Bank.valueOf(sharedPreferences.getString("bank", "은행선택"));
    }

    public int getBankNum(){
        return sharedPreferences.getInt("bankNum", 0);
    }

    public boolean getNoti() {
        return sharedPreferences.getBoolean("noti", true);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
