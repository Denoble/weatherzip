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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherzip.R
import com.example.weatherzip.databinding.FragmentScreen2Binding
import com.example.weatherzip.ui.LocationViewModel
import com.example.weatherzip.ui.screen1.WEATHER_API_KEY


class Screen2 : Fragment() {
    lateinit var binding: FragmentScreen2Binding
    lateinit var zipCode: String
    val viewModel: LocationViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreen2Binding.inflate(inflater, container, false)
        zipCode = Screen2Args.fromBundle(requireArguments()).zipCode
        val sb = StringBuilder()
        sb.append("Zipcode:")
        sb.append(zipCode)
        binding.city.text = sb.toString()
        getWeatherCondition(zipCode)
        observeIcon()
        observeTemperature()
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
           binding.temp.text = it.toString()
           binding.time.text = viewModel.getCurrentTime()
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

}