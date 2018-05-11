package at.ac.univie.hci.powercoin.at.ac.univie.hci.powercoin.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;

import at.ac.univie.hci.powercoin.at.ac.univie.hci.powercoin.functionality.Graph;
import at.ac.univie.hci.powercoin.R;


public class TickerScreen extends AppCompatActivity implements View.OnClickListener {

    /**HAMBURGER-MENU RELATED
     *
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    /**GRAPH RELATED
     * mHandler: https://developer.android.com/reference/android/os/Handler
     * mTimer: https://developer.android.com/reference/java/lang/Runnable
     * Allows for threads to be created and update Graph in real time
     */
    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private Graph graph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //HAMBURGER-RELATED STUFF THERE
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

        //GRAPH-RELATED STUFF HERE (NO TOUCH)
        GraphView graphView = findViewById(R.id.graph);
        graph = new Graph();
        graphView.addSeries(graph.newGraph());
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(40);

        Button buttonUpdateGraph = findViewById(R.id.graphManualUpdate);
        buttonUpdateGraph.setOnClickListener(this);
        //END OF GRAPH-RELATED STUFF

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ticker_screen, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /* kept for future reference
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startPortfolio();
            //return true;
        }
        */

        //enables Hamburger-Menu to be opened by pressing the button
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //GRAPH RELATED
    @Override
    public void onResume() {
        Log.d("GRAPH", "Starting new thread to update graph");
        super.onResume();
        mTimer = new Runnable() {
            @Override
            public void run() {
                graph.updateGraph(); //COMMENT THIS TO STOP GRAPH UPDATE (FOR OPTIMIZATION PURPOSES DURING DEBUG)
                mHandler.postDelayed(this, 15000); //UPDATES EVERY 15 seconds at 15000 ms
            }
        };
        mHandler.postDelayed(mTimer, 15000);
        Log.d("GRAPH", "Successfully updated!");
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.graphManualUpdate:
                Log.d("GRAPH", "Update Button was clicked!");
                graph.updateGraph();
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
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

    public void startSettings() {
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }

    public void startPortfolio(){
        Intent intent = new Intent(this, PortfolioScreen.class);
        startActivity(intent);
    }



}
