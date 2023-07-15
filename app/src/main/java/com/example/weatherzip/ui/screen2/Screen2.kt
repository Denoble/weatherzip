package com.example.weatherzip.ui.screen2

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherzip.R
import com.example.weatherzip.databinding.FragmentScreen2Binding
import com.example.weatherzip.ui.LocationViewModel
import com.example.weatherzip.ui.screen1.WEATHER_API_KEY


class Screen2 : Fragment() {
    lateinit var binding: FragmentScreen2Binding
    lateinit var zipCode: String
    var tempReading = 0.0
    val viewModel: LocationViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreen2Binding.inflate(inflater, container, false)

        observeIcon()
        observeTemperature()
        observerTemperatureMetric()
        navigateToSettings()
        refresh()
        refreshOnclick()
        val view = binding.root
        return view
    }

    fun getWeatherCondition(zip: String) {
        val latLon = viewModel.getLatLon(zip)
        viewModel.getCurrentWeather(latLon.first, latLon.second, WEATHER_API_KEY)
    }
    fun observeIcon(){
        viewModel.weatherIcon.observe(viewLifecycleOwner,{
            val iconurl = "http://openweathermap.org/img/w/" + it+ ".png"
            bindImage(binding.icon,iconurl)
        })
    }
   @RequiresApi(Build.VERSION_CODES.O)
   fun observeTemperature(){
       viewModel.temperature.observe(viewLifecycleOwner,{
           tempReading = it
           binding.time.text = viewModel.getCurrentTime()
           observerTemperatureMetric()
       })

   }
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val glideImgUrl = it.toUri().buildUpon().scheme("https").build()
            Glide.with(imgView.context)
                .load(glideImgUrl).apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_baseline_broken_image_24)
                )
                .into(imgView)
        }
    }
    fun observerTemperatureMetric(){
        viewModel.tempMetric.observe(viewLifecycleOwner,{
            val view = binding.temp
                when(it.lowercase()){
                    "celcius"->{
                        val celciusValue =  tempReading-273.0
                        view.text = (celciusValue.toInt().toString())
                        view.setCompoundDrawablesWithIntrinsicBounds(0,0,
                            R.drawable.celcius_png,0)
                    }
                    "fahrenheit" ->{
                        val fahrenheitValue = ( ((tempReading -273.0)*1.8)  + 32)
                        view.text = fahrenheitValue.toInt().toString()
                        view.setCompoundDrawablesWithIntrinsicBounds(0,
                            0,R.drawable.fahrenheit
                            ,0)
                    }
                    else ->{

                    }
                }
        })
    }
    fun navigateToSettings(){
        binding.settings.setOnClickListener {
            findNavController().navigate(Screen2Directions.actionScreen2ToScreen3())
        }
    }
    fun refresh(){
        zipCode = Screen2Args.fromBundle(requireArguments()).zipCode
        val sb = StringBuilder()
        sb.append("Zipcode:")
        sb.append(zipCode)
        binding.city.text = sb.toString()
        getWeatherCondition(zipCode)
        observerTemperatureMetric()
    }
    fun refreshOnclick(){
        binding.refresh.setOnClickListener {
            refresh()
        }
    }
}