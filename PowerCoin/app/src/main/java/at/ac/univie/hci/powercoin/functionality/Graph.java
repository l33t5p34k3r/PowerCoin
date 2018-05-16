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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Graph {

    /**
     * mSeries: initializes Array of DataPoints for Graph
     */
    private LineGraphSeries<DataPoint> mSeries;

    /**
     * graphLastXValue: refers to the last x-value in the graph
     * tmpTime: temporal value mostly used for time
     * tmpVal: temporal value mostly used for value of bitcoin
     * now: value that saves the current time
     * context: used for Toast messages on values
     */
    private double graphLastXValue = 0;
    private Context context;

    /**
     * Graph Context setter
     * @param context
     */
    public Graph(Context context){
        this.context = context;
    }


    /**
     * Initializes graph
     * @param oldVal array of all the value-entries
     * @param oldTime array of all the time-entries
     * @return data points with values for graph
     */
    public LineGraphSeries<DataPoint> newGraph(double [] oldVal, long [] oldTime){
        Log.i("GRAPH", "New Graph being created");

        Long [] time = new Long[oldTime.length];


        for (int i = 0; i < oldVal.length; i++) {
            time[i] =  oldTime[i];
        }

        Set<Long> tmp = new HashSet<>();

        for (int i = 0; i < time.length; i++) {
            tmp.add(time[i]);
        }

        time =  tmp.toArray(new Long[tmp.size()]);

        DataPoint[] newGraph = new DataPoint[time.length];

        int count = 0;

        for (int i = 0; i < oldVal.length; i++) {

            Date date = new Date(oldTime[i]);

            if (date.getTime() > graphLastXValue) {
                newGraph[count] = new DataPoint(date, oldVal[i]);
                graphLastXValue = date.getTime();
                count++;
            }
        }

        mSeries = new LineGraphSeries<>(newGraph);


        mSeries.setDrawDataPoints(true);
        mSeries.setDataPointsRadius(10);

        mSeries.setDrawBackground(true);

        mSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast toast;
                toast = Toast.makeText(context, " $ " + dataPoint.getY(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Log.i("GRAPH", "New Graph succesfully created");
        return mSeries;
    }

    /**
     * Updates Graph
     * @param newVal
     * @param newTime
     */
    public void updateGraph(double newVal, long newTime){

        Date date = new Date(newTime);
        if (date.getTime() > graphLastXValue) {
            graphLastXValue = date.getTime();
            mSeries.appendData(new DataPoint(graphLastXValue, newVal), false, Integer.MAX_VALUE);
        }
    }
}
