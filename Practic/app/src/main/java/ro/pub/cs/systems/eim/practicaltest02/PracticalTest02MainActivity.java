package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText serverportedittext;
    Button connect ;
    EditText clientaddress ;
    EditText clientport ;
    EditText city ;
    Button getweather ;
    Spinner spin;
    TextView weatherTextForecastTextView;
    ServerThread serverThread;
    ClientThread clientThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        serverportedittext = (EditText) findViewById(R.id.server_port_edit_text);
        connect = (Button) findViewById(R.id.connect_button);
        clientaddress = (EditText) findViewById(R.id.client_address_edit_text);
        clientport = (EditText) findViewById(R.id.client_port_edit_text);
        city = (EditText) findViewById(R.id.city_edit_text);
        getweather = (Button) findViewById(R.id.get_weather_forecast_button);
        spin = (Spinner) findViewById(R.id.information_type_spinner);
        weatherTextForecastTextView = (TextView) findViewById(R.id.weather_forecast_text_view);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverPort = serverportedittext.getText().toString();
                // Verificare ca e corect
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                //verificare iara
                serverThread.start();
            }
        });


        getweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = city.getText().toString();
                String query2 = spin.getSelectedItem().toString();

                String clientPort = clientport.getText().toString();
                String clientAddress = clientaddress.getText().toString();

                clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), query, query2, weatherTextForecastTextView);
                clientThread.start();
            }
        });


    }

    @Override
    protected void onDestroy() {

        if (serverThread != null)
            serverThread.stopThread();

        super.onDestroy();


    }



}
