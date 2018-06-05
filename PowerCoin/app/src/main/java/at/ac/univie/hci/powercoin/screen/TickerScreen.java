package at.ac.univie.hci.powercoin.screen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import at.ac.univie.hci.powercoin.R;
import at.ac.univie.hci.powercoin.functionality.Graph;

public class TickerScreen extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    private static DecimalFormat dec = new DecimalFormat(".##");
    /**
     * GRAPH RELATED
     * mHandler: https://developer.android.com/reference/android/os/Handler
     * mTimer: https://developer.android.com/reference/java/lang/Runnable
     *
     * graph: creates Graph to fill
     * currText: saves the currency as text
     * currSymbol: saves the currency as symbol
     */
    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private Graph graph;
    private String currText;
    private String currSymbol;
    /**
     * HAMBURGER-MENU RELATED
     * mToggle: makes the Hamburger Button clickable
     */
    private ActionBarDrawerToggle mToggle;
    /**
     * API RELATED
     * upQueue: creates queue for API
     * upUrl: saves API-Url
     * upVal: saves latest price
     * upTime: saves latest time
     *
     * sinceQueue: creates queue for API
     * sinceUrl: saves API-Url
     * sinceVal: saves array of prices since now
     * sinceTime: saves array of times for the values
     *
     * firstVal: first entry of the interval
     *
     * currency: passes currency to API
     * scale: passes the scale to API
     */

    private RequestQueue upQueue;
    private String upUrl;
    private double upVal;
    private long upTime;
    private RequestQueue sinceQueue;
    private String sinceUrl;
    private double[] sinceVal;
    private long[] sinceTime;
    private double firstVal;
    private String currency;
    private String scale;
    private int time;
    /**
     * TEXTVIEW RELATED
     * valueView: displays the current value of Bitcoin
     * changeView: displays the difference in the last time period
     *
     * dec: creates format for values in changeView
     */

    private TextView valueView;
    private TextView currSymView;
    private TextView changeView;
    /**
     * SWIPE-TO-UPDATE RELATED
     * SwipeRefreshLayout: creates binds to SwipeRefreshLayout in activity_ticker_screen.xml
     */
    private SwipeRefreshLayout swipeUpdate;

    //--------------
    //Main Functions
    //--------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle("Ticker");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_screen);

        TextView currencyView = findViewById(R.id.currency);
        valueView = findViewById(R.id.value);
        currSymView = findViewById(R.id.currSym);
        TextView timeperiodView = findViewById(R.id.timeperiod);
        changeView = findViewById(R.id.change);

        menuInitialization();

        try {
            File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "currency.txt");

            if (file.exists()) {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                currency = br.readLine();

                fr.close();
                br.close();
            } else {

                FileWriter cw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(cw);

                currency = "USD";
                pw.print(currency);


                pw.close();
                cw.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (currency.equals("USD")) {
            currText = " Dollar ";
            currSymbol = " $ ";
        }

        if (currency.equals("EUR")) {
            currText = " Euro ";
            currSymbol = " € ";
        }

        if (currency.equals("JPY")) {
            currText = " Yen ";
            currSymbol = " ¥ ";
        }

        if (currency.equals("GBP")) {
            currText = " Pound ";
            currSymbol = " £ ";
        }

        currencyView.setText(currText);

        try {
            File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "timeperiod.txt");

            if (file.exists()) {
                FileReader fr = new FileReader(file);

                time = fr.read();

                fr.close();
            } else {

                FileWriter cw = new FileWriter(file);

                time = 24;
                cw.write(time);

                cw.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (time == 24) {
            timeperiodView.setText("last 24 hours");
            scale = "hour";
        }
        if (time == 168) {
            timeperiodView.setText("last 7 days");
            scale = "hour";
        }
        if (time == 31) {
            timeperiodView.setText("last month");
            scale = "day";
        }

        //API RELATED
        upQueue = Volley.newRequestQueue(this);
        upUrl = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=" + currency + "&e=Kraken";

        sinceQueue = Volley.newRequestQueue(this);
        sinceUrl = "https://min-api.cryptocompare.com/data/histo" + scale + "?fsym=BTC&tsym=" + currency + "&limit=" + time + "&e=Kraken";

        loadGraph();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.graphManualUpdate:

                Log.i("GRAPH", "Update Button was clicked!");

                updateGraph();

                Log.i("GRAPH", "Successfully updated!");
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

    //-----
    //Graph
    //-----

    /**
     * Initializes Graph
     */
    private void createGraph() {

        Log.i("GRAPH", "creating graph with time: " + Double.toString(time));

        GraphView graphView = findViewById(R.id.graph);

        final SimpleDateFormat day = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat week = new SimpleDateFormat("EEE");
        final SimpleDateFormat month = new SimpleDateFormat("dd.MM");

        day.setTimeZone(TimeZone.getDefault());
        week.setTimeZone(TimeZone.getDefault());
        month.setTimeZone(TimeZone.getDefault());

        graph = new Graph(this);

        LineGraphSeries<DataPoint> mSeries = graph.newDataPoints(sinceVal, sinceTime, currSymbol);
        graphView.addSeries(mSeries);


        //GRIDLABELRENDERER HERE
        GridLabelRenderer glr = graphView.getGridLabelRenderer();

        glr.setHumanRounding(true, true);

        final DecimalFormat df = new DecimalFormat("#");

        glr.setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (time == 24)
                        return day.format(value);
                    if (time == 168)
                        return week.format(value);
                    if (time == 31) {
                        return month.format(value);

                    } else
                        return "ERROR";
                } else
                    return df.format(value) + currSymbol;
            }
        });


        //VIEWPORT HERE

        Viewport gv = graphView.getViewport();

        gv.setMinX(graphView.getViewport().getMinX(true));
        gv.setXAxisBoundsManual(true);



        swipeUpdate = findViewById(R.id.swiperefresh);
        swipeUpdate.setOnRefreshListener(this);

        Button buttonUpdate = findViewById(R.id.graphManualUpdate);
        buttonUpdate.setOnClickListener(this);
    }

    /**
     * Fills Graph with past information taken from the API
     */
    private void loadGraph() {
        JsonRequest sinceReq = new JsonObjectRequest(

                Request.Method.GET, sinceUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("LOAD_API_RESPONSE", response.toString());

                        try {

                            JSONArray data = response.getJSONArray("Data");
                            sinceVal = new double[data.length()];
                            sinceTime = new long[data.length()];

                            for (int i = 0; i < sinceVal.length; i++) {
                                sinceVal[i] = data.getJSONObject(i).getDouble("close");
                                sinceTime[i] = data.getJSONObject(i).getLong("time") * 1000L;
                            }
                            valueView.setText(dec.format(sinceVal[sinceVal.length - 1]));
                            currSymView.setText(currSymbol);

                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                            TextView lastView = findViewById(R.id.last_update_ticker);
                            lastView.setText("last update: " + sdf.format(new Date(System.currentTimeMillis())));

                            firstVal = sinceVal[0];
                            double percent = sinceVal[sinceVal.length - 1] / firstVal;

                            //if course has fallen
                            if (percent < 1) {
                                percent = (1 - percent) * 100;
                                changeView.setText("-" + dec.format(percent) + "%");
                                changeView.setTextColor(Color.RED);
                            }
                            //if course has risen
                            else if (percent > 1) {
                                percent = (percent - 1) * 100;
                                changeView.setText("+" + dec.format(percent) + "%");
                                changeView.setTextColor(Color.rgb(0, 100, 0));
                            }
                            if (percent == 1) {
                                changeView.setText("No Change");
                                changeView.setTextColor(Color.BLACK);
                            }

                            createGraph();

                        } catch (JSONException e) {
                            Toast.makeText(TickerScreen.this,
                                    "Could not parse API response for Creation!",
                                    Toast.LENGTH_LONG).show();
                            Log.e("PARSER_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TickerScreen.this,
                                "API is not responding!",
                                Toast.LENGTH_LONG).show();
                        if (error.getMessage() != null) Log.e("API_ERROR", error.getMessage());
                    }
                });
        sinceQueue.add(sinceReq);

    }

    /**
     * Updates Graph with the latest value from the API
     */
    private void updateGraph() {
        JsonRequest upRequest = new JsonObjectRequest(
                Request.Method.GET, upUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("UPDATE_API_RESPONSE", response.toString());
                        try {
                            upVal = response.getDouble(currency);
                            upTime = System.currentTimeMillis();

                            valueView.setText(dec.format(upVal));
                            currSymView.setText(currSymbol);

                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                            TextView lastView = findViewById(R.id.last_update_ticker);
                            lastView.setText("last update: " + sdf.format(new Date(upTime)));

                            double percent = upVal / firstVal;

                            //if course has fallen
                            if (percent < 1) {
                                percent = (1 - percent) * 100;
                                changeView.setText("-" + dec.format(percent) + "%");
                                changeView.setTextColor(Color.RED);
                            }
                            //if course has risen
                            else if (percent > 1) {
                                percent = (percent - 1) * 100;
                                changeView.setText("+" + dec.format(percent) + "%");
                                changeView.setTextColor(Color.rgb(0, 100, 0));
                            } else if (percent == 1) {
                                changeView.setText("No Change");
                                changeView.setTextColor(Color.BLACK);
                            }

                            graph.update(upVal, upTime);

                        } catch (JSONException e) {
                            Toast.makeText(TickerScreen.this,
                                    "Could not parse API response!",
                                    Toast.LENGTH_LONG).show();
                            Log.e("PARSER_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TickerScreen.this,
                                "Please try again!",
                                Toast.LENGTH_LONG).show();
                        if (error.getMessage() != null) Log.e("API_ERROR", error.getMessage());
                    }
                }
        );
        upQueue.add(upRequest);
    }

    //-----------
    //Auto-Update
    //-----------

    /**
     * While the Activity is open on the Ticker Screen, updates happen in an interval
     */
    @Override
    public void onResume() {
        super.onResume();

        mTimer = new Runnable() {
            @Override
            public void run() {

                updateGraph();

                mHandler.postDelayed(this, 15000);

            }
        };
        mHandler.postDelayed(mTimer, 15000);
        Log.i("GRAPH", "Successfully updated automatically!");
    }

    /**
     * When Activity is paused, pause function
     */
    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    /**
     * Uses updateGraph to enable swipe-to-update
     */
    @Override
    public void onRefresh() {
        Log.i("GRAPH", "Swipe was used!");

        updateGraph();

        swipeUpdate.setRefreshing(false);
        Log.i("GRAPH", "Successfully updated with swipe!");
    }

    //---------------
    //Other Functions
    //---------------

    /**
     * Initializes Hamburger Menu, allowing the user to go to a different screen
     */
    private void menuInitialization() {
        DrawerLayout mDrawerLayout = findViewById(R.id.drawerLayout);
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
     * Menu-Related Function
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ticker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dollar:

                try {
                    File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "currency.txt");
                    FileWriter cw = new FileWriter(file);
                    PrintWriter pw = new PrintWriter(cw);

                    currency = "USD";
                    pw.print(currency);

                    pw.close();
                    cw.close();
                    finish();
                    startTicker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_euro:

                try {
                    File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "currency.txt");
                    FileWriter cw = new FileWriter(file);
                    PrintWriter pw = new PrintWriter(cw);

                    currency = "EUR";
                    pw.print(currency);

                    pw.close();
                    cw.close();
                    finish();
                    startTicker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_yen:

                try {
                    File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "currency.txt");
                    FileWriter cw = new FileWriter(file);
                    PrintWriter pw = new PrintWriter(cw);

                    currency = "JPY";
                    pw.print(currency);

                    pw.close();
                    cw.close();
                    finish();
                    startTicker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_pound:

                try {
                    File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "currency.txt");
                    FileWriter cw = new FileWriter(file);
                    PrintWriter pw = new PrintWriter(cw);

                    currency = "GBP";
                    pw.print(currency);

                    pw.close();
                    cw.close();
                    finish();
                    startTicker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_timeperiod_day:
                try {
                    File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "timeperiod.txt");
                    FileWriter cw = new FileWriter(file);

                    time = 24;
                    cw.write(24);
                    cw.close();
                    finish();
                    startTicker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_timeperiod_week:
                try {
                    File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "timeperiod.txt");
                    FileWriter cw = new FileWriter(file);

                    time = 168;
                    cw.write(168);
                    cw.close();
                    finish();
                    startTicker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_timeperiod_month:
                try {
                    File file = new File(TickerScreen.this.getFilesDir().getAbsolutePath(), "timeperiod.txt");
                    FileWriter cw = new FileWriter(file);

                    time = 31;
                    cw.write(31);
                    cw.close();
                    finish();
                    startTicker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    public void startTicker() {
        Intent intent = new Intent(this, TickerScreen.class);
        startActivity(intent);
    }

    public void startCalculator() {
        Intent intent = new Intent(this, CalculatorScreen.class);
        startActivity(intent);
    }

    public void startNotification() {
        Intent intent = new Intent(this, NotificationScreen.class);
        startActivity(intent);
    }

    public void startPortfolio() {
        Intent intent = new Intent(this, PortfolioScreen.class);
        startActivity(intent);
    }
}
