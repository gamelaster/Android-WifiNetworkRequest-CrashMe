package sk.marekkraus.crashme

import android.Manifest
import android.content.Context
import android.net.*
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private var wifiName: String = ""
    private var wifiPassword: String = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val locationButton = findViewById<Button>(R.id.locationPermButton)
        locationButton.setOnClickListener {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
        }

        val crashMeButton = findViewById<Button>(R.id.crashMeButton)
        crashMeButton.setOnClickListener {
            wifiName = findViewById<EditText>(R.id.wifiNameEditText).text.toString()
            wifiPassword = findViewById<EditText>(R.id.wifiPasswordEditText).text.toString()
            crashMe()
        }
    }

    class  ConnectionNetworkCallback(private val activity: MainActivity) : ConnectivityManager.NetworkCallback() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            println("onAvailable")
            activity.crashMe()
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onUnavailable() {
            super.onUnavailable()
            println("onUnavailable")
            activity.crashMe()
            activity.connectivityManager.unregisterNetworkCallback(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun crashMe() {
        val builder = WifiNetworkSpecifier.Builder()
            .setSsid(wifiName)
            .setWpa2Passphrase(wifiPassword)

        val specifier = builder.build()
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()

        println("Trying to request network...")
        connectivityManager.requestNetwork(
            request,
            ConnectionNetworkCallback(this),
            10000
        )
    }
}