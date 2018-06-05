package at.ac.univie.hci.powercoin.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import at.ac.univie.hci.powercoin.R;

import static at.ac.univie.hci.powercoin.screen.PortfolioScreen.isDouble;

public class CalculatorScreen extends AppCompatActivity implements View.OnClickListener {

    double bitcoinAmount;
    /**
     * HAMBURGER-MENU RELATED
     * mDrawerLayout: Links to Layout for Hamburger Menu
     * mToggle: makes the Hamburger Button clickable
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    /**
     * API RELATED
     */
    private double exVal;
    private String currency;

    private RequestQueue requestQueue;
    /**
     * CALCULATOR RELATED
     */
    private TextView lastTimeView;
    private TextInputLayout bitcoinWrapper;
    private TextInputLayout fiatWrapper;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Calculator");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_screen);

        menuInitialization();

        Button buttonEuro = findViewById(R.id.buttonEuro);
        buttonEuro.setOnClickListener(this);

        Button buttonDollar = findViewById(R.id.buttonDollar);
        buttonDollar.setOnClickListener(this);

        Button buttonYen = findViewById(R.id.buttonYen);
        buttonYen.setOnClickListener(this);

        Button buttonPound = findViewById(R.id.buttonPound);
        buttonPound.setOnClickListener(this);

        bitcoinWrapper = findViewById(R.id.textInputBTC);
        fiatWrapper = findViewById(R.id.textInputFiat);
        lastTimeView = findViewById(R.id.last_update_calc);

        requestQueue = Volley.newRequestQueue(this);
    }

    //----------
    //Calculator
    //----------

    @Override
    public void onClick(View view) {

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);

        switch (view.getId()) {
            case R.id.buttonEuro:
                Log.i("EURO_BUTTON", "Button was clicked!");
                euroClicked();
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.buttonDollar:
                Log.i("DOLLAR_BUTTON", "Button was clicked! :D");
                dollarClicked();
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.buttonYen:
                Log.i("DOLLAR_BUTTON", "Button was clicked! :D");
                yenClicked();
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.buttonPound:
                Log.i("DOLLAR_BUTTON", "Button was clicked! :D");
                poundClicked();
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

    private void euroClicked() {
        currency = "EUR";
        apiFunction();
    }
    private void dollarClicked() {
        currency = "USD";
        apiFunction();
    }

    private void yenClicked() {
        currency = "JPY";
        apiFunction();
    }

    private void poundClicked() {
        currency = "GBP";
        apiFunction();
    }

    private void calculate() {
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        double bitcoinVal;
        if (isDouble(bitcoinWrapper.getEditText().getText().toString())) {
            bitcoinVal = Double.parseDouble(bitcoinWrapper.getEditText().getText().toString());
            bitcoinAmount = bitcoinVal * exVal;
            fiatWrapper.getEditText().setText(Double.toString(round(bitcoinAmount, 2)));
            bitcoinWrapper.getEditText().setText("");

        } else if (isDouble(fiatWrapper.getEditText().getText().toString())) {
            bitcoinVal = Double.parseDouble(fiatWrapper.getEditText().getText().toString());
            bitcoinAmount = bitcoinVal / exVal;
            System.out.println(bitcoinAmount);
            bitcoinWrapper.getEditText().setText(df.format((round(bitcoinAmount, 8))));
            fiatWrapper.getEditText().setText("");
        }
    }

    //---------------
    //Other Functions
    //---------------

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
     * This function calls the API and gets the newest bitcoin value in USD and EUR
     */


    public void apiFunction() {
        JsonRequest request = new JsonObjectRequest(
                Request.Method.GET, "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=" + currency + "&e=Kraken", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("CALC_API_RESPONSE", response.toString());
                        try {
                            exVal = response.getDouble(currency);
                            calculate();


                            long lastTime = System.currentTimeMillis();


                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                            lastTimeView.setText("last update: " + sdf.format(new Date(lastTime)));

                        } catch (JSONException e) {
                            Toast.makeText(CalculatorScreen.this,
                                    "Could not parse API response!",
                                    Toast.LENGTH_LONG).show();
                            Log.e("PARSER_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CalculatorScreen.this,
                                "Please try again!",
                                Toast.LENGTH_LONG).show();
                        if (error.getMessage() != null) Log.e("API_ERROR", error.getMessage());
                    }
                }
        );

        requestQueue.add(request);
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


}
