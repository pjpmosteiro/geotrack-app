package com.redp.geotrack;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.*;

import static android.Manifest.permission.*;

public class MainActivity extends AppCompatActivity {


    private ArrayList<String> permissionsToRequest;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    SendMessage sendMessage = new SendMessage();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(INTERNET);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        Button btn = (Button) findViewById(R.id.btn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(MainActivity.this);
                FIleManager fileManager = new FIleManager();


                if (locationTrack.canGetLocation()) {


                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();

//                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Latitud:" + latitude + "\nLongitud:" + longitude, Toast.LENGTH_SHORT).show();
                    fileManager.writeToFile("Latitud: " + latitude + "\nLongitud:" + longitude, MainActivity.this);


                } else {

                    locationTrack.showSettingsAlert();
                }

            }
        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        EditText textInput   = (EditText)findViewById(R.id.text01);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strictModeDisabler();

                locationTrack = new LocationTrack(MainActivity.this);
                double longitude = locationTrack.getLongitude();
                double latitude = locationTrack.getLatitude();
                String token = null;
                //Request the token URL
                GetToken getToken = new GetToken();
                try {
                    token = getToken.getToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Enviando datos", Toast.LENGTH_SHORT).show();


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

                    String url = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                    sendMessage.sendMessage(Double.toString(longitude), Double.toString(latitude), url, token);

                    if (textInput.getText().toString() != ""){
                        sendMessage.sendMessageWithoutCoordinates(textInput.getText().toString(), url, token);
                    }

            }

            private void strictModeDisabler() {
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
            }
        });


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




    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }
}
