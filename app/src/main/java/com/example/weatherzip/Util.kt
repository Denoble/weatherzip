package com.example.weatherzip

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

val LOCATION_PERMISSION_ID = 42

enum class ServiceStatus { SENDING, LOADING, ERROR, DONE, IDLE }
enum class ConnectivityMode {
    NONE, WIFI, MOBILE, OTHER, MAYBE
}

/**
 * Determines the size of the device
 * @param context the activity context
 * */
fun isLargerScreen(context: Context): Boolean {
    return ((context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
}

fun TextInputEditText.isNotNullOrEmpty(errorString: String): Boolean {
    val textInputLayout = this.parent.parent as TextInputLayout
    textInputLayout.errorIconDrawable = null
    this.onChange { textInputLayout.error = null }

    return if (this.text.toString().trim().isEmpty()) {
        textInputLayout.error = errorString
        false
    } else {
        true
    }
}

fun EditText.isNotNullOrEmpty(errorString: String): Boolean {
    this.onChange { this.error = null }

    return if (this.text.toString().trim().isBlank()) {
        this.error = errorString
        false
    } else {
        true
    }
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun isFineLocationPermissionGranted(activity: Activity): Boolean {
    var permission = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        permission = ActivityCompat.checkSelfPermission(
            activity.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    return permission

}

fun checkConnectivity(context: Context): ConnectivityMode {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    cm.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getNetworkCapabilities(activeNetwork)?.run {
                return when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityMode.WIFI
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityMode.MOBILE
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectivityMode.OTHER
                    hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> ConnectivityMode.MAYBE
                    else -> ConnectivityMode.NONE
                }
            }
        } else {
            @Suppress("DEPRECATION") activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> ConnectivityMode.WIFI
                    ConnectivityManager.TYPE_MOBILE -> ConnectivityMode.MOBILE
                    ConnectivityManager.TYPE_ETHERNET -> ConnectivityMode.OTHER
                    ConnectivityManager.TYPE_BLUETOOTH -> ConnectivityMode.MAYBE
                    else -> ConnectivityMode.NONE
                }
            }
        }
    }
    return ConnectivityMode.NONE
}

fun isLocationEnabled(context: Context): Boolean {
    var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

fun checkLocationPermission(activity: Activity): Boolean {
    var result = true
    if (ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        &&ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        result = false
    }
    return result
}

fun requestPermissions(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        ), LOCATION_PERMISSION_ID
    )
    ActivityCompat.requestPermissions(
        activity, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ), LOCATION_PERMISSION_ID
    )
}

fun roundTo2DecimalPlaces(number: Double): Double {
    val decimalFormat = DecimalFormat("#.##")
    decimalFormat.roundingMode = RoundingMode.FLOOR
    return decimalFormat.format(number).toDouble()
}

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}