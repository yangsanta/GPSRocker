package tw.com.yangsanta.gpsrocker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by USER on 2016/9/6.
 */
public class ChatHeadService extends Service {
    private WindowManager windowManager;
    MockLocationProvider mock;
    private View rockerView, closeView;
    private Button settings, up, down, right, left;
    private boolean IsOpen = false;
    private boolean IsClick = false;
    private int[] location = new int[2];
    private ImageView colseImg;


    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mock = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);


        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rockerView = inflater.inflate(R.layout.rocker, null);

        LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        closeView = inflater2.inflate(R.layout.close, null);


        settings = (Button) rockerView.findViewById(R.id.settings);
        up = (Button) rockerView.findViewById(R.id.up);
        down = (Button) rockerView.findViewById(R.id.down);
        right = (Button) rockerView.findViewById(R.id.right);
        left = (Button) rockerView.findViewById(R.id.left);
        colseImg = (ImageView) closeView.findViewById(R.id.colseImg);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.lat +=0.00002d;
                mock.pushLocation(MainActivity.lat, MainActivity.lon);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.lat -=0.00002d;
                mock.pushLocation(MainActivity.lat, MainActivity.lon);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.lon += 0.00002d;
                mock.pushLocation(MainActivity.lat, MainActivity.lon);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.lon -= 0.00002d;
                mock.pushLocation(MainActivity.lat, MainActivity.lon);
            }
        });
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        final WindowManager.LayoutParams params2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params2.gravity = Gravity.BOTTOM | Gravity.CENTER;

        settings.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        IsClick = false;
                        show();

                        break;

                    case MotionEvent.ACTION_UP:
                        dissmis();

                        if (event.getRawX() - initialTouchX <= 0 && event.getRawY() - initialTouchY <= 0)
                            IsClick = false;
                        else
                            IsClick = true;

                        if (CalculatesDistance(settings, closeView)) {

                            ChatHeadService.this.stopSelf();
                        }
                        break;


                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(rockerView, params);
                        if (CalculatesDistance(settings, closeView)) {

                            colseImg.setLayoutParams(new LinearLayout.LayoutParams(70 * (int) metrics.density, 70 * (int) metrics.density));

                        } else {
                            colseImg.setLayoutParams(new LinearLayout.LayoutParams(50 * (int) metrics.density, 50 * (int) metrics.density));

                        }

                        break;

                }

                return IsClick;
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IsOpen) {
//                    rockerView.getLayoutParams().width = 30 * (int) metrics.density;
//                    rockerView.getLayoutParams().height = 30 * (int) metrics.density;
                    up.setVisibility(View.GONE);
                    down.setVisibility(View.GONE);
                    right.setVisibility(View.GONE);
                    left.setVisibility(View.GONE);
                    IsOpen = false;
                } else {
//                    rockerView.getLayoutParams().width = 90 * (int) metrics.density;
//                    rockerView.getLayoutParams().height = 90 * (int) metrics.density;
                    up.setVisibility(View.VISIBLE);
                    down.setVisibility(View.VISIBLE);
                    right.setVisibility(View.VISIBLE);
                    left.setVisibility(View.VISIBLE);
                    IsOpen = true;

                }

            }
        });
        windowManager.addView(rockerView, params);
        windowManager.addView(closeView, params2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rockerView != null) windowManager.removeView(rockerView);
        if (closeView != null) windowManager.removeView(closeView);

    }


    private boolean CalculatesDistance(View v1, View v2) {

        float v1X, v1Y, v2X, v2Y;

        settings.getLocationOnScreen(location);
        v1X = location[0] + v1.getWidth() / 2;
        v1Y = location[1] + v1.getHeight() / 2;

        closeView.getLocationOnScreen(location);
        v2X = location[0] + v2.getWidth() / 2;
        v2Y = location[1] + v2.getHeight() / 2;

        float verticalDistance;    //垂直距离
        float horizontalDistance;  //水平距离
        verticalDistance = Math.abs(v1X - v2X);
        horizontalDistance = Math.abs(v1Y - v2Y);
        float verticalThreshold;   //两矩形分离的垂直临界值
        float horizontalThreshold; //两矩形分离的水平临界值
        verticalThreshold = (v1.getWidth() + v2.getWidth()) / 2;
        horizontalThreshold = (v1.getHeight() + v2.getHeight()) / 2;

        if (verticalDistance > verticalThreshold || horizontalDistance > horizontalThreshold) {

            return false;
        } else {

            return true;
        }


    }


    public void show() {
        final Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        in.setDuration(500);
        colseImg.setVisibility(View.VISIBLE);
        colseImg.startAnimation(in);
    }

    public void dissmis() {
        final Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        in.setDuration(500);
        colseImg.setVisibility(View.INVISIBLE);
        colseImg.setVisibility(View.GONE);
        colseImg.startAnimation(in);
    }
}
