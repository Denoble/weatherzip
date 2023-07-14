package com.example.weatherzip.ui

import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherzip.ServiceStatus
import com.example.weatherzip.model.location.LocationAddress
import com.example.weatherzip.model.location.Result
import com.example.weatherzip.model.weather.Current
import com.example.weatherzip.model.weather.Temp
import com.example.weatherzip.repository.network.LocationApi
import com.example.weatherzip.repository.network.weatherApi.weatherRetrofitServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.example.weatherzip.model.nearby.Result as NearbyResult

@HiltViewModel
class LocationViewModel @Inject constructor(
    @ApplicationContext app: Context
) : ViewModel() {
    private val _locationAddress = MutableLiveData<LocationAddress>()
    val locationAddress: LiveData<LocationAddress> = _locationAddress
    private val _currentWeather = MutableLiveData<Current>()
    val currentWeather: LiveData<Current> = _currentWeather
    private val _serviceStatusMessage = MutableLiveData<String>()
    private val _serviceStatus = MutableLiveData<ServiceStatus>()
    val serviceStatusMessage: LiveData<String> = _serviceStatusMessage
    val serviceStatus: LiveData<ServiceStatus> = _serviceStatus
    private val _location: MediatorLiveData<Location> = MediatorLiveData<Location>()
    val location: LiveData<Location> = _location
    private val _zipCode = MutableLiveData<Triple<String, Double, Double>>()
    val zipCode: LiveData<Triple<String, Double, Double>> = _zipCode
    private val _nearByzipCodes = MutableLiveData<HashMap<String, Pair<Double, Double>>>()
    val nearByZipCodes: LiveData<HashMap<String, Pair<Double, Double>>> = _nearByzipCodes
    lateinit var viewModelJob: Job
    private val _isNearByZipCode = MutableLiveData<Boolean>()
    val isNearByZipCode: LiveData<Boolean> = _isNearByZipCode
    val map = HashMap<String, Pair<Double, Double>>()
    private val _weatherIcon = MutableLiveData<String>()
    val weatherIcon: LiveData<String> = _weatherIcon
    private val _temperature = MutableLiveData<Double>()
    val temperature: LiveData<Double> = _temperature

    fun requestLocationServices(
        lat: Double, lon: Double,
        apiKey: String, isNearby: Boolean
    ) {
        val latlng = lat.toString() + "," + lon.toString()
        viewModelJob = Job()
        val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
        coroutineScope.launch {
            try {
                _serviceStatus.value = ServiceStatus.LOADING
                Log.i("RequestLocationServices", ServiceStatus.LOADING.toString())
                val deferedLocationServices = LocationApi
                    .locationRetrofitServices.getaddress(
                        latlng, "",
                        "", apiKey
                    )
                deferedLocationServices.await().let {
                    _locationAddress.value = it
                    getCurrentZipCode(it.results[0], isNearby)
                }
                _serviceStatus.value = ServiceStatus.DONE
                _serviceStatusMessage.value = "Success !"
                Log.i("LOCATION SUCCESS ", locationAddress.value.toString())
            } catch (e: Exception) {
                _serviceStatus.value = ServiceStatus.ERROR
                _serviceStatusMessage.value = e.message ?: ""
                Log.i("LOCATION SERVICES -> ", e.stackTraceToString())
            }

        }
    }

    fun getCurrentWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelJob = Job()
        val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
        coroutineScope.launch {
            try {
                _serviceStatus.value = ServiceStatus.LOADING
                val defereWeatherService = weatherRetrofitServices.getCurrentWeather(
                    lat,
                    lon, apiKey
                )
                val result = defereWeatherService.await()
                _currentWeather.value = result.current
                _temperature.value = result.current.temp
                _weatherIcon.value = result.current.weather[0].icon
                Log.i(" WEATHER !!", "${result.current.temp}" +
                        " ${result.current.weather[0].icon}")
                Log.i(" WEATHER !!", result.toString())


            } catch (e: Exception) {
                _serviceStatus.value = ServiceStatus.ERROR
                _serviceStatusMessage.value = e.message ?: ""
                Log.i("WEATHER SERVICES -> ", e.stackTraceToString())
            }
        }
    }

    fun getCurrentZipCode(
        result: Result,
        isNearby: Boolean
    ): HashMap<String, Pair<Double, Double>> {
        val location = result.geometry.location
        result.addressComponents.forEach {
            if (it.types[0] == "postal_code") {
                if (isNearby) {
                    map.put(it.longName, Pair(location.lat, location.lng))
                } else {
                    _zipCode.value = Triple(it.longName, location.lat, location.lng)
                }

            }
        }
        return map
    }

    fun getZipCodes(results: List<NearbyResult>, apiKey: String) {
        for (i in 1 until results.size) {
            val location = results[i].geometry.location
            requestLocationServices(location.lat, location.lng, apiKey, true)
        }
    }

    fun getNearByPlace(lat: Double, lon: Double, apiKey: String) {
        val latlng = lat.toString() + "," + lon.toString()
        viewModelJob = Job()
        val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
        coroutineScope.launch {
            try {
                _serviceStatus.value = ServiceStatus.LOADING
                Log.i("NearbyServices", ServiceStatus.LOADING.toString())
                val deferedLocationServices = LocationApi
                    .locationRetrofitServices.getNearbyPlaces(location = latlng, key = apiKey)
                val nearby = deferedLocationServices.await()
                _serviceStatus.value = ServiceStatus.DONE
                _serviceStatusMessage.value = "Success !"
                Log.i("NEARBY SUCCESS ", nearby.toString())
                async {
                    getZipCodes(nearby.results, apiKey)
                }.await()
                _nearByzipCodes.value = map
            } catch (e: Exception) {
                _serviceStatus.value = ServiceStatus.ERROR
                _serviceStatusMessage.value = e.message ?: ""
                Log.i("NEARBY SERVICES -> ", e.stackTraceToString())
            }

        }
    }

    fun addZipCode(zip: String, lat: Double, lon: Double) {
        _nearByzipCodes.value?.put(zip, Pair(lat, lon))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        return current.toString()
    }
    fun getLatLon(zip:String):Pair<Double,Double>{
       return nearByZipCodes.value?.get(zip)?:Pair(Double.NaN,Double.NaN)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    companion object{
        val LOCALITY = listOf("locality", "political")
    }

}