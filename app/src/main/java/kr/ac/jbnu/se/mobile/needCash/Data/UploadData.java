package kr.ac.jbnu.se.mobile.needCash.Data;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class UploadData extends CalculateCash implements Parcelable{
    private double latitude_user;
    private double longitude_user;

    private double latitude_cash;
    private double longitude_cash;

    private int distanceLimit;
    private int distanceSearching;

    private boolean match;

    private int gratuity;

    private String accountNum;
    private String bank;

    protected UploadData(Parcel in) {
        latitude_user = in.readDouble();
        longitude_user = in.readDouble();
        latitude_cash = in.readDouble();
        longitude_cash = in.readDouble();
        distanceLimit = in.readInt();
        distanceSearching = in.readInt();
        match = in.readByte() != 0;
        gratuity = in.readInt();
        accountNum = in.readString();
        bank = in.readString();
    }

    public UploadData(){
        accountNum = "";
        bank = "";
    }


    public static final Creator<UploadData> CREATOR = new Creator<UploadData>() {
        @Override
        public UploadData createFromParcel(Parcel in) {
            return new UploadData(in);
        }

        @Override
        public UploadData[] newArray(int size) {
            return new UploadData[size];
        }
    };

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public Account.Bank getBankToBank() {
        if(bank.equals("")) return null;
        return Account.Bank.valueOf(bank);
    }

    public String getBank(){
        return bank;
    }


    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getGratuity() {
        return gratuity;
    }

    public void setGratuity(int gratuity) {
        this.gratuity = gratuity;
    }

    public void positionEdit(boolean isUser, PositionData positionData){
        if(isUser){
            latitude_user = positionData.getLatitude();
            longitude_user = positionData.getLongitude();
        }
        else{
            latitude_cash = positionData.getLatitude();
            longitude_cash = positionData.getLongitude();
        }
    }

    public double getLatitude_user() {
        return latitude_user;
    }

    public void setLatitude_user(double latitude_user) {
        this.latitude_user = latitude_user;
    }

    public double getLongitude_user() {
        return longitude_user;
    }

    public void setLongitude_user(double longitude_user) {
        this.longitude_user = longitude_user;
    }

    public double getLatitude_cash() {
        return latitude_cash;
    }

    public void setLatitude_cash(double latitude_cash) {
        this.latitude_cash = latitude_cash;
    }

    public double getLongitude_cash() {
        return longitude_cash;
    }

    public void setLongitude_cash(double longitude_cash) {
        this.longitude_cash = longitude_cash;
    }

    public int getDistanceLimit() {
        return distanceLimit;
    }

    public void setDistanceLimit(int distanceLimit) {
        this.distanceLimit = distanceLimit;
    }

    public int getDistanceSearching() {
        return distanceSearching;
    }

    public void setDistanceSearching(int distanceSearching) {
        this.distanceSearching = distanceSearching;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public double getDistance(){
        Location locationA = new Location("A");
        locationA.setLatitude(latitude_user);
        locationA.setLongitude(longitude_user);

        Location locationB = new Location("B");
        locationB.setLatitude(latitude_cash);
        locationB.setLongitude(longitude_cash);

        return locationA.distanceTo(locationB);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude_user);
        dest.writeDouble(longitude_user);
        dest.writeDouble(latitude_cash);
        dest.writeDouble(longitude_cash);
        dest.writeInt(distanceLimit);
        dest.writeInt(distanceSearching);
        dest.writeByte((byte) (match ? 1 : 0));
        dest.writeInt(gratuity);
        dest.writeString(accountNum);
        dest.writeString(bank);
    }
}
