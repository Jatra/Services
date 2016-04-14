package uk.co.jatra.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.getBooleanExtra(ScanService.STOP_SERVICE, false)) {
            ScanService.stop(this);
            finish();
            return;
        }
        else  if (intent.getBooleanExtra(MyIntentService.STOP_INTENT_SERVICE, false)) {
            MyIntentService.stop(this);
            finish();
            return;
        }


        setContentView(R.layout.activity_main);

        Button intentServiceButton = (Button)findViewById(R.id.intentServiceButton);
        intentServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyIntentService.start(MainActivity.this);
            }
        });

        Button serviceButton = (Button)findViewById(R.id.serviceButton);
        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanService.start(MainActivity.this);
            }
        });

        Button stopServiceButton = (Button)findViewById(R.id.stopScanButton);
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanService.stop(MainActivity.this);
            }
        });
    }
}
