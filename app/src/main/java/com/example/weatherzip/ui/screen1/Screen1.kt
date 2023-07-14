package com.example.weatherzip.ui.screen1

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherzip.BuildConfig
import com.example.weatherzip.checkLocationPermission
import com.example.weatherzip.databinding.FragmentScreen1Binding
import com.example.weatherzip.isFineLocationPermissionGranted
import com.example.weatherzip.ui.LocationViewModel
const val LOCATION_API_KEY = BuildConfig.REVERSE_GEOCODING_API_KEY
const val WEATHER_API_KEY = BuildConfig.OPENWEATHERMAP_KEY
class Screen1 : Fragment(), LocationListener {
    var lat = 0.0
    var lon = 0.0
    var latlng = ""
    lateinit var locationManager: LocationManager
    val viewModel:LocationViewModel by activityViewModels()
   lateinit var binding:FragmentScreen1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreen1Binding.inflate(inflater)
        val view = binding.root
        observeZipCode()
        observeNearbyZipCodes()
        addZipCodeClickListener()
        return view
    }
    override fun onStart() {
        super.onStart()
        getLocation()
    }
    override fun onLocationChanged(location: Location) {
         lat = location.latitude
         lon = location.longitude
        viewModel.requestLocationServices(lat,lon, LOCATION_API_KEY,false)
        viewModel.getNearByPlace(lat,lon, LOCATION_API_KEY)
        Log.i("MainActivity", " WE GOT $lat  $lon   AS LAT AND LNG")
        if (isFineLocationPermissionGranted(requireActivity())) {
            //locationViewModel.requestLocationServices(queryString, LOCATION_API_KEY)
        } else {
            com.example.weatherzip.requestPermissions(requireActivity())
        }
    }
    private fun observeNearbyZipCodes(){
        viewModel.nearByZipCodes .observe(viewLifecycleOwner,{
            if(it.entries.size >0){
                binding.recyclerView.visibility =View.VISIBLE
                binding.imageView.visibility= View.GONE
                binding.recyclerView.adapter =
                    ListAdapter(it.keys.toList(),this.requireActivity(),
                    ListAdapter.OnClickListener{
                        findNavController().navigate(Screen1Directions.actionScreen1ToScreen2(it))
                    })
            }
        })
    }
    private fun observeZipCode(){
        viewModel.zipCode.observe(viewLifecycleOwner,{
            val sb = StringBuilder()
            sb.append("Your zipcode is: ")
            sb.append(it.first)
            Log.i("ZIPCode",it.toString())
            binding.defaultZip.text =  sb.toString()
        })
    }
    private fun getLocation() {
        locationManager = requireActivity().application
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission(requireActivity())) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000, 5f, this
            )
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 5f, this
            )
        } else {
            com.example.weatherzip.requestPermissions(requireActivity())
        }
    }
    fun addZipCodeClickListener(){
        binding.button.setOnClickListener {
            val zip = binding.editZip.text.toString()
            val  lat = binding.editLat.text.toString().trim()
            val lon = binding.editLon.text.toString().trim()
            if(zip.isNotEmpty() && lat.isNotEmpty() && lon.isNotEmpty()){
                viewModel.addZipCode(zip,lat.toDouble(),lon.toDouble())
                binding.editZip.setText("")
                binding.editLat.setText("")
                binding.editLon.setText("")
                observeNearbyZipCodes()
            }
        }
    }
}