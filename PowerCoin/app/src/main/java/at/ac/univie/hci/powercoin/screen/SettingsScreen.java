package at.ac.univie.hci.powercoin.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import at.ac.univie.hci.powercoin.R;

public class SettingsScreen extends AppCompatActivity {

    /**
     * HAMBURGER-MENU RELATED
     * mDrawerLayout: Links to Layout for Hamburger Menu
     * mToggle: makes the Hamburger Button clickable
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    //--------------
    //Main Functions
    //--------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        menuInitialization();
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
                    case (R.id.nav_settings):
                        startSettings();
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

    public void startSettings() {
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }
}
