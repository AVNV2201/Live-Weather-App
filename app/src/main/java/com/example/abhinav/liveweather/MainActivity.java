package com.example.abhinav.liveweather;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    boolean flag1 = false, flag2 = false ;


    public void getData(View v ){

        EditText cityEditText = findViewById(R.id.cityName) ;
        String city = cityEditText.getText().toString() ;
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=4152fea3a1774f444bfb997c1464e1bd" ;

        DownloadInfo weatherInfo = new DownloadInfo() ;
        weatherInfo.execute(url) ;

        if( flag1 == false )
            Toast.makeText(this, "Something went wrong... Try Again!!!", Toast.LENGTH_SHORT).show();
        else if( flag2 == false )
            Toast.makeText(this, "City Not Found!!!", Toast.LENGTH_SHORT).show();


        flag1 = false;
        flag2 = false ;
    }

    public class DownloadInfo extends AsyncTask< String, Void, String > {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL( urls[0] ) ;
                HttpURLConnection connection = (HttpURLConnection)url.openConnection() ;
                InputStream in = connection.getInputStream() ;
                InputStreamReader reader = new InputStreamReader(in) ;

                String result = "" ;

                int data = reader.read() ;
                while( data != -1 ){
                    result += (char)data ;
                    data = reader.read() ;
                }

                flag1 = true ;

                return result ;

            } catch (Exception e) {
                e.printStackTrace();
                return "" ;
            }

        }

        @Override
        protected void onPostExecute(String s) {

            if( s.isEmpty() ) return ;

            try {
                JSONObject info = new JSONObject(s) ;

                TextView tempTextView = findViewById(R.id.TempTextView) ;
                TextView mintempTextView = findViewById(R.id.minTempTextView) ;
                TextView maxtempTextView = findViewById(R.id.maxTempTextView) ;
                TextView cityNameTextView = findViewById(R.id.cityTextView) ;
                TextView precipitationTextView = findViewById(R.id.precipitationTextView) ;
                TextView descriptionTextView = findViewById(R.id.descriptionTextView) ;
                TextView longitudeTextView = findViewById(R.id.longirudeTextView) ;
                TextView latitudeTextView = findViewById(R.id.latitudeTextView) ;

                String city = info.getString("name") ;
                cityNameTextView.setText(city);

                JSONObject coord = new JSONObject(info.getString("coord")) ;
                double val = coord.getDouble("lon") ;
                String lon = String.valueOf(Math.abs(val)) ;
                if( val > 0 ) lon += " E" ;
                else if( val < 0 ) lon += " W" ;
                longitudeTextView.setText(lon);
                val = coord.getDouble("lat") ;
                String lat = String.valueOf(Math.abs(val)) ;
                if( val > 0 ) lat += " N" ;
                else if( val < 0 ) lat += " S" ;
                latitudeTextView.setText(lat);

                JSONObject temp = new JSONObject(info.getString("main")) ;
                int i = (int)Math.round(temp.getDouble("temp") - 273 ) ;
                String tmp = String.valueOf(i) + "° C" ;
                tempTextView.setText(tmp);
                i = (int)Math.round(temp.getDouble("temp_min") - 273 ) ;
                tmp = "Min: " + String.valueOf(i) + "°C" ;
                mintempTextView.setText(tmp);
                i = (int)Math.round(temp.getDouble("temp_max") - 273 ) ;
                tmp = "Max: " + String.valueOf(i) + "°C" ;
                maxtempTextView.setText(tmp);

                JSONArray arr = new JSONArray( info.getString("weather")) ;
                JSONObject weather = arr.getJSONObject(0) ;
                precipitationTextView.setText( weather.getString("main") );
                descriptionTextView.setText( weather.getString("description") );

                ImageView image = findViewById(R.id.imageView ) ;
                if( weather.getString("main").equals("Rain") || weather.getString("main").equals("Driaale") )
                    image.setImageResource(R.drawable.rain);
                else
                    image.setImageResource(R.drawable.clear);

                flag2 = true ;

            } catch (Exception e) {
                e.printStackTrace();
                return ;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
