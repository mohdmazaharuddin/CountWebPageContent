
/*

 * Purpose – To Display Count Occurrence in Specified Web Page.

 * @author

 * Created  on November 03, 2018

 * Modified on November 03, 2018

 */


package com.opengenus.countwebpagecontent;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class LandingScreenActivity extends AppCompatActivity {

    private EditText etURL, etUserInput;
    private Button btHit;
    private TextView tvOccurenceCount, tvError;
    private String strUserInput;
    private LinearLayout ll_Error;
    private ProgressDialog dialog;
    private String finalOutput="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        initializeControlls();

        btHit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String strURL = etURL.getText().toString();
                strUserInput = etUserInput.getText().toString();

                if (!TextUtils.isEmpty(strURL) && !TextUtils.isEmpty(strUserInput)) {
                    dialog = ProgressDialog.show(LandingScreenActivity.this, "",
                            "Loading. Please wait...", true);
                    loadOccurenceCount(strURL);
                } else if (TextUtils.isEmpty(strURL)) {

                    Toast.makeText(LandingScreenActivity.this, "Please Enter URL", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(strUserInput)) {

                    Toast.makeText(LandingScreenActivity.this, "Please Enter Search Input", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    // method for execute the async task
    public void loadOccurenceCount(String strUrl) {
        MyTask taskLoad = new MyTask();

        if (!isNetworkAvailable()) {
            dialog.cancel();
            tvOccurenceCount.setText("");
            ll_Error.setVisibility(View.VISIBLE);
            tvError.setText(R.string.no_internet);
        } else {
            // if user dont enter protocol adding programatically
            if (!(strUrl.contains("http://") || strUrl.contains("https://"))) {
                String appendHttp = "https://";
                strUrl = appendHttp + strUrl;
            }
            taskLoad.execute(strUrl);
        }

    }

    // initializeControlls Method for initialize components
    public void initializeControlls() {
        etURL =  findViewById(R.id.etURL);
        etUserInput =  findViewById(R.id.etUserInput);
        btHit =  findViewById(R.id.btHit);
        tvOccurenceCount =  findViewById(R.id.tvOccurenceCount);
        ll_Error =  findViewById(R.id.errorLayout);
        tvError =  findViewById(R.id.tvError);
    }

    // Check Internet Connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Async Task for run Background
    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
                URLConnection urlConnection = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = bufferedReader.readLine()) != null)
                    stringBuilder.append(inputLine);
                bufferedReader.close();
                String finalOutputContent = stringBuilder.toString().replaceAll("\\<.*?>","");
                return finalOutputContent;

            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            tvOccurenceCount.setText("");
            if (result.contains("Unable to resolve")) {
                dialog.cancel();
                ll_Error.setVisibility(View.VISIBLE);
                tvError.setText(R.string.wrong_url);
            } else {
                int lastIndex = 0;
                int count = 0;
                while (lastIndex != -1) {

                    lastIndex = result.indexOf(strUserInput, lastIndex);

                    if (lastIndex != -1) {
                        count++;
                        lastIndex += strUserInput.length();
                    }
                }
                dialog.cancel();
                ll_Error.setVisibility(View.GONE);
                tvOccurenceCount.setText(String.valueOf(count));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // for release memory
        Runtime.getRuntime().gc();
    }
}