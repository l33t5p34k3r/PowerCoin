package at.ac.univie.hci.powercoin.functionality;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import at.ac.univie.hci.powercoin.screen.TickerScreen;

public class Graph {


    private LineGraphSeries<DataPoint> mSeries;

    private double graphLastXValue;
    private double tmpTime;
    private double tmpVal;
    private long now = System.currentTimeMillis();
    private Context context;

    public Graph(Context context){
        this.context = context;
    }




    public LineGraphSeries<DataPoint> newGraph(double [] oldVal, long [] oldTime){
        Log.d("GRAPH", "New Graph being created");

        DataPoint[] newGraph = new DataPoint[oldVal.length];
        System.out.println("huihui: " + oldVal.length);

        for (int i = 0; i < newGraph.length; i++) {

            tmpTime  = oldTime[i] - now;
            tmpTime /= 100;



            newGraph[i] = new DataPoint(tmpTime, oldVal[i]);
            graphLastXValue = tmpTime;

        }

        //add stuff here for changing the looks of the lines etc.
        mSeries = new LineGraphSeries<>(newGraph);

        mSeries.setDrawDataPoints(true);
        mSeries.setDataPointsRadius(10);

        mSeries.setDrawBackground(true);

        mSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast toast;
                toast = Toast.makeText(context, "$ " + dataPoint.getY(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Log.d("GRAPH", "New Graph succesfully created");
        return mSeries;
    }

    public void updateGraph(double newVal, long newTime){
        //Log.d("GRAPH", "Updating graph...");

        //needs to be changed for some reason, keep it!
        tmpVal = newVal;
        tmpVal /= 1000;

        tmpTime = newTime - now;
        if (tmpTime / 1000 > graphLastXValue) {
            graphLastXValue = tmpTime / 1000;
            mSeries.appendData(new DataPoint(graphLastXValue, newVal), true, Integer.MAX_VALUE);
        }
    }



    //FOR TESTING PURPOSES

}
