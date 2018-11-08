package com.csci4176.jbstks.weatherapp;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * ALL weather icons made by iconixar [https://www.flaticon.com/authors/iconixar] from www.flaticon.com.
 * I am using them for free with attribution of the author (above) as advised in the article here
 * [https://support.flaticon.com/hc/en-us/articles/202798341-Are-Flaticon-contents-for-free-and-where-can-I-use-them-]
 *
 * Referenced content from Lab 6.
 *
 * Joanna Bistekos (B00710704)
 */
public class MainActivity extends AppCompatActivity {

    String cityName;

    // UI references
    CardView getWeatherBtn;
    EditText cityNameET;
    TextView city;
    TextView temperature;
    TextView mainWeather;
    TextView description;
    TextView highLowTemp;
    TextView humidityPercent;
    TextView cloudinessPercent;
    ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // locking portrait view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting UI references
        cityNameET          = findViewById(R.id.cityNameET);
        getWeatherBtn       = findViewById(R.id.getWeatherBtn);
        city                = findViewById(R.id.city);
        temperature         = findViewById(R.id.temperature);
        mainWeather         = findViewById(R.id.mainWeather);
        description         = findViewById(R.id.description);
        highLowTemp         = findViewById(R.id.highLowTemp);
        humidityPercent     = findViewById(R.id.humidityPercent);
        cloudinessPercent   = findViewById(R.id.cloudinessPercent);
        weatherIcon         = findViewById(R.id.weatherIcon);

        // get Halifax's current weather
        getWeather();
        cityName = "Halifax";

        getWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the city name
                cityName = cityNameET.getText().toString();
                // get the city's weather
                getWeather();

                // close the soft keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // check if view isn't focused
                if (getCurrentFocus() == null)
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                else
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    /**
     * Gets called when the app initially loads and whenever the search button is pressed.
     * Connects to the OpenWeatherMap API, and searches for the user inputted city
     */
    public void getWeather() {
        // using the API to search for the user inputted city, getting the data in the metric measurement system
        String url = "http://api.openweathermap.org/data/2.5/weather?appid=922d7ef7ac2604da77d2f8655e3f90d4&q=";
        String urlWithCity = url.concat(TextUtils.isEmpty(cityName) ? "Halifax,CA" : cityName);
        urlWithCity += "&units=metric";

        // build the request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, urlWithCity, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplicationContext(), cityName+"'s weather data has been loaded", Toast.LENGTH_SHORT).show();

                        try {
                            // getting JSON objects
                            JSONObject main = response.getJSONObject("main");
                            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                            JSONObject clouds = response.getJSONObject("clouds");

                            // setting values in the UI based on the values from the JSON
                            city.setText(String.valueOf(response.getString("name")));
                            temperature.setText(String.valueOf(main.getInt("temp"))+"°");
                            highLowTemp.setText(String.valueOf(main.getInt("temp_max"))+"° / "+String.valueOf(main.getInt("temp_min"))+"°");
                            mainWeather.setText(String.valueOf(weather.getString("main")));
                            changeIcon(String.valueOf(weather.getString("icon")));
                            description.setText(String.valueOf(weather.getString("description")));
                            humidityPercent.setText(String.valueOf(main.getInt("humidity"))+"%");
                            cloudinessPercent.setText(String.valueOf(clouds.getInt("all"))+"%");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();

                // provide different toasts depending on the error
                if (e.toString().equals("com.android.volley.ClientError"))
                    Toast.makeText(getApplicationContext(), "Error finding city", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Error retrieving weather data", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    /**
     * Takes in the icon code from the JSON and sets the weather image depending on that value
     *
     * @param icon the icon code from the JSON
     */
    public void changeIcon(String icon) {
        switch (icon) {
            case "01d":
                weatherIcon.setImageResource(R.drawable.clearday);
                break;
            case "01n":
                weatherIcon.setImageResource(R.drawable.clearnight);
                break;
            case "02d":
                weatherIcon.setImageResource(R.drawable.cloudday);
                break;
            case "02n":
                weatherIcon.setImageResource(R.drawable.cloudnight);
                break;
            case "03d":
            case "03n":
                weatherIcon.setImageResource(R.drawable.cloud);
                break;
            case "04d":
            case "04n":
                weatherIcon.setImageResource(R.drawable.cloudy);
                break;
            case "09d":
            case "09n":
                weatherIcon.setImageResource(R.drawable.rain);
                break;
            case "10d":
                weatherIcon.setImageResource(R.drawable.rainday);
                break;
            case "10n":
                weatherIcon.setImageResource(R.drawable.rainnight);
                break;
            case "11d":
                weatherIcon.setImageResource(R.drawable.stormday);
                break;
            case "11n":
                weatherIcon.setImageResource(R.drawable.stormnight);
                break;
            case "13d":
                weatherIcon.setImageResource(R.drawable.snowday);
                break;
            case "13n":
                weatherIcon.setImageResource(R.drawable.snownight);
                break;
            case "50d":
            case "50n":
                weatherIcon.setImageResource(R.drawable.fog);
                break;
            default:
                break;
        }
    }
}
