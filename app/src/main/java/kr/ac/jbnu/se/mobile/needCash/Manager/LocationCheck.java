package kr.ac.jbnu.se.mobile.needCash.Manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import kr.ac.jbnu.se.mobile.needCash.Data.PositionData;

public class LocationCheck {

    private PositionData positionData;

    private Context context;

    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationCheck(Context context) {
        this.context = context;
        this.positionData = new PositionData(0, 0);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void deleteLocationListener() {
        if (locationListener == null) return;
        locationManager.removeUpdates(locationListener);
    }

    public void addLocationListener(LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "좌표 수신을 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 100, 1, locationListener);
        locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);
        this.locationListener = locationListener;
    }

    public PositionData getMyPositionData(){
        return positionData;
    }
}
