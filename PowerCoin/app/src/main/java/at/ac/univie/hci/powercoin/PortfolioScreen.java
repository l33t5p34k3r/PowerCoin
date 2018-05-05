package at.ac.univie.hci.powercoin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PortfolioScreen extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout usernameWrapper;
    double bitcoinAmmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_default_screen);

        writeToFile("Test Data 2", this);

        Log.d("FILE", readFromFile(this));
        System.out.println(readFromFile(this));
        Log.d("FILE", PortfolioScreen.this.getFilesDir().getAbsolutePath());

        this.deleteFile("PortfolioHistory.txt"); //THIS IS USED TO DELETE FILE FOR DEBUG PURPOSES


        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);

        Button buttonRemove = findViewById(R.id.buttonRemove);
        buttonRemove.setOnClickListener(this);

        Button buttonHistory = findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(this);

        usernameWrapper = (TextInputLayout) findViewById(R.id.textInputBTC);


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
            Log.e("Exception", "File write fail: " + e.toString());
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
        switch(view.getId())
        {
            case R.id.buttonAdd:
                Log.d("ADD_BUTTON", "Button was clicked!");
                addClicked();
                break;
            case R.id.buttonRemove:
                Log.d("REMOVE_BUTTON", "Button was clicked!");
                removeClicked();
                break;
            case R.id.buttonHistory:
                Log.d("HISTORY_BUTTON", "Button was clicked!");
                historyClicked();
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }


    }


    public void historyClicked(){

    }

    public void addClicked(){
        //Check file if bitcoin already exists -> if not, then create bitcoin 0
        //if(bitcoinAmmount == null) bitcoinAmmount = 0;
        //usernameWrapper.getEditText().getText().toString();

        Log.d("ADD_BUTTON", "If statement parse String to double STARTS!");
        if( isDouble( usernameWrapper.getEditText().getText().toString())){

            Log.d("ADD_BUTTON", "If statement PASSED!");
            bitcoinAmmount += Double.parseDouble(usernameWrapper.getEditText().getText().toString());
        }
        else{
            Toast.makeText(PortfolioScreen.this,
                    "Please enter a number!",
                    Toast.LENGTH_LONG).show();
        }

        writeToFile("BTC: " + bitcoinAmmount + "\n", this);
        Log.d("ADD_BUTTON", "Bitcoin in wallet: " + bitcoinAmmount);
    }

    public void removeClicked(){
        if( isDouble( usernameWrapper.getEditText().getText().toString())){
            Log.d("REMOVE_BUTTON", "If statement PASSED!");
            bitcoinAmmount -= Double.parseDouble(usernameWrapper.getEditText().getText().toString());
        }
        else{
            Toast.makeText(PortfolioScreen.this,
                    "Please enter a number!",
                    Toast.LENGTH_LONG).show();
        }
        writeToFile("BTC: " + bitcoinAmmount + "\n", this);
        Log.d("REMOVE_BUTTON", "Bitcoin in wallet: " + bitcoinAmmount);
    }

    public static boolean isDouble( String str ) {
        try {
            Double.parseDouble(str);
            return true;}
        catch(Exception e ){
            return false;
        }
        //return false;
    }

}
