package activitysensing.datacollection;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationListener;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * Created by saharsh on 10/16/16.
 */

public class GPSService extends Service implements LocationListener {

    private FileWriter writer;
    private FileOutputStream output;
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
            File path = this.getExternalFilesDir(null);
            File outputFile = new File(path, "gps-data" + Long.toString(System.currentTimeMillis() / 1000) + ".csv");
            Toast.makeText(this, outputFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            output = new FileOutputStream(outputFile, true);
            writer = new FileWriter(output.getFD());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Service Started for GPS", Toast.LENGTH_LONG).show();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No GPS Permission", Toast.LENGTH_LONG).show();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        return START_STICKY;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        double latitude = (location.getLatitude());
        double longitude = (location.getLongitude());
        long tsLong = System.currentTimeMillis()/1000;
        if (tsLong > lastTime + period) {
            lastTime = tsLong;
            writeDataToFile(latitude, longitude, tsLong);
        }
    }

    // write to file a line in format:
    // epochtime, x, y, z
    public void writeDataToFile(double latitude, double longitude, Long tsLong){
        String ts = tsLong.toString();
        String gpsLine = ts+", " +
                Double.toString(latitude) + ", " +
                Double.toString(longitude) + ", ";
        try {
            writer.write(gpsLine);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "failed write to file", Toast.LENGTH_LONG).show();
        }
    }

}
