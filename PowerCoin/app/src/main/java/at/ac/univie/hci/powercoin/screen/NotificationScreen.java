package at.ac.univie.hci.powercoin.screen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import at.ac.univie.hci.powercoin.R;
import at.ac.univie.hci.powercoin.functionality.NotificationDialog;

public class NotificationScreen extends AppCompatActivity implements NotificationDialog.NotificationDialogListener {

    /**
     * API RELATED
     */
    private final Handler mHandler = new Handler();
    /**
     * NOTIFICATION RELATED
     */
    NotificationCompat.Builder notification;
    boolean isbigger;
    /**
     * TEXT RELATED
     */
    TextView currPrice;
    TextView entry1;
    private ActionBarDrawerToggle mToggle;
    private Runnable mTimer;
    private String currency = "USD";
    private String currSymbol = " $ ";
    private String priceUrl;

    private double price;
    private double alert = 0;
    private RequestQueue priceQueue;
    /**
     * HAMBURGER-MENU RELATED
     * mDrawerLayout: Links to Layout for Hamburger Menu
     * mToggle: makes the Hamburger Button clickable
     */
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Notifications");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);
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
                    case (R.id.nav_notification):
                        startNotification();
                        break;
                    case (R.id.nav_portfolio):
                        startPortfolio();
                        break;
                }
                return false;
            }
        });

        //NOTIFICATION (AND API)STUFF HERE

        priceUrl = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=" + currency + "&e=Kraken";

        priceQueue = Volley.newRequestQueue(this);

        currPrice = findViewById(R.id.valueNotification);

        setPrice();
        currPrice.setText(Double.toString(price) + currSymbol);
        entry1 = findViewById(R.id.entry1);

        FloatingActionButton button = findViewById(R.id.addFloatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        notification = new NotificationCompat.Builder(this, null);
        notification.setAutoCancel(true);


    }

    public void openDialog() {
        NotificationDialog notificationDialog = new NotificationDialog();
        notificationDialog.show(getSupportFragmentManager(), "notificationDialog");
    }

    //has value from dialog and activates notification
    @Override
    public void applyText(String val) {
        double value = Double.parseDouble(val);
        if (value < price) {
            entry1.setText("1 BTC < " + val + currSymbol);
            alert = value;

            isbigger = false;
        }
        if (value > price) {
            entry1.setText("1 BTC > " + val + currSymbol);
            alert = value;
            isbigger = true;
        }
    }

    public void setPrice() {
        JsonRequest priceRequest = new JsonObjectRequest(
                Request.Method.GET, priceUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("NOTIFY_API_RESPONSE", response.toString());
                        try {

                            price = response.getDouble(currency);
                            currPrice.setText(Double.toString(price) + currSymbol);

                        } catch (JSONException e) {
                            Toast.makeText(NotificationScreen.this,
                                    "Could not parse API response!",
                                    Toast.LENGTH_LONG).show();
                            Log.e("PARSER_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NotificationScreen.this,
                                "Please try again!",
                                Toast.LENGTH_LONG).show();
                        if (error.getMessage() != null) Log.e("API_ERROR", error.getMessage());
                    }
                }
        );
        priceQueue.add(priceRequest);
    }

    //AutoUpdates Value
    @Override
    public void onResume() {
        super.onResume();


        mTimer = new Runnable() {
            @Override
            public void run() {

                setPrice();

                if (isbigger) {
                    if (price > alert) {
                        notification.setSmallIcon(R.mipmap.alarm);
                        notification.setContentTitle("PowerCoin");
                        notification.setTicker("");
                        notification.setWhen(System.currentTimeMillis());

                        Intent intent = new Intent(NotificationScreen.this, NotificationScreen.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationScreen.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pendingIntent);

                        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notification.setContentText("Bitcoin has reached value: " + alert + "!");
                        nm.notify(2323, notification.build());
                    }

                    //send Notification and check
                }
                if (!isbigger) {
                    if (price < alert) {
                        notification.setSmallIcon(R.mipmap.alarm);
                        notification.setContentTitle("PowerCoin");
                        notification.setTicker("");
                        notification.setWhen(System.currentTimeMillis());
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        notification.setSound(alarmSound);

                        Intent intent = new Intent(NotificationScreen.this, NotificationScreen.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationScreen.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pendingIntent);

                        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notification.setContentText("Bitcoin has reached value: " + alert + "!");
                        nm.notify(2323, notification.build());
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer, 1000);
        Log.d("NOTIFICATION", "Successfully updated automatically!");
    }

    public void sendNotification() {

    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_dollar:
                currency = "USD";
                currSymbol = " $ ";
                break;
            case R.id.action_euro:
                currency = "EUR";
                currSymbol = " € ";

                break;
            case R.id.action_yen:
                currency = "JPY";
                currSymbol = " ¥ ";
                break;
            case R.id.action_pound:
                currency = "GBP";
                currSymbol = " £ ";
                break;
        }
        priceUrl = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=" + currency + "&e=Kraken";
        setPrice();

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

    public void startPortfolio() {
        Intent intent = new Intent(this, PortfolioScreen.class);
        startActivity(intent);
    }

    public void startNotification() {
        Intent intent = new Intent(this, NotificationScreen.class);
        startActivity(intent);
    }
}
