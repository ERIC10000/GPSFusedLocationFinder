package com.example.myapplication
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.pm.PackageManager
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var btnGetLocation: Button
//    private lateinit var etLocation: EditText
//
//    private val LOCATION_PERMISSION_REQUEST = 100
//    private val MIN_TIME_BETWEEN_UPDATES: Long = 5000 // Minimum time interval for location updates (in milliseconds)
//    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // Minimum distance for location updates (in meters)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        btnGetLocation = findViewById(R.id.btnGetLocation)
//        etLocation = findViewById(R.id.etLocation)
//
//        btnGetLocation.setOnClickListener {
//            if (checkLocationPermission()) {
//                getLocation()
//            } else {
//                requestLocationPermission()
//            }
//        }
//    }
//
//    private fun checkLocationPermission(): Boolean {
//        val coarseLocationPermission =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//        val fineLocationPermission =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED && fineLocationPermission == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestLocationPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            LOCATION_PERMISSION_REQUEST
//        )
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun getLocation() {
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        // Check if the GPS provider is enabled
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                MIN_TIME_BETWEEN_UPDATES,
//                MIN_DISTANCE_CHANGE_FOR_UPDATES,
//                locationListener
//            )
//        } else {
//            etLocation.setText("GPS is not enabled")
//        }
//    }
//
//    private val locationListener: LocationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            val latitude = location.latitude
//            val longitude = location.longitude
//
//            etLocation.setText("Latitude: $latitude, Longitude: $longitude")
//        }
//
//        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//
//        override fun onProviderEnabled(provider: String) {}
//
//        override fun onProviderDisabled(provider: String) {}
//    }
//
//    // Handle the result of the permission request, if required
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            LOCATION_PERMISSION_REQUEST -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getLocation()
//                } else {
//                    etLocation.setText("Permission denied. Cannot get GPS coordinates.")
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Stop listening for location updates when the activity is destroyed
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        locationManager.removeUpdates(locationListener)
//    }
//}






import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var btnGetLocation: Button
    private lateinit var etLocation: EditText
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetLocation = findViewById(R.id.btnGetLocation)
        etLocation = findViewById(R.id.etLocation)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnGetLocation.setOnClickListener {
            if (checkLocationPermission()) {
                getLocation()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        val coarseLocationPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fineLocationPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED && fineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST
        )
    }

    private fun getLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        etLocation.setText("Latitude: $latitude, Longitude: $longitude")
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse("geo: $latitude, $longitude z=17"))
                        startActivity(i)
                    } ?: run {
                        etLocation.setText("Location not available")
                    }
                }
                .addOnFailureListener { e ->
                    etLocation.setText("Failed to get location: ${e.message}")
                }
        } else {
            etLocation.setText("Location permission not granted")
        }
    }

    // Handle the result of the permission request, if required
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    etLocation.setText("Permission denied. Cannot get GPS coordinates.")
                }
            }
        }
    }
}
