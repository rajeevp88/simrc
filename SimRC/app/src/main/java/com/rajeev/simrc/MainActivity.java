package com.rajeev.simrc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rajeev.simrc.backend.MessageCorresponder;
import com.rajeev.simrc.backend.SocketHelper;

import java.io.IOException;
import java.net.Socket;


public class MainActivity extends Activity {

    private RelativeLayout layout_rc, layout_camera;

    private Vibrator v ;

    private TextView textView1, textView2, textView3, textView4, textView5;
    private TextView textView6, textView7, textView8, textView9, textView10;

    private WebView webView;

    private Controller remoteController, cameraController;

    private MessageCorresponder messageCorresponder;

    private static String CAMERA_IP = "192.168.1.30";
    private static String SERVER_IP = "192.168.1.31";
  //  private static String SERVER_IP = "192.168.1.27";
    private static Integer CAMERA_PORT = 8080;
    private static Integer SERVER_PORT = 6666;

    private Button startButton, stopButton;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
      //  getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setContentView(R.layout.activity_tablet_landscape);

//        this.socket = new Socket();
//
//        SocketHelper x = new SocketHelper(this.socket, SERVER_IP, SERVER_PORT);
//
//        x.execute();





//
//
//
//
//        try {
//            messageCorresponder = new MessageCorresponder(socket, SERVER_IP, SERVER_PORT);
//        } catch (IOException e) {
//            Log.d("onCreate", "Error instantiating socket connection");
//            e.printStackTrace();
//        }
        setUpButtons();

        setUpCamera();

        setUpTouchListeners();

    //    loadPref();

    }


    private void setUpButtons() {

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            String message = buildMessage("ON0", 000);
                try {
                    new MessageCorresponder(SERVER_IP, SERVER_PORT).execute(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            String message = buildMessage("OFF", 000);
                try {
                    new MessageCorresponder(SERVER_IP, SERVER_PORT).execute(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void setUpCamera() {

        webView = (WebView)findViewById(R.id.cameraView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://" + CAMERA_IP + ":" + CAMERA_PORT + "/");
    }

    private void setUpTouchListeners() {

        layout_rc = (RelativeLayout)findViewById(R.id.layout_joystick);
        layout_camera = (RelativeLayout)findViewById(R.id.layout_camera);

        remoteController = new Controller(getApplicationContext(), layout_rc, R.drawable.image_button);
        remoteController.setStickSize(150, 150);
        remoteController.setLayoutSize(500, 500);
        remoteController.setLayoutAlpha(150);
        remoteController.setStickAlpha(100);
        remoteController.setOffset(90);
        remoteController.setMinimumDistance(50);

        cameraController = new Controller(getApplicationContext(), layout_camera, R.drawable.image_button);
        cameraController.setStickSize(150, 150);
        cameraController.setLayoutSize(500, 500);
        cameraController.setLayoutAlpha(150);
        cameraController.setStickAlpha(100);
        cameraController.setOffset(90);
        cameraController.setMinimumDistance(50);

        layout_rc.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {

                int power = 0;
                String dir = "";
                String power_dir = "";

                if(v.hasVibrator())
                    v.vibrate(50);

                long eventDuration = arg1.getEventTime() - arg1.getDownTime();

                System.out.println("Touch sensitiveness measurement. Time duration = " + eventDuration);

                remoteController.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {

                    Log.d("X : " , String.valueOf(remoteController.getX()));
                    Log.d("Y : " , String.valueOf(remoteController.getY()));
                    Log.d("Power : " , String.valueOf(remoteController.getPower()));
                    Log.d("Distance : " , String.valueOf(remoteController.getDistance()));

                    power = remoteController.getPower();

                    int direction = remoteController.get4Direction();
                    if (direction == Controller.STICK_UP) {

                        dir = "FWD";
                        Log.d("Direction : ", dir);

                    } else if (direction == Controller.STICK_RIGHT) {

                        dir = "RHT";
                        Log.d("Direction : ", dir);

                    } else if (direction == Controller.STICK_DOWN) {

                        dir = "BCK";
                        Log.d("Direction : ", dir);

                    } else if (direction == Controller.STICK_LEFT) {

                        dir = "LFT";
                        Log.d("Direction : ", dir);

                    } else if (direction == Controller.STICK_NONE) {

                        dir = "STP";
                        Log.d("Direction : ", dir);

                    }

                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    dir = "STP";
                    Log.d("Direction : ", dir);
                }

                String message = buildMessage(dir, power);


                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    messageCorresponder.execute(message);
                }
                else {
                    textView3.setText("No network connection available.");
                }
                return true;
            }
        });

        layout_camera.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                // Start without a delay
                // Each element then alternates between vibrate, sleep, vibrate, sleep...
                long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                if(v.hasVibrator())
                    v.vibrate(50);

                cameraController.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {

                    Log.d("X : " , String.valueOf(cameraController.getX()));
                    Log.d("Y : " , String.valueOf(cameraController.getY()));
                    Log.d("Power : " , String.valueOf(cameraController.getPower()));
                    Log.d("Distance : " , String.valueOf(cameraController.getDistance()));

                    int direction = cameraController.get4Direction();

                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });

    }


    private String buildMessage(String direction, int power) {

        String formattedPower = String.format("%03d", power);

        String message = "S";
        message += direction;
        message += formattedPower;
        message += "00";
        message += "E";

        return message;
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

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    private void loadPref(){
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SERVER_IP = mySharedPreferences.getString("serverip", "");
        SERVER_PORT = mySharedPreferences.getInt("serverport", 1111);
        CAMERA_IP = mySharedPreferences.getString("cameraip", "");
        CAMERA_PORT = mySharedPreferences.getInt("cameraport", 1111);
        Log.d("test", CAMERA_IP);
    }
}