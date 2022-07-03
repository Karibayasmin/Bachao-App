package com.singularitybd.bachaoapp.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.model.EventSpeechRecognise
import com.singularitybd.bachaoapp.model.GpsEvent
import com.singularitybd.bachaoapp.utils.AppConstants
import kotlinx.android.synthetic.main.activity_registration.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

open class BaseActivity : AppCompatActivity() {

    protected lateinit var progressDialog: ProgressDialog

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = AppConstants.LOCATION_PERMISSION_CODE

    var latLong : String? = ""
    var cityName: String? = ""
    var stateName: String? = ""
    var countryName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        getLocation()

        progressDialog = ProgressDialog(this@BaseActivity, R.style.AppCompatAlertDialogStyle)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.please_wait))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun gpsEvent(event: GpsEvent) {

    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(progressDialog !=null && progressDialog.isShowing) progressDialog.cancel()
    }

    override fun onPause() {
        super.onPause()
        if(progressDialog!=null && progressDialog.isShowing) progressDialog.cancel()

    }

    override fun onResume() {
        super.onResume()

        getLocation()
    }

    private fun getLocation() {
        Log.e("location","enter here 1")
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        try {
            // Request location updates
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch(ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.e("location","enter here 2")

            latLong = ("" + location.longitude + "," + location.latitude+ "")

            val geocoder = Geocoder(this@BaseActivity, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            cityName = addresses[0].getAddressLine(0)
            stateName = addresses[0].getAddressLine(1)
            countryName = addresses[0].getAddressLine(2)

            Log.e("location","enter here 3 $latLong $cityName")
            EventBus.getDefault().post(GpsEvent(true, latLong, cityName))


        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}