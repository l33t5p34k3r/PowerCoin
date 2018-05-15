package at.ac.univie.hci.powercoin.screen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import at.ac.univie.hci.powercoin.R;

import static at.ac.univie.hci.powercoin.screen.PortfolioScreen.isDouble;

public class CalculatorScreen extends AppCompatActivity implements View.OnClickListener {

    /**HAMBURGER-MENU RELATED
     *
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;



    private String upUrlDollar;
    private String upUrlEuro;
    private double upValDollar;
    private double upValEuro;
    private RequestQueue requestQueueDollar;
    private RequestQueue requestQueueEuro;


    private TextInputLayout bitcoinWrapper;
    private TextInputLayout bitcoinWrapper2;
    double bitcoinAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_screen);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nv = findViewById(R.id.hamburger);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case(R.id.nav_ticker):
                        startTicker();
                        break;
                    case(R.id.nav_calc):
                        startCalculator();
                        break;
                    case(R.id.nav_portfolio):
                        startPortfolio();
                        break;
                    case(R.id.nav_notification):
                        startNotification();
                        break;
                    case(R.id.nav_settings):
                        startSettings();
                        break;
                }
                return false;
            }
        });

        apiFunction();





        Button buttonEuro = findViewById(R.id.buttonEuro);
        buttonEuro.setOnClickListener(this);

        Button buttonDollar = findViewById(R.id.buttonDollar);
        buttonDollar.setOnClickListener(this);


        bitcoinWrapper = (TextInputLayout) findViewById(R.id.textInputBTC);
        bitcoinWrapper2 = (TextInputLayout) findViewById(R.id.textInputEurDol);






    }

    private void apiFunction() {
        upUrlDollar = "https://api.cryptowat.ch/markets/kraken/btcusd/price";
        requestQueueDollar = Volley.newRequestQueue( this);

        JsonRequest dollarRequest = new JsonObjectRequest(
                Request.Method.GET, upUrlDollar, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API_RESPONSE", response.toString());
                        upValDollar = UpProcessResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CalculatorScreen.this,
                                "Please try again!",
                                Toast.LENGTH_LONG).show();
                        if(error.getMessage() != null) Log.e("API_ERROR", error.getMessage());
                    }
                });
        requestQueueDollar.add(dollarRequest);

        upUrlEuro = "https://api.cryptowat.ch/markets/kraken/btceur/price";
        requestQueueEuro = Volley.newRequestQueue( this);

        JsonRequest eurRequest = new JsonObjectRequest(
                Request.Method.GET, upUrlEuro, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("API_RESPONSE", response.toString());
                        upValEuro = UpProcessResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CalculatorScreen.this,
                                "Please try again!",
                                Toast.LENGTH_LONG).show();
                        if(error.getMessage() != null) Log.e("API_ERROR", error.getMessage());
                    }
                });
        requestQueueEuro.add(eurRequest);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //enables Hamburger-Menu to be opened by pressing the button
        if(mToggle.onOptionsItemSelected(item)) {
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
    public void startPortfolio(){
        Intent intent = new Intent(this, PortfolioScreen.class);
        startActivity(intent);
    }
    public void startNotification() {
        Intent intent = new Intent(this, NotificationScreen.class);
        startActivity(intent);
    }
    public void startSettings() {
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }



    //@Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.buttonEuro:
                Log.d("EURO_BUTTON", "Button was clicked!");
                euroClicked();
                break;
            case R.id.buttonDollar:
                Log.d("DOLLAR_BUTTON", "Button was clicked! :D");
                dollarClicked();
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

    private void euroClicked() {
        Intent intent = getIntent();
        double bitcoinVal = 0;
        if( isDouble( bitcoinWrapper.getEditText().getText().toString())){
            bitcoinVal = Double.parseDouble(bitcoinWrapper.getEditText().getText().toString());
            Log.d("VAL", Double.toString(upValEuro));
            bitcoinAmount = bitcoinVal * upValEuro;
            bitcoinWrapper2.getEditText().setText(Double.toString(round(bitcoinAmount, 2)));
            bitcoinWrapper.getEditText().setText("");

        }
        else if( isDouble( bitcoinWrapper2.getEditText().getText().toString())){
            bitcoinVal = Double.parseDouble(bitcoinWrapper2.getEditText().getText().toString());
            Log.d("VAL", Double.toString(upValEuro));
            bitcoinAmount = bitcoinVal / upValEuro;
            bitcoinWrapper.getEditText().setText(Double.toString(round(bitcoinAmount, 2)));
            bitcoinWrapper2.getEditText().setText("");
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bigD = new BigDecimal(value);
        bigD = bigD.setScale(places, RoundingMode.HALF_UP);
        return bigD.doubleValue();
    }

    private void dollarClicked() {
        Intent intent = getIntent();
        double bitcoinVal = 0;
        if( isDouble( bitcoinWrapper.getEditText().getText().toString())){
            bitcoinVal = Double.parseDouble(bitcoinWrapper.getEditText().getText().toString());
            Log.d("VAL", Double.toString(upValDollar));
            bitcoinAmount = bitcoinVal * upValDollar;
            bitcoinWrapper2.getEditText().setText(Double.toString(round(bitcoinAmount, 2)));
            bitcoinWrapper.getEditText().setText("");
        }
        else if( isDouble( bitcoinWrapper2.getEditText().getText().toString())){
            bitcoinVal = Double.parseDouble(bitcoinWrapper2.getEditText().getText().toString());
            Log.d("VAL", Double.toString(upValDollar));
            bitcoinAmount = bitcoinVal / upValDollar;
            bitcoinWrapper.getEditText().setText(Double.toString(round(bitcoinAmount, 2)));
            bitcoinWrapper2.getEditText().setText("");
        }


    }

    private double UpProcessResult (JSONObject apiResponse) {
        try {

            JSONObject data = apiResponse.getJSONObject("result");
            double upVal = data.getDouble("price");
            return upVal;

        } catch(JSONException e){
            Toast.makeText(CalculatorScreen.this,
                    "Could not parse API response!",
                    Toast.LENGTH_LONG).show();
            Log.e("PARSER_ERROR", e.getMessage());
        }
        return 0;
    }
    //Calculator functions




















}
