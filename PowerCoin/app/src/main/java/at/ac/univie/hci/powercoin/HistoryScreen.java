package at.ac.univie.hci.powercoin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class HistoryScreen  extends AppCompatActivity{

    private TextView historyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_history_screen);

        Intent intent = getIntent();

        historyView = findViewById(R.id.textViewHist);
        String historyText = intent.getStringExtra(PortfolioScreen.HISTORY_MESSAGE);
        historyView.setText(historyText);
    }

}
