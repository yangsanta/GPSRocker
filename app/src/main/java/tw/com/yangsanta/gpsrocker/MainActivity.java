package tw.com.yangsanta.gpsrocker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity implements LocationListener {
    private Button start;
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;    //最佳資訊提供者
    final private int PERMISSION_LOCATION_REQUEST_CODE = 999;
    private boolean getService = false;
    public static double lat;
    public static double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        LocationManager status = (LocationManager) (MainActivity.this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            getService = true;
            locationServiceInitial();
        } else {
            Toast.makeText(MainActivity.this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, ChatHeadService.class));
//                Runtime runtime = Runtime.getRuntime();
//                try {
////                    Process proc=runtime.exec("su");
////                    DataOutputStream opt = new DataOutputStream(proc.getOutputStream());
////                    opt.writeBytes("ifdown eth0\n");
////                    opt.writeBytes("exit\n");
////                    opt.flush();
//                    do_exec("geo fix -121.45356 46.51119 4392");
////                    do_exec("ifconfig");
//
//
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
        });


    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //        super.onBackPressed();
        if (getService) {
//            if (!MainActivity.checkPermission(this)) {
//                ActivityCompat.requestPermissions(
//                        this,
//                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSION_LOCATION_REQUEST_CODE);
//            }
//
            lms.requestLocationUpdates(bestProvider, 0, 0, this);
            //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (getService) {
//            if (!MainActivity.checkPermission(this)) {
//                ActivityCompat.requestPermissions(
//                        this,
//                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSION_LOCATION_REQUEST_CODE);
//            }
            lms.removeUpdates(this);    //離開頁面時停止更新
        }

    }

    @Override
    public void onLocationChanged(Location location) {    //當地點改變時
        // TODO Auto-generated method stub
        getLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);    //取得系統定位服務
        Criteria criteria = new Criteria();    //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者

//        if (!MainActivity.checkPermission(this)) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSION_LOCATION_REQUEST_CODE);
//        }
        Location location = lms.getLastKnownLocation(bestProvider);    //使用GPS定位座標
        getLocation(location);


    }

    private void getLocation(Location location) {    //將定位資訊顯示在畫面中
        if (location != null) {

            TextView longitude_txt = (TextView) findViewById(R.id.longitude);
            TextView latitude_txt = (TextView) findViewById(R.id.latitude);

            Double longitude = location.getLongitude();    //取得經度
            Double latitude = location.getLatitude();    //取得緯度
            lat = latitude;
            lon = longitude;
            longitude_txt.setText(String.valueOf(longitude));
            latitude_txt.setText(String.valueOf(latitude));
            Toast.makeText(this, "location:"+lat + "___" + lon, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }


//    public static boolean checkPermission(final Context context) {
//        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }


    String do_exec(String cmd) {
        String s = "/n";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "/n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.v("rsuult:", s);
        return cmd;
    }
}
