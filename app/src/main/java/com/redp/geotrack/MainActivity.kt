package com.redp.geotrack

import android.Manifest.permission
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    var locationTrack: LocationTrack? = null
    var sendMessage: SendMessage = SendMessage()
    private var permissionsToRequest: ArrayList<String>? = null

    private val HTTP_OK = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get the permissions we need
        permissions.add(permission.ACCESS_FINE_LOCATION)
        permissions.add(permission.ACCESS_COARSE_LOCATION)
        permissions.add(permission.INTERNET)

        permissionsToRequest = findUnAskedPermissions(permissions)


        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!!.size > 0) requestPermissions(
                permissionsToRequest!!.toTypedArray<String>(),
                ALL_PERMISSIONS_RESULT
            )
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.nav_home) {
                Toast.makeText(
                    this@MainActivity,
                    "Home seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnNavigationItemSelectedListener true
            } else if (item.itemId == R.id.nav_location) {
                Toast.makeText(
                    this@MainActivity,
                    "Location seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnNavigationItemSelectedListener true
            } else if (item.itemId == R.id.nav_settings) {
                Toast.makeText(
                    this@MainActivity,
                    "Settings seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnNavigationItemSelectedListener true
            }
            false
        }


        val btn = findViewById<Button>(R.id.btn)


        btn.setOnClickListener { view: View? ->
            locationTrack = LocationTrack(this@MainActivity)
            val fileManager = FIleManager()
            if (locationTrack!!.canGetLocation()) {
                val longitude = locationTrack!!.getLongitude()
                val latitude = locationTrack!!.getLatitude()

                // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                Toast.makeText(
                    applicationContext,
                    "Latitud:$latitude\nLongitud:$longitude",
                    Toast.LENGTH_SHORT
                ).show()
                fileManager.writeToFile(
                    "Latitud: $latitude\nLongitud:$longitude",
                    this@MainActivity
                )
            } else {
                locationTrack!!.showSettingsAlert()
            }
        }

        val btn2 = findViewById<Button>(R.id.btn2)
        val textInput = findViewById<EditText>(R.id.text01)
        btn2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                strictModeDisabler()
                locationTrack = LocationTrack(this@MainActivity)
                val longitude = locationTrack!!.getLongitude()
                val latitude = locationTrack!!.getLatitude()
                //Request the token URL
                val getToken = GetToken()
                val token: String = getToken.token ?: throw IllegalStateException("Token no puede ser nulo")
                Toast.makeText(applicationContext, "Enviando datos", Toast.LENGTH_SHORT).show()


                //TODO: Generar fichero con los puntos de trayectoria

                //Codigo para enviar ubicacion cada 5 segundos
//                try {
//                    while (true) {
//                        String url = "https://www.google.com/maps/search/?api=1&query="+Double.toString(latitude)+","+Double.toString(longitude);
//                    sendMessage.sendMessage(Double.toString(longitude), Double.toString(latitude), url, token);
//                        Thread.sleep(5 * 1000);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


                //Transferimos latitud, longitud y url de maps al sistema
//                while (loopController == 1) {
//                    startLoop(longitude, latitude, token);
//                }

//                if (loopController != 1) {
//                    String url = "https://www.google.com/maps/search/?api=1&query=" + Double.toString(latitude) + "," + Double.toString(longitude);
//                    sendMessage.sendMessage(Double.toString(longitude), Double.toString(latitude), url, token);
//                }
                val url =
                    "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                sendMessage.sendMessage(
                    applicationContext,
                    longitude.toString(),
                    latitude.toString(),
                    url,
                    token
                )

                if (textInput.text.toString() !== "") {
                    sendMessage.sendMessageWithoutCoordinates(
                        applicationContext,
                        textInput.text.toString(),
                        url,
                        token
                    )
                }
            }

            fun strictModeDisabler() {
                if (Build.VERSION.SDK_INT > 9) {
                    val policy = ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)
                }
            }
        })


        //
    }


    //        try {
    //            while (true) {
    //                String url = "https://www.google.com/maps/search/?api=1&query=" + Double.toString(latitude) + "," + Double.toString(longitude);
    //                sendMessage.sendMessage(Double.toString(longitude), Double.toString(latitude), url, token);
    //                Thread.sleep(5 * 1000);
    //            }
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        }
    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel(
                                "These permissions are mandatory for the application. Please allow access."
                            ) { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(
                                        permissionsRejected.toTypedArray<String>(),
                                        ALL_PERMISSIONS_RESULT
                                    )
                                }
                            }
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity).setMessage(message)
            .setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTrack!!.stopListener()
    }

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 101
    }
}
