package kr.ac.jbnu.se.mobile.needCash.Service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import kr.ac.jbnu.se.mobile.needCash.Data.Account;
import kr.ac.jbnu.se.mobile.needCash.Data.CalculateCash;

public class TossThread extends Thread{

    Account cashAccount;
    CalculateCash calculateCash;
    Context context;
    Handler handler;
    int gratuityRatio;

    private static String link;

    public TossThread(Context context, Handler handler, Account cashAccount, CalculateCash calculateCash, int gratuityRatio){
        this.cashAccount = cashAccount;
        this.calculateCash = calculateCash;
        this.context = context;
        this.handler = handler;
        this.gratuityRatio = gratuityRatio;
    }

    private void setting() {
        if(cashAccount == null || calculateCash == null){
            Toast.makeText(context, "상대방의 계좌 정보가 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        //toss
        Client client = ClientBuilder.newClient();
        Entity payload = Entity.json("{  " +
                "\"apiKey\": \"fb98b8f8fb274039bed853258870ee1b\", " +
                " \"bankName\": \"" + cashAccount.getBank() + "\", " +
                " \"bankAccountNo\": \"" + cashAccount.getAccountNum()+"\", " +
                " \"amount\": \"" + calculateCash.calculateSum() * (gratuityRatio + 100) / 100 + "\", " +
                " \"message\": \"cashPlease\"}");
        Invocation.Builder test = client.target("https://toss.im/transfer-web/linkgen-api/link")
                .request(MediaType.APPLICATION_JSON_TYPE);
        Response response = test.post(payload);

        if(response.getStatus() == 200){

            String entityString = response.readEntity(String.class);
            entityString = entityString.split("link\":\"")[1];
            entityString = entityString.split("\"")[0];

            link = entityString;
            Log.e("TossThread", "link: " + link);
        }
        else{
            Toast.makeText(context, "전송에 실패했습니다. 다른 방법을 통해 송금하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void transmit(){
        if(link == null) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Message message = Message.obtain();
        message.what = 3;
        handler.sendMessage(message);
    }

    @Override
    public void run() {
        setting();
        transmit();
    }
}
