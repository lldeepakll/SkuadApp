package com.application.skuadassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.application.skuadassignment.common.AppCommon
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mLocationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLocationRequest = LocationRequest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    //////////////////////////////////
                    // Update UI with location data //
                    //////////////////////////////////

                    Log.e("Lat", location.latitude.toString())
                    Log.e("Lng", location.longitude.toString())

                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    intent.putExtra("LAT_LNG","${location.latitude},${location.longitude}")
                    startActivity(intent)
                    finish()

                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }

        nearby.setOnClickListener {

           AppCommon.callDexterForLocation(this, mLocationRequest!!, locationCallback, fusedLocationClient)


        }

    }
}