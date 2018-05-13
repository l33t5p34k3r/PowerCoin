package at.ac.univie.hci.powercoin.functionality;

import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Graph {


    private LineGraphSeries<DataPoint> mSeries;

    private double graphLastXValue;
    private double tmpTime;
    private double tmpVal;
    private long now = System.currentTimeMillis();




    public LineGraphSeries<DataPoint> newGraph(double [] newVal, long [] newTime){
        Log.d("GRAPH", "New Graph being created");

        DataPoint[] newGraph = new DataPoint[newVal.length];

        for (int i = 0; i < newGraph.length; i++) {
            tmpVal = newVal[i];
            tmpVal /= 1000;


            tmpTime  = newTime[i] - now;
            tmpTime /= 1000;

            newGraph[i] = new DataPoint(tmpTime, tmpVal);
            graphLastXValue = tmpTime;

        }

        //add stuff here for changing the looks of the lines etc.
        mSeries = new LineGraphSeries<>(newGraph);
        mSeries.setDrawDataPoints(true);
        mSeries.setDrawBackground(true);
        mSeries.setDataPointsRadius(10);

        Log.d("GRAPH", "New Graph succesfully created");
        return mSeries;
    }

    public void updateGraph(double newVal, long newTime){
        //Log.d("GRAPH", "Updating graph...");

        tmpVal = newVal;
        tmpVal /= 1000;

        tmpTime = newTime - now;
        graphLastXValue = tmpTime / 1000;

        System.out.println("value ist: " + newVal);

        mSeries.appendData(new DataPoint(graphLastXValue, newVal), true, 20);
    }



    //FOR TESTING PURPOSES

}
