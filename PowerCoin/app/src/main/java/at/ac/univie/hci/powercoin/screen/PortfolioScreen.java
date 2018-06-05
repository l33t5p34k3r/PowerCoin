package at.ac.univie.hci.powercoin.screen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.ac.univie.hci.powercoin.R;

public class PortfolioScreen extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String HISTORY_DATE_MESSAGE = "historyFileDate";
    public static final String HISTORY_BTC_MESSAGE = "historyFileBTC";
    private static DecimalFormat dec = new DecimalFormat("#.##");
    double bitcoinAmount;
    /**
     * HAMBURGER-MENU RELATED
     * mDrawerLayout: Links to Layout for Hamburger Menu
     * mToggle: makes the Hamburger Button clickable
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    /**
     * CONVERSION RELATED
     */
    private TextView bitcoinView;
    private TextView fiatView;
    private TextView lastTimeView;
    /**
     * HISTORY RELATED
     */
    private TextInputLayout bitcoinWrapper;
    /**
     * API RELATED
     */

    //which value in fiat
    private double value;

    //when did the api update;
    private long lastTime;

    //which currency is being used
    private String currency;

    private String currSym;

    //requestqueue for API
    private RequestQueue requestQueue;




    //--------------
    //Main Functions
    //--------------

    /**
     * Rounding Function
     *
     * @param value  = number that should be rounded
     * @param places = amount of decimals to be rounded on
     * @return rounded number
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bigD = new BigDecimal(value);
        bigD = bigD.setScale(places, RoundingMode.HALF_UP);
        return bigD.doubleValue();
    }

    /**
     * Checks if a String is Double
     *
     * @param str
     * @return true if String is Double || false if String is not Double
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //----------
    //Conversion
    //----------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Portfolio");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_screen);
        bitcoinView = findViewById(R.id.valNum);
        fiatView = findViewById(R.id.currencyVal);
        lastTimeView = findViewById(R.id.last_update_portfolio);

        menuInitialization();

        Spinner currencySpinner = findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setOnItemSelectedListener(this);


        //Portfolio Functions
        File file = new File(PortfolioScreen.this.getFilesDir().getAbsolutePath(), "PortfolioHistoryBTC.txt");
        if (file.exists()) {
            portfolioStart();
        }
        String bitcoinText = "0";
        if (bitcoinAmount != 0) {
            bitcoinText = Double.toString(bitcoinAmount);
            bitcoinView.setText(bitcoinText + " BTC");
        } else
            bitcoinView.setText(bitcoinText + " BTC");

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);

        Button buttonRemove = findViewById(R.id.buttonRemove);
        buttonRemove.setOnClickListener(this);

        Button buttonHistory = findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(this);

        Button buttonDeleteH = findViewById(R.id.buttonDeleteHistory);
        buttonDeleteH.setOnClickListener(this);

        bitcoinWrapper = findViewById(R.id.textInputBTC);

        requestQueue = Volley.newRequestQueue(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                Log.i("ADD_BUTTON", "Button was clicked!");
                addClicked();
                break;
            case R.id.buttonRemove:
                Log.i("REMOVE_BUTTON", "Button was clicked!");
                removeClicked();
                break;
            case R.id.buttonHistory:
                Log.i("HISTORY_BUTTON", "Button was clicked!");
                historyClicked();
                break;
            case R.id.buttonDeleteHistory:
                Log.i("DELETE_BUTTON", "Button was clicked!");
                deleteHistory();
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
        //newConversion();
    }

    /**
     * Adds set amount of Bitcoin to wallet
     */
    public void addClicked() {
        String date;

        if (isDouble(bitcoinWrapper.getEditText().getText().toString())) {
            bitcoinAmount += Double.parseDouble(bitcoinWrapper.getEditText().getText().toString());
            if (bitcoinAmount < 0) {
                bitcoinAmount = 0.0;
                Toast.makeText(PortfolioScreen.this, "Too many BTC removed. Reverting to 0.", Toast.LENGTH_LONG).show();
            }
            date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        } else {
            Toast.makeText(PortfolioScreen.this,
                    "Please enter a number!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        writeToFileDate(date + "\n", this);
        writeToFileBTC(" (+" + bitcoinWrapper.getEditText().getText().toString() + ") BTC: " + bitcoinAmount + "\n", this);

        String bitcoinText = Double.toString(bitcoinAmount);

        apiFunction();

        bitcoinView.setText(bitcoinText + " BTC");
        fiatView.setText(dec.format(round(value, 2)) + currSym);
    }


    //-------
    //History
    //-------

    /**
     * Removes set amount of Bitcoin from wallet.
     */
    public void removeClicked() {
        String date;

        if (isDouble(bitcoinWrapper.getEditText().getText().toString())) {
            bitcoinAmount -= Double.parseDouble(bitcoinWrapper.getEditText().getText().toString());
            if (bitcoinAmount < 0) {
                bitcoinAmount = 0;
                Toast.makeText(PortfolioScreen.this, "Too much BTC removed. Reverting to 0.", Toast.LENGTH_LONG).show();
            }
            date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        } else {
            Toast.makeText(PortfolioScreen.this,
                    "Please enter a number!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        writeToFileDate(date + "\n", this);
        writeToFileBTC(" (-" + bitcoinWrapper.getEditText().getText().toString() + ") BTC: " + bitcoinAmount + "\n", this);
        Log.i("REMOVE_BUTTON", "Bitcoin in wallet: " + bitcoinAmount);
        String bitcoinText = Double.toString(bitcoinAmount);

        apiFunction();

        bitcoinView.setText(bitcoinText + " BTC");
        fiatView.setText(dec.format(round(value, 2)) + currSym);
    }

    /**
     * Clears History File
     */
    private void deleteHistory() {
        this.deleteFile("PortfolioHistoryDATE.txt");
        this.deleteFile("PortfolioHistoryBTC.txt");
        bitcoinAmount = 0;
        bitcoinView.setText("0 BTC");
        Log.i("DELETE_BUTTON", "History was deleted!");
    }

    //---------------
    //Other Functions
    //---------------

    /**
     * Goes to History Screen and shows former transactions of bitcoin
     */
    public void historyClicked() {
        Intent result = new Intent(this, HistoryScreen.class);
        result.putExtra(HISTORY_DATE_MESSAGE, readFromFileDate(this));
        result.putExtra(HISTORY_BTC_MESSAGE, readFromFileBTC(this));
        startActivity(result);
    }

    /**
     * Get the most recent amount of Bitcoin from wallet/transaction history
     */
    public void portfolioStart() {
        String str = readFromFileBTC(this);
        String[] parts = str.split("[\\)]");
        String part2 = parts[parts.length - 1];

        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
        Matcher m = p.matcher(part2);
        double d = 0;
        while (m.find()) {
            d = Double.parseDouble(m.group(1));
        }
        bitcoinAmount = d;
        Log.i("FILE", "CURRENT BITCOIN DEBUG: " + bitcoinAmount);
    }

    /**
     * Writes Bitcoin and other data (such as date) to a file (wallet)
     *
     * @param data
     * @param context
     */
    private void writeToFileDate(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("PortfolioHistoryDATE.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write fail: " + e.toString());
        }
    }

    private void writeToFileBTC(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("PortfolioHistoryBTC.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write fail: " + e.toString());
        }
    }

    /**
     * Reads the file (wallet)
     *
     * @param context
     * @return String with all information of the wallet
     */
    private String readFromFileDate(Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("PortfolioHistoryDATE.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private String readFromFileBTC(Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("PortfolioHistoryBTC.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    /**
     * This function calls the API and gets the newest bitcoin value in fiat
     */
    public void apiFunction() {
        JsonRequest request = new JsonObjectRequest(
                Request.Method.GET, "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=" + currency + "&e=Kraken", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("PORTFOLIO_API_RESPONSE", response.toString());
                        try {
                            value = response.getDouble(currency);
                            double total = value * bitcoinAmount;
                            lastTime = System.currentTimeMillis();


                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                            lastTimeView.setText("last update: " + sdf.format(new Date(lastTime)));

                            fiatView.setText(dec.format(round(total, 2)) + currSym);

                        } catch (JSONException e) {
                            Toast.makeText(PortfolioScreen.this,
                                    "Could not parse API response!",
                                    Toast.LENGTH_LONG).show();
                            Log.e("PARSER_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PortfolioScreen.this,
                                "Please try again!",
                                Toast.LENGTH_LONG).show();
                        if (error.getMessage() != null) Log.e("API_ERROR", error.getMessage());
                    }
                }
        );
        requestQueue.add(request);
    }

    /**
     * This function gets the API Response and gives back the newest BTC price
     *
     * @param apiResponse
     * @return upVal - the newest bitcoin value
     */
    private double getPrice(JSONObject apiResponse) {
        try {

            JSONObject data = apiResponse.getJSONObject("result");
            double upVal = data.getDouble("price");
            return upVal;

        } catch (JSONException e) {
            Toast.makeText(PortfolioScreen.this,
                    "Could not parse API response!",
                    Toast.LENGTH_LONG).show();
            Log.e("PARSER_ERROR", e.getMessage());
        }
        return 0;
    }

    /**
     * Initializes Menu, allowing the user to go to a different screen
     */
    private void menuInitialization() {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nv = findViewById(R.id.hamburger);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case (R.id.nav_ticker):
                        startTicker();
                        break;
                    case (R.id.nav_calc):
                        startCalculator();
                        break;
                    case (R.id.nav_portfolio):
                        startPortfolio();
                        break;
                    case (R.id.nav_notification):
                        startNotification();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Menu-Related Functions
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTicker() {
        Intent intent = new Intent(this, TickerScreen.class);
        startActivity(intent);
    }

    public void startCalculator() {
        Intent intent = new Intent(this, CalculatorScreen.class);
        startActivity(intent);
    }

    public void startPortfolio() {
        Intent intent = new Intent(this, PortfolioScreen.class);
        startActivity(intent);
    }

    public void startNotification() {
        Intent intent = new Intent(this, NotificationScreen.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();

        if (selected.equals("Dollar")) {
            currency = "USD";
            currSym = " $ ";
        }
        if (selected.equals("Euro")) {
            currency = "EUR";
            currSym = " € ";

        }
        if (selected.equals("Yen")) {
            currency = "JPY";
            currSym = " ¥ ";
        }
        if (selected.equals("Pound")) {
            currency = "GBP";
            currSym = " £ ";
        }

        apiFunction();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "HELLO DUDE", Toast.LENGTH_SHORT).show();
    }
}
