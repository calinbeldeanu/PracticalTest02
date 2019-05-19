package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }


    @Override
    public void run() {
        if (socket == null) {
            Log.e("abc", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        Log.d("abc", "Started Communication Thread");
        try {
            BufferedReader bufferedReader = getReader(socket);
            PrintWriter printWriter = getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e("abc", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("abc", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            // We read the first query sent in the ClientThread
            String query1 = bufferedReader.readLine();
            // We read the second query sent in the ClientThread
            String informationType = bufferedReader.readLine();

            if (query1 == null || query1.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e("abc", "[COMMUNICATION THREAD] Error receiving parameters from client (query1 / information type!");
                return;
            }

            HashMap<String, ContainerClass> dataServer = serverThread.getData();
            ContainerClass responseData;
            String result;

            if (dataServer.containsKey(query1)) {
                Log.i("abc", "[COMMUNICATION THREAD] Getting the information from the cache...");
                responseData = dataServer.get(query1);

            } else {
                Log.i("abc", "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                // In case of POST change to HttpPost and remover the arghuments from the urkl
                HttpGet httpPost = new HttpGet("https://samples.openweathermap.org/data/2.5/weather?" + "q=" + query1 + "&" + "appid=" + "b6907d289e10d714a6e88b30761fae22");
                List<NameValuePair> params = new ArrayList<>();
                // Use for the post
                //params.add(new BasicNameValuePair("q", "London,uk"));
                //params.add(new BasicNameValuePair("appid", "b6907d289e10d714a6e88b30761fae22"));
                //UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                //httpPost.setEntity(urlEncodedFormEntity);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e("abc", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }
                Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);
                //Log.d("abc", element.toString());

                /* Parsare de boss */
                Elements elements = element.getElementsByTag("body");
                Log.d("abc", elements.text());
                JSONObject jsonData = new JSONObject(elements.text());
                JSONObject querryData = jsonData.getJSONObject("main");
                Log.d("abc", querryData.getString("temp"));


                responseData = new ContainerClass(querryData.getString("temp"), querryData.getString("humidity"));
                serverThread.setData(query1, responseData);


            }
            result = null;

            if (informationType.equals("Temp")) {
                // we set just the temperature
                result = responseData.queryResponse1;

            } else

            if (informationType.equals("Humidity")) {
                result = responseData.queryResponse2;
            }


            // Send the data to the client
            printWriter.println(result);
            printWriter.flush();

            socket.close();
        }catch (Exception e){
            Log.d("abc", "Exceptie: + " + e);
        }






    }
            /*
            if (weatherForecastInformation == null) {
                Log.e("abc", "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            String result = null;
            switch(informationType) {
                case Constants.ALL:
                    result = weatherForecastInformation.toString();
                    break;
                case Constants.TEMPERATURE:
                    result = weatherForecastInformation.getTemperature();
                    break;
                case Constants.WIND_SPEED:
                    result = weatherForecastInformation.getWindSpeed();
                    break;
                case Constants.CONDITION:
                    result = weatherForecastInformation.getCondition();
                    break;
                case Constants.HUMIDITY:
                    result = weatherForecastInformation.getHumidity();
                    break;
                case Constants.PRESSURE:
                    result = weatherForecastInformation.getPressure();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e("abc", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e("abc", "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("abc", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
        */
}

