package com.rajeev.simrc;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rajeev.simrc.backend.MessageCorresponder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    private RelativeLayout layout_rc, layout_camera;

    private TextView textView1, textView2, textView3, textView4, textView5;
    private TextView textView6, textView7, textView8, textView9, textView10;

    private WebView webView;

    private RemoteController remoteController, cameraController;

    private MessageCorresponder messageCorresponder;

    private Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tablet_landscape);

        webView = (WebView)findViewById(R.id.cameraView);

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        textView6 = (TextView)findViewById(R.id.textView6);
        textView7 = (TextView)findViewById(R.id.textView7);
        textView8 = (TextView)findViewById(R.id.textView8);
        textView9 = (TextView)findViewById(R.id.textView9);
        textView10 = (TextView)findViewById(R.id.textView10);

        layout_rc = (RelativeLayout)findViewById(R.id.layout_joystick);
        layout_camera = (RelativeLayout)findViewById(R.id.layout_camera);

        webView.loadUrl("http://192.168.1.153:8080/");

        remoteController = new RemoteController(getApplicationContext(), layout_rc, R.drawable.image_button);
        remoteController.setStickSize(150, 150);
        remoteController.setLayoutSize(500, 500);
        remoteController.setLayoutAlpha(150);
        remoteController.setStickAlpha(100);
        remoteController.setOffset(90);
        remoteController.setMinimumDistance(50);

        cameraController = new RemoteController(getApplicationContext(), layout_camera, R.drawable.image_button);
        cameraController.setStickSize(150, 150);
        cameraController.setLayoutSize(500, 500);
        cameraController.setLayoutAlpha(150);
        cameraController.setStickAlpha(100);
        cameraController.setOffset(90);
        cameraController.setMinimumDistance(50);

        setUpTouchListeners();

    }

    private void setUpTouchListeners() {

        layout_rc.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                long eventDuration = arg1.getEventTime() - arg1.getDownTime();

                System.out.println("Touch sensitiveness measurement. Time duration = " + eventDuration);

                remoteController.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(remoteController.getX()));
                    textView2.setText("Y : " + String.valueOf(remoteController.getY()));
                    textView3.setText("Angle : " + String.valueOf(remoteController.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(remoteController.getDistance()));

                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new MessageCorresponder().execute(String.valueOf(cameraController.getAngle()));
                    }
                    else {
                        textView3.setText("No network connection available.");
                    }

                    int direction = remoteController.get4Direction();
                    if (direction == RemoteController.STICK_UP) {
                        textView5.setText("Direction : Up");
                    } else if (direction == RemoteController.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                    } else if (direction == RemoteController.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                    } else if (direction == RemoteController.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                    } else if (direction == RemoteController.STICK_NONE) {
                        textView5.setText("Direction : Center");
                    }
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");
                }
                return true;
            }
        });

        layout_camera.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                cameraController.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView6.setText("X : " + String.valueOf(cameraController.getX()));
                    textView7.setText("Y : " + String.valueOf(cameraController.getY()));
                    textView8.setText("Angle : " + String.valueOf(cameraController.getAngle()));
                    textView9.setText("Distance : " + String.valueOf(cameraController.getDistance()));

                    int direction = cameraController.get4Direction();
                    if (direction == RemoteController.STICK_UP) {
                        textView10.setText("Direction : Up");
                    } else if (direction == RemoteController.STICK_RIGHT) {
                        textView10.setText("Direction : Right");
                    } else if (direction == RemoteController.STICK_DOWN) {
                        textView10.setText("Direction : Down");
                    } else if (direction == RemoteController.STICK_LEFT) {
                        textView10.setText("Direction : Left");
                    } else if (direction == RemoteController.STICK_NONE) {
                        textView10.setText("Direction : Center");
                    }
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView6.setText("X :");
                    textView7.setText("Y :");
                    textView8.setText("Angle :");
                    textView9.setText("Distance :");
                    textView10.setText("Direction :");
                }
                return true;
            }
        });
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
}
