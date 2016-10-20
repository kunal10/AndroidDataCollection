package activitysensing.datacollection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class AccelService extends Service implements SensorEventListener {
    private FileWriter writer;
    private FileOutputStream output;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastTime = 0;
    private int period = 5;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        try {
//            File path = this.getExternalFilesDir(null);
            String pathStr = Environment.getExternalStorageDirectory().getPath() + "/accel-data/"+Long.toString(System.currentTimeMillis()/1000)+".csv";
            File outputFile = new File(pathStr);
            Toast.makeText(this, outputFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            output = new FileOutputStream(outputFile, true);
            writer = new FileWriter(output.getFD());
        } catch(Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "Service Started for accelerometer", Toast.LENGTH_LONG).show();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        long tsLong = System.currentTimeMillis()/1000;
        if (tsLong > lastTime + period) {
            lastTime = tsLong;
            writeDataToFile(x, y, z, tsLong);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        try {
            writer.close();
            output.getFD().sync();
            output.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // write to file a line in format:
    // epochtime, x, y, z
    public void writeDataToFile(float x, float y, float z, Long tsLong){
        String ts = tsLong.toString();
        String accelLine = ts+", " +
                Float.toString(x) + ", " +
                Float.toString(y) + ", " +
                Float.toString(z) + "\n";
        try {
            writer.write(accelLine);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "failed write to file", Toast.LENGTH_LONG).show();
        }
    }
}
