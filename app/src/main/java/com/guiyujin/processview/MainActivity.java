package com.guiyujin.processview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ProcessView processView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processView = findViewById(R.id.process);
        processView.setProgressMax(100);
        processView.setProgressStart(0);
        processView.setProcessEnd(50);
        processView.setType(ProcessView.MANUAL_INCREASE);
        processView.start();
        processView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click"
                        , Toast.LENGTH_SHORT).show();
                processView.setProcessEnd(processView.getProcessEnd() + 10);
                processView.start();
            }
        });
        processView.setOnCompleteListener(new ProcessView.onCompleteListener() {
            @Override
            public void onComplete() {
                Toast.makeText(getApplicationContext(), "Complete"
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }
}