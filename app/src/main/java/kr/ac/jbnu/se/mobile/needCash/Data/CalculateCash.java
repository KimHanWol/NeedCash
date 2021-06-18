package kr.ac.jbnu.se.mobile.needCash.Data;

import java.text.DecimalFormat;

public class CalculateCash {

    private int _hundred;  //100
    private int _fiveHundred; //500
    private int _thousand; //1000
    private int _tenThousand; //10000
    private int _fiftyThousand; //50000

    public CalculateCash(){

    }

    public CalculateCash(int _hundred, int _fiveHundred, int _thousand, int _tenThousand, int _fiftyThousand) {
        this._hundred = _hundred;
        this._fiveHundred = _fiveHundred;
        this._thousand = _thousand;
        this._tenThousand = _tenThousand;
        this._fiftyThousand = _fiftyThousand;
    }

    public void amountSetting(CalculateCash calculateCash) {
        this._hundred = calculateCash.get_hundred();
        this._fiveHundred = calculateCash.get_fiveHundred();
        this._thousand = calculateCash.get_thousand();
        this._tenThousand = calculateCash.get_tenThousand();
        this._fiftyThousand = calculateCash.get_fiftyThousand();
    }

    public enum cashUnit{
        hundred,
        fiveHundred,
        thousand,
        tenThousand,
        fiftyThousand,
    }

    public int calculateSum(){
        return _hundred * 100 + _fiveHundred * 500 + _thousand * 1000 + _tenThousand * 10000 + _fiftyThousand * 50000;
    }

    public void amountSetting(int amount, cashUnit unit)
    {
        switch (unit){
            case hundred:
                set_hundred(amount);
                break;
            case fiveHundred:
                set_fiveHundred(amount);
                break;
            case thousand:
                set_thousand(amount);
                break;
            case tenThousand:
                set_tenThousand(amount);
                break;
            case fiftyThousand:
                set_fiftyThousand(amount);
                break;
        }
    }

    public int get_hundred() {
        return _hundred;
    }

    public int get_fiveHundred() {
        return _fiveHundred;
    }

    public int get_thousand() {
        return _thousand;
    }

    public int get_tenThousand() {
        return _tenThousand;
    }

    public int get_fiftyThousand() {
        return _fiftyThousand;
    }

    public void set_hundred(int _hundred) {
        this._hundred = _hundred;
    }

    public void set_fiveHundred(int _fiveHundred) {
        this._fiveHundred = _fiveHundred;
    }

    public void set_thousand(int _thousand) {
        this._thousand = _thousand;
    }

    public void set_tenThousand(int _tenThousand) {
        this._tenThousand = _tenThousand;
    }

    public void set_fiftyThousand(int _fiftyThousand) {
        this._fiftyThousand = _fiftyThousand;
    }

    public static String intToStringCash(int cash){
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        return decimalFormat.format(cash);
    }
}
