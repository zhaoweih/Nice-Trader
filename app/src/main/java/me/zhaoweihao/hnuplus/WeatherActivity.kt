package me.zhaoweihao.hnuplus

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.github.pwittchen.weathericonview.WeatherIconView
import com.taishi.flipprogressdialog.FlipProgressDialog
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest

import java.io.IOException

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.current.*
import me.zhaoweihao.hnuplus.Gson.Channel
import me.zhaoweihao.hnuplus.Gson.Forecast
import me.zhaoweihao.hnuplus.Gson.Weather
import me.zhaoweihao.hnuplus.Utils.HttpUtil
import me.zhaoweihao.hnuplus.Utils.Utility
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

class WeatherActivity : AppCompatActivity(), TencentLocationListener {

    private var latitudeStr: String? = null
    private var longitudeStr: String? = null
    private var addressStr: String? = null
    private var icon: Int = 0

    private var flipProgressDialog: FlipProgressDialog? = null

    companion object {
        private val TAG = "WeatherActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        ButterKnife.bind(this)

        flipProgressDialog = Utility.myDialog()

        flipProgressDialog!!.show(fragmentManager, "")

        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0)
            } else {
                val request = TencentLocationRequest.create()
                val locationManager = TencentLocationManager.getInstance(this)
                val error = locationManager.requestLocationUpdates(request, this)
            }
        }


    }

    private fun requestWeather(latitude: String, longitude: String) {

        val url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(SELECT%20woeid%20FROM%20geo.places%20WHERE%20text%3D%22($latitude%2C$longitude)%22)%20and%20u%3D'c'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
        HttpUtil.sendOkHttpRequest(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body().string()
                val weather = Utility.handleWeatherResponse(responseData)
                val channel = weather!!.query!!.results!!.channel
                val location = channel!!.location!!.city + "," + channel.location!!.region + "," + channel.location!!.country
                Log.d(TAG, location)
                runOnUiThread {
                    if (weather != null) {
                        showWeatherInfo(weather)
                    } else {
                        Log.d(TAG, "获取天气信息失败")
                    }
                }
            }
        })
    }

    private fun showWeatherInfo(weather: Weather?) {
        val channel = weather!!.query!!.results!!.channel
        val locationStr = channel!!.location!!.city + "," + channel.location!!.region + "," + channel.location!!.country
        val statusStr = channel.item!!.condition!!.text
        val degreeStr = channel.item!!.condition!!.temp
        val humidityStr = channel.atmosphere!!.humidity
        val speedStr = channel.wind!!.speed
        val sunriseStr = channel.astronomy!!.sunrise
        val sunsetStr = channel.astronomy!!.sunset
        val chillStr = channel.wind!!.chill
        val directionStr = channel.wind!!.direction
        val pressureStr = channel.atmosphere!!.pressure
        val risingStr = channel.atmosphere!!.rising
        val visibilityStr = channel.atmosphere!!.visibility
        val code = channel.item!!.condition!!.code

        title = locationStr
        status!!.text = statusStr
        degree!!.text = degreeStr!! + " C"
        humidity!!.text = humidityStr!! + " %"
        speed!!.text = speedStr!! + " km/h"
        sunrise!!.text = sunriseStr
        sunset!!.text = sunsetStr
        forecast_layout!!.removeAllViews()
        chill!!.text = chillStr
        direction!!.text = directionStr
        speed_detail!!.text = speedStr + " km/h"
        humidity_detail!!.text = humidityStr + " %"
        pressure!!.text = pressureStr!! + " mb"
        rising!!.text = risingStr
        visibility!!.text = visibilityStr!! + " km"
        latitude!!.text = latitudeStr
        longitude!!.text = longitudeStr
        address!!.text = addressStr
        setWeatherIcon(code)

        my_weather_icon!!.setIconResource(getString(icon))
        my_weather_icon!!.setIconSize(80)
        my_weather_icon!!.setIconColor(Color.GRAY)

        for (forecast in channel.item!!.forecastList!!) {
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast_layout, false)
            val date = view.findViewById<TextView>(R.id.date)
            val day = view.findViewById<TextView>(R.id.day)
            val statusForecast = view.findViewById<TextView>(R.id.status_forecast)
            val high = view.findViewById<TextView>(R.id.high)
            val low = view.findViewById<TextView>(R.id.low)
            val dateStr = forecast.date
            val dayStr = forecast.day
            val statusForecastStr = forecast.text
            val highStr = forecast.high
            val lowStr = forecast.low
            date.text = dateStr
            day.text = dayStr
            statusForecast.text = statusForecastStr
            high.text = highStr
            low.text = lowStr
            forecast_layout!!.addView(view)
        }

        flipProgressDialog!!.dismiss()
    }

    private fun setWeatherIcon(code: String?) {
        when (code) {
            "0" -> icon = R.string.wi_tornado
            "1" -> icon = R.string.wi_storm_showers
            "2" -> icon = R.string.wi_tornado
            "3" -> icon = R.string.wi_thunderstorm
            "4" -> icon = R.string.wi_thunderstorm
            "5" -> icon = R.string.wi_snow
            "6" -> icon = R.string.wi_rain_mix
            "7" -> icon = R.string.wi_rain_mix
            "8" -> icon = R.string.wi_sprinkle
            "9" -> icon = R.string.wi_sprinkle
            "10" -> icon = R.string.wi_hail
            "11" -> icon = R.string.wi_showers
            "12" -> icon = R.string.wi_showers
            "13" -> icon = R.string.wi_snow
            "14" -> icon = R.string.wi_storm_showers
            "15" -> icon = R.string.wi_snow
            "16" -> icon = R.string.wi_snow
            "17" -> icon = R.string.wi_hail
            "18" -> icon = R.string.wi_hail
            "19" -> icon = R.string.wi_cloudy_gusts
            "20" -> icon = R.string.wi_fog
            "21" -> icon = R.string.wi_fog
            "22" -> icon = R.string.wi_fog
            "23" -> icon = R.string.wi_cloudy_gusts
            "24" -> icon = R.string.wi_cloudy_windy
            "25" -> icon = R.string.wi_thermometer
            "26" -> icon = R.string.wi_cloudy
            "27" -> icon = R.string.wi_night_cloudy
            "28" -> icon = R.string.wi_day_cloudy
            "29" -> icon = R.string.wi_night_cloudy
            "30" -> icon = R.string.wi_day_cloudy
            "31" -> icon = R.string.wi_night_clear
            "32" -> icon = R.string.wi_day_sunny
            "33" -> icon = R.string.wi_night_clear
            "34" -> icon = R.string.wi_day_sunny_overcast
            "35" -> icon = R.string.wi_hail
            "36" -> icon = R.string.wi_day_sunny
            "37" -> icon = R.string.wi_thunderstorm
            "38" -> icon = R.string.wi_thunderstorm
            "39" -> icon = R.string.wi_thunderstorm
            "40" -> icon = R.string.wi_storm_showers
            "41" -> icon = R.string.wi_snow
            "42" -> icon = R.string.wi_snow
            "43" -> icon = R.string.wi_snow
            "44" -> icon = R.string.wi_cloudy
            "45" -> icon = R.string.wi_lightning
            "46" -> icon = R.string.wi_snow
            "47" -> icon = R.string.wi_thunderstorm
            "3200" -> icon = R.string.wi_cloud
            else -> icon = R.string.wi_cloud
        }

    }

    override fun onLocationChanged(tencentLocation: TencentLocation, error: Int, reason: String) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            Log.d(TAG, tencentLocation.address)
            latitudeStr = tencentLocation.latitude.toString()
            longitudeStr = tencentLocation.longitude.toString()
            addressStr = tencentLocation.address
            requestWeather(latitudeStr!!, longitudeStr!!)
            val locationManager = TencentLocationManager.getInstance(this)
            locationManager.removeUpdates(this)
        } else {
            // 定位失败
        }
    }

    override fun onStatusUpdate(s: String, i: Int, s1: String) {

    }
}
