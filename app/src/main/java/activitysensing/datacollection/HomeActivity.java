package activitysensing.datacollection;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        final Button button = (Button) findViewById(R.id.button_id);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Perform action on click
//            }
//        });
    }

    public void start(View v){
        Intent intentAccel = new Intent(this, AccelService.class);
        startService(intentAccel);
        Intent intentGPS = new Intent(this,GPSService.class);
        startService(intentGPS);
        Intent intentCam = new Intent(this,CameraService.class);
        startService(intentCam);
    }

    public void stop(View v){
        stopService(new Intent(getApplicationContext(), AccelService.class));
        stopService(new Intent(getApplicationContext(),GPSService.class));
        stopService(new Intent(getApplicationContext(),CameraService.class));
    }


}
