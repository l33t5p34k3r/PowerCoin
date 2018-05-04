package at.ac.univie.hci.powercoin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PortfolioScreen extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_default_screen);

        writeToFile("Test Data 2", this);

        Log.d("FILE", readFromFile(this));
        Log.d("FILE", PortfolioScreen.this.getFilesDir().getAbsolutePath());

        this.deleteFile("PortfolioHistory.txt");


        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);

        Button buttonRemove = findViewById(R.id.buttonRemove);
        buttonRemove.setOnClickListener(this);

        Button buttonHistory = findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(this);
        

    }

    //History



    /*public void createFile(Context context){

        String filename = "PortfolioHistory.txt";
        String fileContents = "Test Data";
        FileOutputStream outputStream;

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("PortfolioHistory.txt",  Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("PortfolioHistory.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }



    //Transactions
    @Override
    public void onClick(View view) {
        Log.d("ADD_BUTTON", "Button was clicked!");
    }



}
