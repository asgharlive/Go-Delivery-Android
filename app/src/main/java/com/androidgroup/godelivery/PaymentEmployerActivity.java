package com.androidgroup.godelivery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentEmployerActivity extends Activity {

    String jobID = null;
    String jobFileName = null;

    TextView paymentDescription;
    TextView paymentTitle;

    Button LogOutButton;
    Button RefreshButton;

    Button payButton;

    String[] jobDetails = new String[22];

    String amount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_job_employer);

        Typeface font = Typeface.createFromAsset(getAssets(), "FancyFont_1.ttf");

        paymentDescription = (TextView) findViewById(R.id.PaymentJobCreaterDescriptionID);

        paymentTitle = (TextView) findViewById(R.id.PaymentJobCreaterTitleID);

        payButton = (Button) findViewById(R.id.PaymentJobCreaterButtonID);

        LogOutButton = (Button) findViewById(R.id.LogoutButtonID);

        LogOutButton.setTypeface(font);
        LogOutButton.setTextColor(Color.WHITE);

        RefreshButton = (Button) findViewById(R.id.RefreshButtonID);

        RefreshButton.setTypeface(font);
        RefreshButton.setTextColor(Color.WHITE);



        paymentTitle.setTypeface(font);
        paymentTitle.setTextColor(Color.WHITE);

        paymentDescription.setTypeface(font);
        paymentDescription.setTextColor(Color.WHITE);

        payButton.setTypeface(font);
        payButton.setTextColor(Color.WHITE);


        Intent intent  = getIntent();

        jobID = intent.getStringExtra("AcceptedJobIDNumber");


        for (int i = 0; i < jobDetails.length; ++i) {
            jobDetails[i] = "";

        }


        jobFileName = (jobID + ".txt");



        new GetPaymentStatusFromServer().execute("http://192.168.0.185/AndroidApps/GoDelivery/PaymentsStatus/" + jobID + "-Status.txt");





    }




    private class FetchAcceptedJobDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return FetchJobDetails(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressBar.setVisibility(View.VISIBLE);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {






            if (result.equals("OK"))
            {


                amount = jobDetails[8];


                Double amountDoubleFormat = Double.valueOf(amount);


                double rounded = (double) Math.round(amountDoubleFormat * 100.0) / 100.0;

                String roundedAmount = String.valueOf(rounded);

                paymentDescription.setText("Payment Required!\n\nYou need to pay €" + roundedAmount + " to "+ jobDetails[3] + ".");


                payButton.setVisibility(View.VISIBLE);
            }









        }
    }

    private String FetchJobDetails(String myurl) throws IOException, UnsupportedEncodingException {
        InputStream is = null;

        // Only display the first 500 characters of the retrieved
        // web page content.


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);
            conn.addRequestProperty("Cache-Control", "no-cache");
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();



            is = conn.getInputStream();

            BufferedReader textReader = new BufferedReader(new InputStreamReader(is));


            String readlineTextListing;
            String complexJobListingString= null;




            while ((readlineTextListing = textReader.readLine()) != null) {

                complexJobListingString = readlineTextListing;


                if(complexJobListingString.length() > 25) {



                    int counter = 0;


                    for (int i = 0; i < complexJobListingString.length(); ++i) {

                        if(complexJobListingString.charAt(i) == ' ')
                        {
                            continue;
                        }

                        if (complexJobListingString.charAt(i) == '|') {
                            ++counter;
                            continue;
                        }


                        jobDetails[counter] = jobDetails[counter] + complexJobListingString.charAt(i);


                    }


                }


            }




            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                return "OK";
            }
            else
            {
                return "NetworkError";
            }



            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {


            if (is != null)
            {
                is.close();


            }

        }
    }




}



}
