package at.ac.univie.hci.powercoin.at.ac.univie.hci.powercoin.functionality;

import android.os.Handler;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class Graph {

    //TODO: Get data from API at start of graph
    //TODO: Get data from API at every update

    private LineGraphSeries<DataPoint> mSeries;
    private double graphLastXValue = 5d;


    public LineGraphSeries<DataPoint> newGraph(){
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

    public void updateGraph(){
        //Log.d("GRAPH", "Updating graph...");
        graphLastXValue += 1d;
        mSeries.appendData(new DataPoint(graphLastXValue, getDataAPI()), true, 40);
    }

    //TODO: IMPLEMENT ACTUAL FUNCTION
    public double getDataAPI(){
        return getRandom();
    }

    //FOR TESTING PURPOSES
    //TODO: DELETE LATER
    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

}
