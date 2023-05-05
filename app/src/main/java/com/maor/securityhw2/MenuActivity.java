package com.maor.securityhw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MenuActivity extends AppCompatActivity {
    private MaterialButton menu_BTN_start;
    /* access modifiers changed from: private */
    public TextInputEditText menu_EDT_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViews();
        initViews();
    }
    // down = 3, right = 1, left = 0, up = 2
    // My id steps -> down, right, left, left, up, left, left, right, left
    private void initViews() {
        this.menu_BTN_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isOnline()){
                    Toast.makeText(MenuActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
                }
                else if (menu_EDT_id.getText().toString().length()!=9){
                    Toast.makeText(MenuActivity.this, "ID should be 9 numbers", Toast.LENGTH_LONG).show();
                }
                else{
                    MenuActivity.this.makeServerCall();
                }
            }
        });
    }

    private void findViews() {
        this.menu_BTN_start = (MaterialButton) findViewById(R.id.menu_BTN_start);
        this.menu_EDT_id = (TextInputEditText) findViewById(R.id.menu_EDT_id);
    }
    /* access modifiers changed from: private */
    public void makeServerCall() {
        new Thread() {
            public void run() {
                String data = MenuActivity.getJSON(MenuActivity.this.getString(R.string.url));
                Log.d("pttt", data);
                if (data != null) {
                    MenuActivity activity_Menu = MenuActivity.this;
                    activity_Menu.startGame(activity_Menu.menu_EDT_id.getText().toString(), data);
                }
            }
        }.start();
    }

    /* access modifiers changed from: private */
    public void startGame(String id, String data) {
        String state = data.split(",")[Integer.valueOf(String.valueOf(id.charAt(7))).intValue()];
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_ID, id);
        intent.putExtra(GameActivity.EXTRA_STATE, state);
        startActivity(intent);
    }

    public static String getJSON(String url) {
        String data = "";
        HttpsURLConnection con = null;
        try {
            HttpsURLConnection con2 = (HttpsURLConnection) new URL(url).openConnection();
            con2.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con2.getInputStream()));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = br.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                sb.append(line + "\n");
            }
            br.close();
            data = sb.toString();
            if (con2 != null) {
                try {
                    con2.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (MalformedURLException ex2) {
            ex2.printStackTrace();
            if (con != null) {
                con.disconnect();
            }
        } catch (IOException ex3) {
            ex3.printStackTrace();
            if (con != null) {
                con.disconnect();
            }
        } catch (Throwable th) {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex4) {
                    ex4.printStackTrace();
                }
            }
            throw th;
        }
        return data;
    }
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            // doesn't connent
            return false;
        }
        return true;
    }
}