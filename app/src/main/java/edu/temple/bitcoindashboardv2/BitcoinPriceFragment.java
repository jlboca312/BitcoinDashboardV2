package edu.temple.bitcoindashboardv2;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class BitcoinPriceFragment extends Fragment {

    /* GLOBAL VARIABLES*/
    //get a HTTP connection object
    public HttpURLConnection connection = null;
    BufferedReader reader = null;
    TextView tv;

    public BitcoinPriceFragment(){
        //required empty public constructor
    }

    @Override public void onAttach(Activity c){
        super.onAttach(c);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.bc_price_frag, container, false);

        //connect textview widget to variable
        tv = (TextView) v.findViewById(R.id.jsonText);

        //perform http get request and fetch json string
        new JSONTask().execute("http://api.coindesk.com/v1/bpi/currentprice.json");

        return v;
    }

    //deals with making connection and putting into json string
    public class JSONTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls){

            /* MAKE THE CONNECTION*/
            try {
                //get url
                URL url = new URL(urls[0]);

                //connect to url
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                //stores JSON into a string
                String finalJsonString =  buffer.toString();

                //fetch the rate of the bitcoin from JSON
                JSONObject parentObject = new JSONObject(finalJsonString);
                JSONObject bpiObject = parentObject.getJSONObject("bpi");
                JSONObject usdObject = bpiObject.getJSONObject("USD");
                double bitcoinValue = Math.round((Double.valueOf(usdObject.getString("rate"))) * 100.0) / 100.0;
                String bitcoinValuestr = String.valueOf(bitcoinValue);

                //return the US price per bitcoin
                return "$US Price per Bitcoin:\n $" + bitcoinValuestr;



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                //check if connection is not null
                if(connection != null) {
                    //close the connection
                    connection.disconnect();
                }
                try {
                    //check if reader is not null
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //if we weren't able to get the data return null
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //variable results contains the json code
            tv.setText(result);
        }
    }
}



