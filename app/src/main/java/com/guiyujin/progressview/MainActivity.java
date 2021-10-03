package com.guiyujin.progressview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressView = findViewById(R.id.process);
        progressView.setProgressMax(100);
        progressView.setProgressStart(0);
        progressView.setProgressEnd(50);
        progressView.setType(ProgressView.MANUAL_INCREASE);
        progressView.start();
        progressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click"
                        , Toast.LENGTH_SHORT).show();
                if (progressView.getProgressEnd() <= 90){
                    progressView.setProgressEnd(progressView.getProgressEnd() + 10);
                }
                progressView.start();
            }
        });
        progressView.setOnCompleteListener(new ProgressView.onCompleteListener() {
            @Override
            public void onComplete() {
                Toast.makeText(getApplicationContext(), "Complete"
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }
}