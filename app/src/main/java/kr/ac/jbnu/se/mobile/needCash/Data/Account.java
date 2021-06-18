package kr.ac.jbnu.se.mobile.needCash.Data;

public class Account {
    private String accountNum;
    public static enum Bank{
        은행선택,
        농협,
        우리,
        신한,
        국민,
        하나,
        시티,
        기업,
        케이뱅크,
        카카오뱅크,
        새마을,
        부산,
        경남,
        광주,
        전북,
        신협,
        제일,
        대구,
        제주,
        우체국,
        수협,
        외환,
    }

    private Bank bank;

    public Account(String accountNum, Bank bank){
        this.accountNum = accountNum;
        this.bank = bank;
    }

    public String getAccountNum(){
        return accountNum;
    }

    public String getBank(){
        return bank.toString();
    }

}
