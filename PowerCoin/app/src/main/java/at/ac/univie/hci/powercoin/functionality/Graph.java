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

import java.util.HashSet;
import java.util.Set;

public class Graph {


    private LineGraphSeries<DataPoint> mSeries;

    private double graphLastXValue = -4000000.0;
    private double tmpTime;
    private double tmpVal;
    private long now;
    private Context context;

    public Graph(Context context){
        this.context = context;
    }


    //no idea how I did it, don't change
    public LineGraphSeries<DataPoint> newGraph(double [] oldVal, long [] oldTime){
        Log.d("GRAPH", "New Graph being created");

        now  = System.currentTimeMillis();

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



            tmpTime  = oldTime[i] - now;

            tmpTime /= 3600;

            if (tmpTime > graphLastXValue) {
                newGraph[count] = new DataPoint(tmpTime, oldVal[i]);
                graphLastXValue = tmpTime;
                count++;
            }

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
                toast = Toast.makeText(context, " $ " + dataPoint.getY(), Toast.LENGTH_SHORT);
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
        tmpTime /= 3600000;
        if (tmpTime > graphLastXValue) {
            graphLastXValue = tmpTime;
            mSeries.appendData(new DataPoint(graphLastXValue, newVal), true, Integer.MAX_VALUE);
        }
    }
}
