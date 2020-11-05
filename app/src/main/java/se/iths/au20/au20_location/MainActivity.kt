package se.iths.au20.au20_location

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION = 1
    lateinit var locationProvider: FusedLocationProviderClient
    var locationRequest : LocationRequest? = null
    lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        // alternativt sätt att skapa vår locationcallback
        // locationCallback = MyCustomLocationCallBack()

        // skapa en ny anonym klass som ärver av LocationCallback och skapa ett object av denna anonyma class
        locationCallback = object : LocationCallback() {

            // kommer att köras när vi får en uppdaterad position ( i vårt fall cirka varannan sekund)
            override fun onLocationResult(locationResult: LocationResult) {
                for(location in locationResult.locations ) {
                    Log.d("!!!", "lat: ${location.latitude} lng: ${location.longitude}" )
                }
            }
        }

        if( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("!!!", "no permission")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
             REQUEST_LOCATION)

        } else {
            locationProvider.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude
                    Log.d("!!!", "lat: $lat, lng: $lng")
                }
            }

        }

        locationRequest = creatLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    fun startLocationUpdates() {
        if( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    fun stopLocationUpdates() {
        locationProvider.removeLocationUpdates(locationCallback)
    }


    fun creatLocationRequest()  =
        LocationRequest.create().apply{
            interval = 2000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


/* Alternativt sätt att skriva samma sak
    fun creatLocationRequest() : LocationRequest{
        val request = LocationRequest.create()

        request.interval = 2000
        request.fastestInterval = 1000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        return request
    }
    */



    // körs när användaren har tryckt på antingen ja eller nej till att ge tillåtelse att använda GPS
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_LOCATION ) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Log.d("!!!", "Permission granted")
                startLocationUpdates()
            } else {
                Log.d("!!!","Permission denied")
            }
        }

    }
}

//1. locationmanager

//2. fusedlocationprovider - google play services