package activitysensing.datacollection;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
    private LocationManager locationManager;
    private LocationListener locationListener;

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No GPS Permission", Toast.LENGTH_LONG).show();
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
        long tsLong = System.currentTimeMillis() / 1000;
        if (tsLong > lastTime + period) {
            lastTime = tsLong;
            writeDataToFile(latitude, longitude, tsLong);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    // write to file a line in format:
    // epochtime, x, y, z
    public void writeDataToFile(double latitude, double longitude, Long tsLong){
        String ts = tsLong.toString();
        String gpsLine = ts+", " +
                Double.toString(latitude) + ", " +
                Double.toString(longitude) + ", " + "\n";
        try {
            writer.write(gpsLine);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "failed write to file", Toast.LENGTH_LONG).show();
        }
    }

}
