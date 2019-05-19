package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;

public class ServerThread extends Thread {
    private int port = 0;
    private ServerSocket serverSocket = null;

    private HashMap<String, ContainerClass> data = null;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {

        }
        this.data = new HashMap<>();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setData(String city, ContainerClass weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public synchronized HashMap<String, ContainerClass> getData() {
        return data;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("abc", "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i("abc", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.i("abc", "nu a mers");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.i("abc", "nu a mers");
            }
        }
    }



}
