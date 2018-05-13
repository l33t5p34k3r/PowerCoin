package at.ac.univie.hci.powercoin.functionality;

import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Graph {


    private LineGraphSeries<DataPoint> mSeries;
    private long graphLastXValue;


    public LineGraphSeries<DataPoint> newGraph(double [] newVal, long [] newTime){
        Log.d("GRAPH", "New Graph being created");

        DataPoint[] newGraph = new DataPoint[newVal.length];
        graphLastXValue = newTime[newTime.length-1];

        for (int i = 0; i < newGraph.length; i++) {
            newGraph[i] = new DataPoint(newTime[i], newVal[i]);
        }

        mSeries = new LineGraphSeries<>(newGraph);

        Log.d("GRAPH", "New Graph succesfully created");
        return mSeries;
    }

    public void updateGraph(double newVal, long newTime){
        //Log.d("GRAPH", "Updating graph...");
        System.out.println(graphLastXValue);
        graphLastXValue = newTime;
        System.out.println(graphLastXValue);
        mSeries.appendData(new DataPoint(graphLastXValue, newVal), true, 20);
    }



    //FOR TESTING PURPOSES

}
