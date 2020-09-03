package android.support.p003v7.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.p000v4.content.PermissionChecker;
import android.util.Log;
import java.util.Calendar;

/* renamed from: android.support.v7.app.TwilightManager */
class TwilightManager {
    private static final int SUNRISE = 6;
    private static final int SUNSET = 22;
    private static final String TAG = "TwilightManager";
    private static final TwilightState sTwilightState = new TwilightState();
    private final Context mContext;
    private final LocationManager mLocationManager;

    /* renamed from: android.support.v7.app.TwilightManager$TwilightState */
    private static class TwilightState {
        boolean isNight;
        long nextUpdate;
        long todaySunrise;
        long todaySunset;
        long tomorrowSunrise;
        long yesterdaySunset;

        private TwilightState() {
        }
    }

    TwilightManager(Context context) {
        this.mContext = context;
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    /* access modifiers changed from: 0000 */
    public boolean isNight() {
        TwilightState twilightState = sTwilightState;
        if (isStateValid(twilightState)) {
            return twilightState.isNight;
        }
        Location lastKnownLocation = getLastKnownLocation();
        if (lastKnownLocation != null) {
            updateState(lastKnownLocation);
            return twilightState.isNight;
        }
        Log.i(TAG, "Could not get last known location. This is probably because the app does not have any location permissions. Falling back to hardcoded sunrise/sunset values.");
        int i = Calendar.getInstance().get(11);
        return i < 6 || i >= 22;
    }

    private Location getLastKnownLocation() {
        Location location = null;
        Location lastKnownLocationForProvider = PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION") == 0 ? getLastKnownLocationForProvider("network") : null;
        if (PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            location = getLastKnownLocationForProvider("gps");
        }
        if (location == null || lastKnownLocationForProvider == null) {
            if (location != null) {
                lastKnownLocationForProvider = location;
            }
            return lastKnownLocationForProvider;
        }
        if (location.getTime() > lastKnownLocationForProvider.getTime()) {
            lastKnownLocationForProvider = location;
        }
        return lastKnownLocationForProvider;
    }

    private Location getLastKnownLocationForProvider(String str) {
        LocationManager locationManager = this.mLocationManager;
        if (locationManager != null) {
            try {
                if (locationManager.isProviderEnabled(str)) {
                    return this.mLocationManager.getLastKnownLocation(str);
                }
            } catch (Exception e) {
                Log.d(TAG, "Failed to get last known location", e);
            }
        }
        return null;
    }

    private boolean isStateValid(TwilightState twilightState) {
        return twilightState != null && twilightState.nextUpdate > System.currentTimeMillis();
    }

    private void updateState(@NonNull Location location) {
        long j;
        TwilightState twilightState = sTwilightState;
        long currentTimeMillis = System.currentTimeMillis();
        TwilightCalculator instance = TwilightCalculator.getInstance();
        TwilightCalculator twilightCalculator = instance;
        twilightCalculator.calculateTwilight(currentTimeMillis - 86400000, location.getLatitude(), location.getLongitude());
        long j2 = instance.sunset;
        twilightCalculator.calculateTwilight(currentTimeMillis, location.getLatitude(), location.getLongitude());
        boolean z = instance.state == 1;
        long j3 = instance.sunrise;
        long j4 = j2;
        long j5 = instance.sunset;
        long j6 = j3;
        instance.calculateTwilight(currentTimeMillis + 86400000, location.getLatitude(), location.getLongitude());
        long j7 = instance.sunrise;
        if (j6 == -1 || j5 == -1) {
            j = 43200000 + currentTimeMillis;
        } else {
            long j8 = currentTimeMillis > j5 ? 0 + j7 : currentTimeMillis > j6 ? 0 + j5 : 0 + j6;
            j = j8 + 60000;
        }
        twilightState.isNight = z;
        twilightState.yesterdaySunset = j4;
        twilightState.todaySunrise = j6;
        twilightState.todaySunset = j5;
        twilightState.tomorrowSunrise = j7;
        twilightState.nextUpdate = j;
    }
}
