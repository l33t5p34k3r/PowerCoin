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

import java.text.SimpleDateFormat;
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
     *
     * @param context
     */
    public Graph(Context context) {
        this.context = context;
    }


    /**
     * Initializes DataPoints for Graph
     *
     * @param oldVal  array of all the value-entries
     * @param oldTime array of all the time-entries
     * @return data points with values for graph
     */
    public LineGraphSeries<DataPoint> newDataPoints(double[] oldVal, long[] oldTime, final String currSymbol) {
        Log.d("GRAPH.java", "New DataPointSeries being created");

        Long[] time = new Long[oldTime.length];


        for (int i = 0; i < oldVal.length; i++) {
            time[i] = oldTime[i];
        }

        Set<Long> tmp = new HashSet<>();

        for (int i = 0; i < time.length; i++) {
            tmp.add(time[i]);
        }

        time = tmp.toArray(new Long[tmp.size()]);

        DataPoint[] dataPointArray = new DataPoint[time.length];

        int count = 0;

        for (int i = 0; i < oldVal.length; i++) {

            if (oldTime[i] > graphLastXValue) {
                dataPointArray[count] = new DataPoint(oldTime[i], oldVal[i]);
                graphLastXValue = oldTime[i];
                count++;
            }
        }

        mSeries = new LineGraphSeries<>(dataPointArray);

        mSeries.setDrawDataPoints(false);
        mSeries.setDataPointsRadius(10);


        mSeries.setDrawBackground(true);

        mSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM HH:mm");
                Toast toast;
                toast = Toast.makeText(context, dataPoint.getY() + currSymbol + " at " + sdf.format(dataPoint.getX()) + "", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Log.d("GRAPH.java", "DataPoints successfully added to mSeries");
        return mSeries;
    }

    /**
     * Updates Graph
     *
     * @param newVal
     * @param newTime
     */
    public void update(double newVal, long newTime) {

        Log.d("GRAPH.java", "updating mSeries");

        if (newTime > graphLastXValue) {
            graphLastXValue = newTime;
            mSeries.appendData(new DataPoint(graphLastXValue, newVal), true, Integer.MAX_VALUE);
        }
        Log.d("GRAPH.java", "mSeries successfully updated");

    }
}
