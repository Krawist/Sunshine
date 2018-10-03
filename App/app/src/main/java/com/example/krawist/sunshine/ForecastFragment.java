package com.example.krawist.sunshine;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> mForecastadapter;


    public ForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.forecast_menu,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh:
                new FetchWeatherTask().execute("Yaounde,CM ");
                Toast.makeText(getActivity(),"Refresh is clicked",Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        String[] days = {"Today-Sunny-88/63",
                "Tomorrow-Foggy-70/46",
                "Weds-Cloudy-72/63",
                "Thurs-Rainy-64/51",
                "Fri-Foggy-70/46",
                "Sat-Sunny-76/68",
                "Sun-Rainy-54/40"};

        List<String> listOfDays = new ArrayList<>(Arrays.asList(days));

        mForecastadapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_textview,
                listOfDays);

        ListView listView = rootView.findViewById(R.id.fragement_main_listview);

        listView.setAdapter(mForecastadapter);

        new FetchWeatherTask().execute("Yaounde,CM ");

        return rootView;
    }



    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {

           if(strings!=null){
               mForecastadapter.clear();

               for(String dayForecast: strings){
                   mForecastadapter.add(dayForecast);
               }
           }

        }

        @Override
        protected String[] doInBackground(String... strings) {
            String[] daysWeather = null;
            if(strings.length==0){
                return null;
            }else{
                String forecastResult = doRequest(strings);
                try {
                    daysWeather = WeatherDataParser.getWeatherDataFromJson(forecastResult, 7);
                }catch(JSONException e){
                    Log.e(LOG_TAG,e.getMessage());
                }

                for(int i=0;i<7;i++){
                    Log.v(LOG_TAG,daysWeather[i]);
                }
            }


            return daysWeather;
        }

        private String doRequest(String... strings){
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            /*list of param value */
            String resultFormat = "json";
            int numDays = 7;
            String units = "metric";
            String api_key = "47dbb5cb61a2142ba31fe8366d68b9a2";
            String langage = "Fr";

            /*list of all parameter */
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?";
            final String FORMAT_PARAM = "mode";
            final String LOCATION_PARAM = "q";
            final String API_KEY_PARAM = "APPID";
            final String UNIT_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String LANGAGE_PARAM = "lang";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("q=94043&mode=json&APPID=47dbb5cb61a2142ba31fe8366d68b9a2&units=metric&cnt=7");




                Uri builtUri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon().
                        appendQueryParameter(LOCATION_PARAM,strings[0])
                        .appendQueryParameter(FORMAT_PARAM,resultFormat)
                        .appendQueryParameter(API_KEY_PARAM,api_key)
                        .appendQueryParameter(UNIT_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .appendQueryParameter(LANGAGE_PARAM,langage)
                        .build();

                URL url = new URL(builtUri.toString());

                //Log.e(LOG_TAG,builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }

                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG,forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return forecastJsonStr;
        }
    }

}
