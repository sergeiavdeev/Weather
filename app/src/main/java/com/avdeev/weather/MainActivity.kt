package com.avdeev.weather

import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import com.avdeev.weather.network.YandexGeocoderAPI
import com.avdeev.weather.network.YandexWeatheAPI
import com.avdeev.weather.network.data.WeatherResponse
import com.avdeev.weather.network.data.YandexGeocoderResponse
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val yandexWeather: YandexWeatheAPI = YandexWeatheAPI()
    private val yandexGeocode: YandexGeocoderAPI = YandexGeocoderAPI()
    private var lat: Double = 0.0;
    private var lon: Double = 0.0;

    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        icon.typeface = Typeface.createFromAsset(assets, "weather.ttf")

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    // ...
                    var a = 1;
                }
            }
        }

        getLocation();
        sendServerRequest();

    }

    private fun renderData(dataModel: WeatherResponse?, error: Throwable?) {
        if (dataModel == null || dataModel.fact == null || error != null) {
            //Ошибка
        } else {
            //city_field.text = getWorldCities()[2].city
            date_field.text = dataModel.now_dt.toString();
            val fact = dataModel.fact
            val temperature = fact.temp
            if (temperature == null) {
                //"Поле пустое")
            } else {
                current_temperature_field.text = temperature.toString()
            }

            val feelsLike = fact.feels_like
            if (feelsLike == null) {
                //"Поле пустое")
            } else {
                feel_temperature_field.text = feelsLike.toString()
            }

            val condition = fact.condition
            if (condition.isNullOrEmpty()) {
                //"Описание пустое"
            } else {
                when (condition) {
                    "clear" -> icon.text = getString(R.string.weather_sunny)
                    "partly-cloudy", "cloudy", "overcast" -> icon.text =
                            getString(R.string.weather_cloudy)
                    "partly-cloudy-and-light-rain", "cloudy-and-light-rain", "overcast-and-light-rain" -> icon.text =
                            getString(R.string.weather_drizzle)
                    "partly-cloudy-and-rain", "overcast-and-rain", "cloudy-and-rain" -> icon.text =
                            getString(R.string.weather_rainy)
                    "overcast-thunderstorms-with-rain" -> icon.text =
                            getString(R.string.weather_thunder)
                    "overcast-and-wet-snow", "partly-cloudy-and-light-snow", "partly-cloudy-and-snow", "overcast-and-snow", "cloudy-and-light-snow", "overcast-and-light-snow", "cloudy-and-snow" -> icon.text =
                            getString(R.string.weather_snowy)
                    else -> icon.text = getString(R.string.weather_sunny)
                }
            }
        }
    }

    private fun sendServerRequest() {

        yandexWeather.api()
                .informers(getString(R.string.weather_token), lat, lon)
                .enqueue(object :
                        Callback<WeatherResponse> {

                    override fun onResponse(
                            call: Call<WeatherResponse>,
                            response: Response<WeatherResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            renderData(response.body(), null)
                        } else {
                            renderData(null, Throwable("Ответ от сервера пустой"))
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        renderData(null, t)
                    }
                })
    }

    private fun geocode(lat: Double, lon: Double) {
        yandexGeocode.api()
            .geocode(getString(R.string.geocode_token), "$lon,$lat")
            .enqueue(object: Callback<YandexGeocoderResponse>{

                override fun onResponse(call: Call<YandexGeocoderResponse>, response: Response<YandexGeocoderResponse>) {
                    var body: YandexGeocoderResponse? = null;
                    if (response.isSuccessful && response.body() != null) {
                        body = response.body();
                        if (body != null) {
                            city_field.text = body.response.GeoObjectCollection.featureMember[0].GeoObject.name;
                            city_descr.text = body.response.GeoObjectCollection.featureMember[0].GeoObject.description;
                        }

                    } else {
                        TODO("Bad request")
                    }
                }

                override fun onFailure(call: Call<YandexGeocoderResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun getLocation() {

        if (!checkPermission())return;

        val locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                lat = it.latitude;
                lon = it.longitude;
                sendServerRequest();
                geocode(lat, lon);
            }
        }

        locationClient.requestLocationUpdates(
            LocationRequest.create(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun checkPermission() : Boolean {

        var permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!permission) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }

        return permission;
    }


}




