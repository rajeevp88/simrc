package com.rajeev.simrc.backend;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Created by rajeev on 2/24/15.
 */
public class MessageCorresponder extends AsyncTask<String, Void, String> {

    private String serverIp;
    private Integer serverPort;
    private String response = "";

    public MessageCorresponder(String serverIp, Integer serverPort) throws IOException {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    @Override
    protected String doInBackground(String... message) {

        // params comes from the execute() call: params[0] is the url.
        try {
            transmitMessage(message[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void transmitMessage(String message) throws IOException {

        URL url = new URL("http://" + this.serverIp + ":" + this.serverPort + "/rcp?command=" + message.toString());

//        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//        try {
//            urlConn.setDoInput (true);
//            urlConn.setDoOutput (true);
//            urlConn.setUseCaches (false);
//            urlConn.setRequestMethod("POST");
//            urlConn.setChunkedStreamingMode(100);
//            urlConn.setRequestProperty("Content-Type","text/plain");
//            urlConn.connect();
//            // Send POST output.
//            DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
//            String output = URLEncoder.encode(message.toString(), "UTF-8");
//            Log.d("postTaskURL",output);
//            printout.writeUTF(output);
//            printout.flush();
//            printout.close();
//
//
//
//        }
//        finally {
//            urlConn.disconnect();
//        }m

        try(Socket socket = new Socket(this.serverIp, this.serverPort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());) {

            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            socket.close();
            socket.shutdownOutput();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

