package edu.temple.bitcoindashboardv2;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class BlockInfoFragment extends Fragment {

    public static final int MAX_BLOCK = 442837;

    /* GLOBAL VARIABLES*/
    TextView blockNumber;
    TextView blockFee;
    TextView blockSize;
    TextView blockDifficulty;
    Button previousButton;
    Button nextButton;
    Button loadButton;
    EditText blockNumInput;
    int currentBlock;
    View v;
    int isTwoPanes;

    //http connection object
    public HttpURLConnection connection = null;
    BufferedReader reader = null;


    public BlockInfoFragment(){
        //required empty public constructor
    }

    @Override public void onAttach(Activity c){
        super.onAttach(c);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //determine if two panes, if returned 1 then PORTRAIT, if 2, then LANDSCAPE
        isTwoPanes = getResources().getConfiguration().orientation;

        //if portrait
        if(isTwoPanes == 1) {
            v = inflater.inflate(R.layout.block_info_frag, container, false);
        }else{ //else landscape
            v = inflater.inflate(R.layout.block_info_landscape, container, false);
        }

        //connect all views/widgets to variables
        blockNumber = (TextView) v.findViewById(R.id.blockNumber);
        blockFee = (TextView) v.findViewById(R.id.blockFee);
        blockSize = (TextView) v.findViewById(R.id.blockSize);
        blockDifficulty = (TextView) v.findViewById(R.id.blockDifficulty);
        previousButton = (Button) v.findViewById(R.id.previousBlockButton);
        nextButton = (Button) v.findViewById(R.id.nextBlockButton);
        loadButton = (Button) v.findViewById(R.id.loadBlockButton);
        blockNumInput = (EditText) v.findViewById(R.id.blockInput);



        //when user hits load button, update the text fields to the block information
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //set url to perform http get request
                String blockURL = "http://btc.blockr.io/api/v1/block/info/";
                blockURL = blockURL.concat(blockNumInput.getText().toString());
                int blockNumInt = Integer.parseInt(blockNumInput.getText().toString());
                currentBlock = blockNumInt;

                //check if block exists
                if(currentBlock < MAX_BLOCK) {
                    //perform http get request and fetch json string
                    new JSONTask().execute(blockURL);
                }else{ //else doesn't exist so print it
                    blockNumber.setText("Block entered does not exist.");
                    blockFee.setText("Please try another block number.");
                    blockSize.setText("");
                    blockDifficulty.setText("");
                }
            }
        });

        //deal with when user clicks next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set url to perform http get request
                String blockURL = "http://btc.blockr.io/api/v1/block/info/";
                String blockStr = String.valueOf(currentBlock++);
                blockURL = blockURL.concat(blockStr);

                //check if block exists
                if(currentBlock < MAX_BLOCK) {
                    //perform http get request and fetch json string
                    new JSONTask().execute(blockURL);
                }else{ //else doesn't exist so print it
                    blockNumber.setText("Block entered does not exist.");
                    blockFee.setText("Please try another block number.");
                    blockSize.setText("");
                    blockDifficulty.setText("");
                }
            }
        });

        //deal with when user clicks preivous button
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set url to perform http get request
                String blockURL = "http://btc.blockr.io/api/v1/block/info/";
                String blockStr = String.valueOf(currentBlock--);
                blockURL = blockURL.concat(blockStr);

                //check if block exists
                if(currentBlock < MAX_BLOCK) {
                    //perform http get request and fetch json string
                    new JSONTask().execute(blockURL);
                }else{ //else doesn't exist so print it
                    blockNumber.setText("Block entered does not exist.");
                    blockFee.setText("Please try another block number.");
                    blockSize.setText("");
                    blockDifficulty.setText("");
                }
            }
        });




        return v;
    }

    //deals with making connection and putting into json string
    public class JSONTask extends AsyncTask<String, String, String> {

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

                //return JSON into a string
                return buffer.toString();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }  finally {

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

            //variables that hold data from json string
            String blockNumStr="", blockFeeStr="", blockSizeStr="", blockDifficultyStr="";

            //stores JSON into a string
            String finalJsonString =  result;

            //fetch the rate of the bitcoin from JSON
            JSONObject parentObject = null;
            try {
                parentObject = new JSONObject(finalJsonString);
                JSONObject dataObject = parentObject.getJSONObject("data");

                //store the json results into the variables
                blockNumStr = dataObject.getString("nb");
                blockFeeStr = dataObject.getString("fee");
                blockSizeStr = dataObject.getString("size");
                blockDifficultyStr = dataObject.getString("difficulty");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //set the TextView labels
            blockNumber.setText("Block Number/Height: "+blockNumStr);
            blockFee.setText("Block Fee: "+blockFeeStr);
            blockSize.setText("Block Size: "+blockSizeStr);
            blockDifficulty.setText("Block Difficulty: "+blockDifficultyStr);



        }
    }
}
