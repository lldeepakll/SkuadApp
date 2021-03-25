package com.application.skuadassignment.common


import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class AppCommon {

    companion object {

        fun callDexterForLocation(
            activity: AppCompatActivity,
            mLocationRequest: LocationRequest,
            locationCallback: LocationCallback,
            fusedLocationClient: FusedLocationProviderClient
        ) {

            Dexter.withActivity(activity)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                        when {
                            report.isAnyPermissionPermanentlyDenied -> {

                                showSettingsDialog(activity)
                            }
                            report.areAllPermissionsGranted() -> {


                                Log.e("Here", "Dexter")
                                openLocationSettings(
                                    mLocationRequest,
                                    locationCallback,
                                    fusedLocationClient,
                                    activity
                                )


                            }
                            else -> showSettingsDialog(activity)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }

        private fun openLocationSettings(
            mLocationRequest: LocationRequest,
            locationCallback: LocationCallback,
            fusedLocationClient: FusedLocationProviderClient,
            activity: AppCompatActivity
        ) {

//            mLocationRequest = LocationRequest()
            mLocationRequest.interval = 10
            mLocationRequest.smallestDisplacement = 10f
            mLocationRequest.fastestInterval = 10
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)

            val task =
                LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())

            task.addOnCompleteListener {

                try {

                    val response: LocationSettingsResponse =
                        task.getResult(ApiException::class.java)!!



                    Log.e("RES", response.toString())
                    startLocationUpdates(
                        mLocationRequest,
                        locationCallback,
                        fusedLocationClient,
                        activity
                    )

                } catch (exception: ApiException) {

                    when (exception.statusCode) {

                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                val resolvable = exception as ResolvableApiException
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(activity, 101)
                            } catch (e: IntentSender.SendIntentException) {
                                // Ignore the error.
                            } catch (e: ClassCastException) {
                                // Ignore, should be an impossible error.
                            }

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                        }

                    }

                }

            }

        }


        private fun startLocationUpdates(
            mLocationRequest: LocationRequest,
            locationCallback: LocationCallback,
            fusedLocationClient: FusedLocationProviderClient,
            activity: AppCompatActivity
        ) {

            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    activity,
                    "You need to enable permissions to display location !",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            fusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }


        private fun showSettingsDialog(mActivity: AppCompatActivity) {
            AlertDialog.Builder(mActivity).apply {
                setTitle("Need Permissions")
                setCancelable(false)
                setMessage("This app needs permission to use this feature. You can grant them in app settings.")
                setPositiveButton("GOTO SETTINGS") { dialog, _ ->
                    dialog.dismiss()
                    openSettings(mActivity)
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }

        private fun openSettings(activity: AppCompatActivity) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivityForResult(intent, 1010)
        }


    }
}