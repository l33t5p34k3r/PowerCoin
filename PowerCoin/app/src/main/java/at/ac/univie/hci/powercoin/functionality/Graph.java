package at.ac.univie.hci.powercoin.functionality;

import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Graph {

    //TODO: Get data from API at start of graph
    //TODO: Get data from API at every update

    private LineGraphSeries<DataPoint> mSeries;
    private double graphLastXValue = 5d;


    public LineGraphSeries<DataPoint> newGraph(double [] newVal, int [] newTime){
        Log.d("GRAPH", "New Graph being created");
        /*LineGraphSeries<DataPoint> mSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/
        mSeries = new LineGraphSeries<>();

        Log.d("GRAPH", "New Graph succesfully created");
        return mSeries;
    }

    public void updateGraph(double newVal){
        //Log.d("GRAPH", "Updating graph...");
        graphLastXValue += 1d;
        mSeries.appendData(new DataPoint(graphLastXValue, newVal), true, 20);
    }



    //FOR TESTING PURPOSES

}
