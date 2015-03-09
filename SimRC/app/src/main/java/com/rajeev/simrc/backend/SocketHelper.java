package com.rajeev.simrc.backend;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by rajeev on 3/8/15.
 */
public class SocketHelper extends AsyncTask<String, Void, Void> {

    private Socket socket;
    private String serverIp;
    private Integer serverPort;

    public SocketHelper(Socket socket, String serverIp, Integer serverPort){

        this.socket = socket;
        this.serverIp = serverIp;
        this.serverPort = serverPort;

    }

    @Override
    protected Void doInBackground(String... strings) {

        SocketAddress socketAddress = new InetSocketAddress(this.serverIp, this.serverPort);

        try {
            this.socket.connect(socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
