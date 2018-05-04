package at.ac.univie.hci.powercoin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class DefaultScreen extends AppCompatActivity {



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
        setContentView(R.layout.activity_default_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //GRAPH-RELATED STUFF HERE (NO TOUCH)
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        graph = new Graph();
        graphView.addSeries(graph.newGraph());
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(40);
        //END OF GRAPH-RELATED STUFF

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startPortfolio();
            //return true;
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
                //graph.updateGraph();
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer, 1000);
        Log.d("GRAPH", "Successfully updated!");
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    //PORTFOLIO RELATED
    public void startPortfolio(){
        Intent portfolio = new Intent(this, PortfolioScreen.class);
        startActivity(portfolio);
    }



}
