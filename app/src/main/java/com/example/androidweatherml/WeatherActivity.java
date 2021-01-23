package com.example.androidweatherml;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pavlospt.CircleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Sovelluksen pääluokka, joka kontrolloi koko sovellusta.
 *
 * @author Matias Leinonen
 * @version 1.0
 * @see com.example.androidweatherml.DialogChangeCity.OnInputListener Tämän avulla saadaan DialogChangeCity-luokasta siirrettyä tietoa.
 */

public class WeatherActivity extends AppCompatActivity implements DialogChangeCity.OnInputListener {

    /**
     * TAG jota voidaan kutsua konsoli komennoissa
     */
    private static final String TAG = "WeatherActivity";

    /**
     * Paikka johon sharedPreferences tallennetaan
     */
    public static final String SHARED_PREFS = "sharedPrefs";
    /**
     * Oletuskaupungin tallennusmuuttuja
     */
    public static final String DEFAULTCITY = "";

    /**
     * Yleinen muuttuja oletuskaupungille
     */
    private String defaultCity;

    /**
     * Tekstikenttä joka esittää kaupungin ja maan
     */
    private TextView city_country;
    /**
     * Tekstikenttä joka esittää päivämäärän
     */
    private TextView date_text;
    /**
     * Tekstikenttä joka esittää tuulen nopeuden
     */
    private TextView wind_result;
    /**
     * Tekstikenttä joka esittää kosteuden
     */
    private TextView humidity_result;
    /**
     * Tekstikenttä joka esittää paineen
     */
    private TextView pressure_result;
    /**
     * Tekstikenttä joka esittää milloin säätila on päivitetty.
     */
    private TextView last_updated;
    /**
     * Näyttää säätyypin kuvakkeen
     */
    private ImageView weather_icon;
    /**
     * Näyttää lämpötilan ja säätyypin
     */
    private CircleView weather_description;


    /**
     * Luo sovelluksen näkymän ja kutsuu tarvittavia metodeita sekä muuttujia.
     *
     * @param savedInstanceState Hakee Activityn tilan.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //asetetaan näkymä
        setContentView(R.layout.activity_weather);

        //haetaan id:t Activity:stä
        city_country = (TextView) findViewById(R.id.city_country);
        date_text = (TextView) findViewById(R.id.date);
        weather_description = (CircleView) findViewById(R.id.weather_result);
        wind_result = (TextView) findViewById(R.id.wind_result);
        humidity_result = (TextView) findViewById(R.id.humidity_result);
        pressure_result = (TextView) findViewById(R.id.pressure);
        weather_icon = (ImageView) findViewById(R.id.weather_icon);
        last_updated = (TextView) findViewById(R.id.last_updated);

        //haetaan oletuskaupungin data SHARED_PREFS:stä
        loadData();
        //suoritetaan säätiedon haku oletuskaupungilla
        FindWeather(defaultCity);
    }

    /**
     * Tallentaa oletuskaupungin SHARED_PREFS:iin.
     *
     * @param input Antaa kaupungin minkä käyttäjä on syöttänyt.
     */
    public void saveData(String input) {
        //luodaan SHARED_PREFS ja editori
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //tallennetaan oletuskaupunki SHARED_PREFS:iin
        editor.putString(DEFAULTCITY, input);
        editor.apply();
    }

    /**
     * Hakee datan SHARED_PREFS:stä.
     */
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        //jos käyttäjä ei ole vielä valinnut oletuskaupunkia, käytetään luotua oletuskaupunkia
        if (sharedPreferences.getString(DEFAULTCITY, "").equals(""))
            defaultCity = "Kuopio";
        else
            //käytetään käyttäjän valitsemaa oletuskaupunkia
            defaultCity = sharedPreferences.getString(DEFAULTCITY, "");
    }

    /**
     * Suorittaa OpenWeatherMap API:sta säädatan hakemisen.
     *
     * @param input Antaa kaupungin minkä käyttäjä on syöttänyt.
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void FindWeather(String input) {

        try {
            //luodaan uusi task ja suoritetaan haku API:sta käyttämällä osoitetta
            ExecuteTask task = new ExecuteTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + input + "&APPID=037b9703d46b3ba35cb533029f15576e&units=metric");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Sisäluokka, joka pitää sisällään doInBackground ja onPostExecute-metodit.
     * Metodien avulla ohjelma voi suorittaa haun FindWeatherilla.
     * </p>
     *
     * @see android.os.AsyncTask Avulla pystytään suorittamaan background threadissa operaatioita.
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class ExecuteTask extends AsyncTask<String, Void, String> {

        /**
         * Luo yhteyden ja lukee saadun datan background threadissa.
         *
         * @param strings Muuttaa saadun API-osoitteen string-muotoon.
         */
        @Override
        protected String doInBackground(String... strings) {

            //luodaan tarvittavat yleiset muuttujat
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            //luodaan yhteys ja datan lukemiseen tarvittavat muuttujat
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();

                //luetaan data kirjain kerrallaan ja palautetaan saatu data
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Liittää käyttöliittymään tiedot JSONin avulla.
         *
         * @param s Tuo string-muodossa API:sta saadun datan.
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                //luodaan uusi JSONObject datan lukemista varten
                JSONObject jsonObject = new JSONObject(s);

                //luodaan yksittäiset objektin oman tietonsa lukemiseen
                JSONObject weather_object = jsonObject.getJSONArray("weather").getJSONObject(0);
                JSONObject main_object = jsonObject.getJSONObject("main");
                JSONObject sys_object = jsonObject.getJSONObject("sys");

                //asetetaan päivämäärämuoto
                String date = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(new Date());

                //asetetaan jokaiseen kenttään oma tieto
                city_country.setText(jsonObject.getString("name") + ", " + sys_object.getString("country"));
                date_text.setText(date);
                weather_description.setTitleText(main_object.getInt("temp") + "°");
                weather_description.setSubtitleText(weather_object.getString("main"));
                wind_result.setText(jsonObject.getJSONObject("wind").getString("speed") + " km/h");
                pressure_result.setText(main_object.getString("pressure") + " hPa");
                humidity_result.setText(main_object.getString("humidity") + "%");

                //haetaan sääkuvakkeet
                Drawable clear = getResources().getDrawable(R.drawable.clear);
                Drawable clouds = getResources().getDrawable(R.drawable.clouds);
                Drawable mist = getResources().getDrawable(R.drawable.mist);
                Drawable rain = getResources().getDrawable(R.drawable.rain);
                Drawable snow = getResources().getDrawable(R.drawable.snow);
                Drawable thunderstorm = getResources().getDrawable(R.drawable.thunderstorm);

                //asetetaan sääkuvakkeet objektista saadun tiedon perusteella
                if (weather_object.getString("main").equals("Clear"))
                    weather_icon.setImageDrawable(clear);
                else if (weather_object.getString("main").equals("Clouds"))
                    weather_icon.setImageDrawable(clouds);
                else if (weather_object.getString("main").equals("Mist"))
                    weather_icon.setImageDrawable(mist);
                else if (weather_object.getString("main").equals("Rain"))
                    weather_icon.setImageDrawable(rain);
                else if (weather_object.getString("main").equals("Snow"))
                    weather_icon.setImageDrawable(snow);
                else if (weather_object.getString("main").equals("Thunderstorm"))
                    weather_icon.setImageDrawable(thunderstorm);

                //muotoillaan viimeksi päivitetty-kohtaan aika
                DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.US);
                String updatedOn = df.format(new Date(jsonObject.getLong("dt") * 1000));
                last_updated.setText(getString(R.string.last_updated_text) + updatedOn);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Luo sovelluksen yläpalkkiin menun.
     *
     * @param menu Käyttöliittymä valikon hallintaan.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //laittaa menun näkyville yläpalkkiin
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
    }

    /**
     * Muokkaa sovelluksen yläpalkin tapahtumia.
     *
     * @param item MenuItem, jolla voidaan muokata mitä Menussa tapahtuu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //jokaisesta case:sta avautuu oma dialog
        switch (item.getItemId()) {
            case R.id.change_city:
                DialogChangeCity dialog = new DialogChangeCity();
                dialog.show(getSupportFragmentManager(), "DialogChangeCity");
                return true;

            case R.id.about:
                DialogAbout dialogAbout = new DialogAbout();
                dialogAbout.show(getSupportFragmentManager(), "DialogAbout");
                return true;

            case R.id.exit:
                DialogExit dialogExit = new DialogExit();
                dialogExit.show(getSupportFragmentManager(), "DialogExit");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Tuo WeatherActivityyn DialogChangeCity:stä saadun kaupungin.
     *
     * @param input Kaupunki minkä käyttäjä on syöttänyt.
     */
    @Override
    public void sendInput(String input) {
        Log.e(TAG, "sendInput: input received " + input);
    }

}
