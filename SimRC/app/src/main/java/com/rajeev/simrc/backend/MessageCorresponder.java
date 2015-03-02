package com.rajeev.simrc.backend;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by rajeev on 2/24/15.
 */
public class MessageCorresponder extends AsyncTask<String, Void, String> {

    public static final String SERVER_IP = "192.168.0.198";
    private static final Integer SERVER_PORT = 6666;
    private String response = "";

    public MessageCorresponder() {
    }

    @Override
    protected String doInBackground(String... messages) {

        // params comes from the execute() call: params[0] is the url.
        try {
            transmitMessage(messages[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void transmitMessage(String message) throws IOException {

        URL url = new URL("http://192.168.1.81:3000");

        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        try {

            createMessage();


            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches (false);
            urlConn.setRequestMethod("POST");
            urlConn.setChunkedStreamingMode(100);
            urlConn.setRequestProperty("Content-Type","application/json");
            urlConn.connect();
            // Send POST output.
            DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
            String output = URLEncoder.encode("abc", "UTF-8");
            Log.d("postTaskURL",output);
            printout.writeUTF(output);
            printout.flush();
            printout.close();
        }
        finally {
            urlConn.disconnect();
        }

    }

    private void createMessage() {
      //  Mymessage

    }
}

