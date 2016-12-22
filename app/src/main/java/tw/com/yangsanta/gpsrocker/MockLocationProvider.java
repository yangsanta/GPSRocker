package tw.com.yangsanta.gpsrocker;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by USER on 2016/9/7.
 */
@TargetApi(21)
public class MockLocationProvider {
    String providerName;
    Context ctx;

    public MockLocationProvider(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;
//        int value = setMockLocationSettings();//toggle ALLOW_MOCK_LOCATION on
//
//        try {
            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);

            lm.addTestProvider(providerName, false, false, false, false, false,
                    true, true, 0, 5);
            lm.setTestProviderEnabled(providerName, true);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } finally {
//            restoreMockLocationSettings(value);//toggle ALLOW_MOCK_LOCATION off
//        }
    }

    public void pushLocation(double lat, double lon) {
//        int value = setMockLocationSettings();//toggle ALLOW_MOCK_LOCATION on
//
//        try {
            LocationManager lm = (LocationManager) ctx.getSystemService(
                    Context.LOCATION_SERVICE);

            Location mockLocation = new Location(providerName);
            mockLocation.setLatitude(lat);
            mockLocation.setLongitude(lon);
            mockLocation.setAltitude(0);
            mockLocation.setTime(System.currentTimeMillis());
            mockLocation.setAccuracy(100.0f);
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            lm.setTestProviderLocation(providerName, mockLocation);
//            Log.v("location", lat + "___" + lon);

//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } finally {
//            restoreMockLocationSettings(value);//toggle ALLOW_MOCK_LOCATION off
//        }
    }


    public void shutdown() {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);
    }


    @TargetApi(21)
    private int setMockLocationSettings() {
        int value = 1;
        try {
            value = Settings.Secure.getInt(ctx.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION);
            Settings.Secure.putInt(ctx.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @TargetApi(21)
    private void restoreMockLocationSettings(int restore_value) {
        try {
            Settings.Secure.putInt(ctx.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION, restore_value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* every time you mock location, you should use these code */


}
//
//
//public class MockLocationProvider {
//    String providerName;
//    Context ctx;
//
//    LocationManager lm;
//
//    public MockLocationProvider(String name, Context ctx) {
//        this.providerName = name;
//        this.ctx = ctx;
//
//        lm = (LocationManager) ctx.getSystemService(
//                Context.LOCATION_SERVICE);
//        lm.addTestProvider(providerName, false, false, false, false, false,
//                true, true, 0, 5);
//        lm.setTestProviderEnabled(providerName, true);
//    }
//
//
//    public void pushLocation(double lat, double lon) {
////        = (LocationManager) ctx.getSystemService(
////                Context.LOCATION_SERVICE);
//
//        Location mockLocation = new Location(providerName);
//        mockLocation.setLatitude(lat);
//        mockLocation.setLongitude(lon);
////        mockLocation.setAltitude(0);
//        mockLocation.setTime(System.currentTimeMillis());
//
//        mockLocation.setAccuracy(100.0f);
//
//        lm.setTestProviderLocation(providerName, mockLocation);
//    }
//
//    public void shutdown() {
////        LocationManager lm = (LocationManager) ctx.getSystemService(
////                Context.LOCATION_SERVICE);
//        lm.removeTestProvider(providerName);
//    }
//}


