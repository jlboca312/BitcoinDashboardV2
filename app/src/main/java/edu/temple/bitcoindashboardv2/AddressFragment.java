package edu.temple.bitcoindashboardv2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddressFragment extends Fragment {

    /* GLOBAL VARIABLES */
    EditText addressInput;
    Button saveAddressButton;
    TextView balanceLabel;
    Button loadAddressButton;
    Spinner addressSpinner;
    int isTwoPanes;
    View v;
    String[] addressArray;
    String addressSelectedStr;

    //define file names
    String fileName = "test7";

    //http connection object
    public HttpURLConnection connection = null;
    BufferedReader reader = null;

    public AddressFragment() {
        //required public empty constructor
    }

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //determine if two panes, if returned 1 then PORTRAIT, if 2, then LANDSCAPE
        isTwoPanes = getResources().getConfiguration().orientation;

        //if portrait
        if (isTwoPanes == 1) {
            v = inflater.inflate(R.layout.address_frag, container, false);
        } else { //else landscape
            v = inflater.inflate(R.layout.address_frag_landscape, container, false);
        }

        //connect views to variables
        addressInput = (EditText) v.findViewById(R.id.addressInput);
        saveAddressButton = (Button) v.findViewById(R.id.saveAddressButton);
        balanceLabel = (TextView) v.findViewById(R.id.balanceTextLabel);
        loadAddressButton = (Button) v.findViewById(R.id.loadAddressButton);
        addressSpinner = (Spinner) v.findViewById(R.id.balanceSpinner);

        //temporarily set the balance label to nothing
        balanceLabel.setText("");

        //check if there is data in the file, if there is load the spinner
        if(checkFileForData()){
            //create an array adapter for the time periods array
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, addressArray);

            //set the adapter
            addressSpinner.setAdapter(adapter);
        }


        //deal with spinner functionality
        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //store what the spinner selected into a string so you can concatenate it to the url upon load button press
                addressSelectedStr = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //NOTHING?
            }
        });


        //deals with when the user clicks the save address button
        saveAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //store address to .txt file in internal storage
                writeMessage(v);

                //read from internal storage and set addressArray
                readMessage(v);

                //create an array adapter for the time periods array
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, addressArray);

                //set the adapter
                addressSpinner.setAdapter(adapter);

                //set url to perform http get request
                String addressURL = "http://btc.blockr.io/api/v1/address/info/";
                addressURL = addressURL.concat(addressInput.getText().toString());

                //perform http get request and fetch json string
                new JSONTask().execute(addressURL);

            }
        });

        //deal with when the user clicks the load address button
        loadAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //clear text box
                addressInput.setText("");

                //set url to perform http get request
                String addressURL = "http://btc.blockr.io/api/v1/address/info/";
                addressURL = addressURL.concat(addressSelectedStr);

                //perform http get request and fetch json string
                new JSONTask().execute(addressURL);

            }
        });

        return v;
    }




    //deals with writing to an internal file
    public void writeMessage(View view){
        //retrieve message from addressInput
        String address = addressInput.getText().toString();
        address = address.concat("\n");

        try {


            //create file output stream object
            FileOutputStream fos = getActivity().openFileOutput(fileName, Context.MODE_APPEND);

            //write data to internal storage
            fos.write(address.getBytes());

            //close the stream
            fos.close();

            //display a toast
            Toast.makeText(getActivity(), "Address Saved", Toast.LENGTH_LONG).show();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "File is Empty", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //deals with reading from an internal file
    public void readMessage(View view){
        //create file input stream object
        FileInputStream fis = null;
        try {
            String address;

            fis = getActivity().openFileInput(fileName);

            //create reader objects
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuffer stringBuffer = new StringBuffer();

            //fetch information using buffer object
            while((address=bufferedReader.readLine()) != null){

                stringBuffer.append(address +"\n");


            }

            String data = stringBuffer.toString();

            //store string buffer into array
            addressArray = data.split("\n");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Boolean checkFileForData(){

        //create file input stream object
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = null;
        try {
            String address;

            fis = getActivity().openFileInput(fileName);

            //create reader objects
            InputStreamReader isr = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(isr);
            stringBuffer = new StringBuffer();

            //fetch information using buffer object
            while((address=bufferedReader.readLine()) != null){
                stringBuffer.append(address +"\n");
            }

            String data = stringBuffer.toString();

            //store string buffer into array
            addressArray = data.split("\n");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return false if stringBuffer is empty
        if(stringBuffer == null){
            return false;
        }else
            return true;
    }


    //deals with making connection and putting into json string
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

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
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                //return JSON into a string
                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                //check if connection is not null
                if (connection != null) {
                    //close the connection
                    connection.disconnect();
                }
                try {
                    //check if reader is not null
                    if (reader != null) {
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

            //variables that hold data from json string
            String addressBalanceStr = "";

            //stores JSON into a string
            String finalJsonString = result;

            //fetch the rate of the bitcoin from JSON
            JSONObject parentObject = null;
            try {
                parentObject = new JSONObject(finalJsonString);
                JSONObject dataObject = parentObject.getJSONObject("data");

                //store the json results into the variable
                addressBalanceStr = dataObject.getString("balance");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //set the TextView label
            balanceLabel.setText("Bitcoin Address Balance: " + addressBalanceStr);

        }

    }
}

    /*//deals with writing to an internal file
    public void writeMessage(View view){
        //retrieve message from addressInput
        String address = addressInput.getText().toString();


        try {
            //create file output stream object
            FileOutputStream fos = getActivity().openFileOutput(fileName, Context.MODE_APPEND);

            //write data to internal storage
            fos.write(address.getBytes());

            //close the stream
            fos.close();

            //display a toast
            Toast.makeText(getActivity(), "Address Saved", Toast.LENGTH_LONG).show();

            //clear text box
            addressInput.setText("");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //deals with reading from an internal file
    public void readMessage(View view){
        //create file input stream object
        FileInputStream fis = null;
        try {
            String address;

            fis = getActivity().openFileInput(fileName);

            //create reader objects
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuffer stringBuffer = new StringBuffer();

            //fetch information using buffer object
            while((address=bufferedReader.readLine()) != null){

                stringBuffer.append(address +"\n");


            }

            //set test label
            testLabel.setText(stringBuffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    */

