package com.nhm.distribution

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.telecom.TelecomManager.EXTRA_LOCATION
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.nhm.distribution.utils.callPermissionDialog
import com.nhm.distribution.utils.getMediaFilePathFor
import com.nhm.distribution.utils.showOptions
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File

class ABC : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    private val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

//        activityResultLauncher.launch(permissionList)

        callMediaPermissions()
//        requestQ()


//        locationRequest = LocationRequest.create().apply {
//
//        }
//
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
//
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                // Normally, you want to save a new location to a database. We are simplifying
//                // things a bit and just saving it as a local variable, as we only need it again
//                // if a Notification is created (when the user navigates away from app).
//                currentLocation = locationResult.lastLocation
//
//            }
//        }
//
//
//        val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//        removeTask.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("TAG", "Location Callback removed.")
//                stopSelf()
//            } else {
//                Log.d("TAG", "Failed to remove Location Callback.")
//            }
//        }
//    }


    }


    private fun callMediaPermissions() {
        activityResultLauncher.launch(permissionList)
    }

    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                openCamera()
            } else {
                callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun openCamera() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
        }
    }


    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
            lifecycleScope.launch {

            }

        }




//    private fun requestQ() {
//        requestMultiplePermissions.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.CAMERA
//            )
//        )
//    }
//
//
//    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
//            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
//            permissions[Manifest.permission.CAMERA] == true) {
//            Log.e("DEBUG", "${permissions.size} = ${permissions.values}")
//            val longerThan4 = permissions.filter { it.value == false }
//            Log.e("longerThan4", "longerThan4 "+longerThan4.size)
//
//            if(longerThan4.size == 0){
//                Log.e("longerThan4", "CLICK")
//            } else {
//                Log.e("longerThan4", "")
////                requestQ()
//
//            }
//        } else {
//            // Permission was denied. Display an error message.
//            Log.e("DEBUG", "Permission was denied")
//        }
//
////        permissions.entries.forEach {
////            Log.e("DEBUG", "${it.key} = ${it.value}")
////        }
////        if (isGranted.containsValue(false)) {
////            Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
////            multiplePermissionLauncher.launch(PERMISSIONS);
////        }
////        if (permissions) {
////
////        }
//    }

//    private val activityResultLauncher =
//        registerForActivityResult(RequestMultiplePermissions()) { permissions ->
//            if (!permissions.values.contains(false)) {
////                contactsProvider.addMockContactList()
//                permissionState.value = true
//                Log.d(TAG, "activityResultLauncher")
//            }
//        }
//
//    val activityResultLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted ->
//            Log.e("TAG", "activityResultLauncher")
//            // Handle Permission granted/rejected
//            if (isGranted) {
//                // Permission is granted
//            } else {
//                // Permission is denied
//            }
//        }

}