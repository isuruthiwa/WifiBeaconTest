package com.isuruthiwa.workplace.wifibeacontest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.maps.model.LatLng;
//import com.google.maps.android.SphericalUtil;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

//    private static final String FILE_NAME = "wifidata.csv";

    private WifiManager wifiManager;
    //    private ListView listView;
    //    private Button buttonScan;
    private TextView status;
    private String rsu;
    private TextView msg;
    private TextView rssi;
    private TextView timestamp;
    private TextView info;
    private TextView lonTxt;
    private TextView latTxt;
    private EditText editSpeed;
    private Button speedBtn;
    private TextView speedMsg;
    private Button button;
    private TextView txtDec;
    private ImageView signView;
    private TextView configTxt;
    private TextView dist;
    private Button start;
    private Button configSubmit, closeBtn, getBtn, resetBtn, sendLoc;
    private TextView signLoc;
    private TextView headtxt, timestamptxt, beacontimestampLbl;
    private TextView unixTimeTxt;
    private TextView distanceTxt;
    private TextView editHead;
    private Button addSign, removeSign;
    private Spinner sign_details;
    private TextView messageView;

    long unixTime;
    int distance;

    Location mLastLocation;                 //Last location variable

//    public LatLng preLocation =new LatLng(0.0, 0.0);

//    int i=0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    double latitude, longitude;
    public static final String TAG = MainActivity.class.getSimpleName();
    private String speed;
    Geocoder geocoder;
    String ssidpart;
    String bssidpart;
    double a = 6.7968796265647375;
    double b = 79.90121638336899;
    static final double _d2r = (Math.PI / 180D);
    String lat_new, lon_new;
    double beacontime, beaconhead;
    String signtype, signmessage;

    public long unixTimeNow;

    String[] alnum = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "1", "2", "3", "4", "5", "6", "7", "8"};

    ArrayList<String> obj = new ArrayList<String>(Arrays.asList(alnum));


    FileWriter fw;
    BufferedWriter bw;
    FileOutputStream fos;
    private Location loc;
    //    FileOutputStream fos ;
    private int t = 0;

    //Configuring the Sign
    Socket myAppSocket = null;
    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;
    public static String CMD = "0";
    Thread task;

    String prev_mac_val = "";
    long prev_timestamp_val = 0;

    //Decryption
    String decryptedString;

    String key1 = "2b7e151628aed2a6abf7158809cf4f3c"; //hexkey
    String iv1 = "000102030405060708090a0b0c0d0e0f"; //hexiv
    String key2 = "K34VFiiu0qar9xWICc9PPA==";//base64key
    String iv2 = "AAECAwQFBgcICQoLDA0ODw==";//base64iv

    Calendar calendar;

    Spinner dropdown;
    ArrayAdapter<String> sign_details_adapter;
    String[] sign_details_items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = findViewById(R.id.status);
        rssi = findViewById(R.id.rssi);
        msg = findViewById(R.id.msg);
        timestamp = findViewById(R.id.timeStampTxt);
        info = findViewById(R.id.headtxt);
        lonTxt = findViewById(R.id.lonTxt);
        latTxt = findViewById(R.id.latTxt);
        editSpeed = findViewById(R.id.editSpeed);
        speedBtn = findViewById(R.id.speedBtn);
        dist = findViewById(R.id.dist);
        button = findViewById(R.id.button);
        start = findViewById(R.id.start);
        txtDec = findViewById(R.id.txtDec);
        signView = findViewById(R.id.signView);
        speedMsg = findViewById(R.id.speedMsg);
        geocoder = new Geocoder(this, Locale.getDefault());
        configTxt = findViewById(R.id.configTxt);
        configSubmit = findViewById(R.id.configSubmit);
        //editMsg = findViewById(R.id.editMsg1);
        closeBtn = findViewById(R.id.closeBtn);
        getBtn = findViewById(R.id.getBtn);
        resetBtn = findViewById(R.id.resetBtn);
        sendLoc = findViewById(R.id.sendLoc);
        signLoc = findViewById(R.id.signLoc);
        headtxt = findViewById(R.id.headtxt);
        timestamptxt = findViewById(R.id.timestamp);
        unixTimeTxt = findViewById(R.id.unixTime);
        distanceTxt = findViewById(R.id.distance);
        beacontimestampLbl =findViewById(R.id.timestampLbl);
        editHead = findViewById(R.id.editHead);
        messageView = findViewById(R.id.messageView);
        sign_details =findViewById(R.id.sign_details);




        dropdown = findViewById(R.id.sign_type);
        String[] items = new String[]{"T Junction", "Four Way Junction", "Traffic Light on Straight Road","Railway Gate","Emergency Sign"};
        ArrayAdapter<String> signlistadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(signlistadapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                signtype = parent.getItemAtPosition(position).toString();
                Log.d(TAG, signtype);
//                editMsg.setText(signtype);

                if(signtype.equals("Emergency Sign")){
                    sign_details_items = new String[]{"Lane Merge", "Road Flood", "Road Fire","Road Block","Oil Leak"};
                    sign_details_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, sign_details_items);
                    sign_details.setAdapter(sign_details_adapter);
                }
                else if(signtype.equals("T Junction")){
                    sign_details_items = new String[]{"T1", "T2", "T3","T4","T5","T6"};
                    sign_details_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, sign_details_items);
                    sign_details.setAdapter(sign_details_adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        String[] sign_details_items = new String[]{"Lane Merge", "Road Flood", "Road Fire","Road Block","Emergency Sign"};
//        ArrayAdapter<String> sign_details_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sign_details_items);
//        sign_details.setAdapter(sign_details_adapter);
        sign_details.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //signtype = parent.getItemAtPosition(position).toString();
               // Log.d(TAG, signtype);
//                editMsg.setText(signtype);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //AbstractList permissions = null;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = latitude;
                b = longitude;
            }
        });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double Lat = location.getLatitude();
                double Lon = location.getLongitude();
                double R = 6371e3;
                double deltaLat = (Lat - a) * _d2r;
                double deltaLon = (Lon - b) * _d2r;

                double c = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                        Math.cos(a) * Math.cos(Lat) *
                                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
                double e = 2 * Math.atan2(Math.sqrt(c), Math.sqrt(1 - c));

                double d = R * e;
                mLastLocation = location;

                latitude = location.getLatitude();

                longitude = location.getLongitude();
                String l_latitude = new DecimalFormat("##.#######").format(latitude);
                String l_longitude = new DecimalFormat("##.#######").format(longitude);
                System.out.println("1 : " + l_latitude);
                System.out.println("2 : " + l_longitude);

                String[] l_latitude_ar = l_latitude.split("\\.");
                String[] l_longitude_ar = l_longitude.split("\\.");

                long lat_i = Long.parseLong(l_latitude_ar[0]);
                long lat_f = Long.parseLong(l_latitude_ar[1]);
                long lon_i = Long.parseLong(l_longitude_ar[0]);
                long lon_f = Long.parseLong(l_longitude_ar[1]);

                double lat_new_a = lat_i / 60;
                double lat_new_b = lat_i % 60;
                double lat_new_c = lat_f / (Math.pow(60, 3));
                double lat_new_d = (lat_f % (Math.pow(60, 3))) / (Math.pow(60, 2));
                double lat_new_e = ((lat_f % (Math.pow(60, 3))) % (Math.pow(60, 2))) / (60);
                double lat_new_f = lat_f % 60;

                lat_new = obj.get((int) lat_new_a) + obj.get((int) lat_new_b) + obj.get((int) lat_new_c) +
                        obj.get((int) lat_new_d) + obj.get((int) lat_new_e) + obj.get((int) lat_new_f);

                System.out.println("LAT: " + lat_new);

                double lon_new_a = lon_i / 60;
                double lon_new_b = lon_i % 60;
                double lon_new_c = lon_f / (Math.pow(60, 3));
                double lon_new_d = (lon_f % (Math.pow(60, 3))) / (Math.pow(60, 2));
                double lon_new_e = ((lon_f % (Math.pow(60, 3))) % (Math.pow(60, 2))) / (60);
                double lon_new_f = lon_f % 60;

                lon_new = obj.get((int) lon_new_a) + obj.get((int) lon_new_b) + obj.get((int) lon_new_c) +
                        obj.get((int) lon_new_d) + obj.get((int) lon_new_e) + obj.get((int) lon_new_f);

                System.out.println("LON: " + lon_new);

                latTxt.setText(l_latitude + "");
                lonTxt.setText(l_longitude + "");
                float[] distance = new float[1];
                Location.distanceBetween(Double.parseDouble(l_latitude), Double.parseDouble(l_longitude), latitude, longitude, distance);

                dist.setText(distance[0] + " m");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
                return;
            }
        } else {
            configureButton();
        }


        File publicDcimDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String filename = "wifiData" + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) + ".csv";
        File newFile = new File(publicDcimDirPath, filename);

        try {
            fos = new FileOutputStream(newFile, true);
//            fos=openFileOutput(filename,MODE_APPEND);
            fw = new FileWriter(fos.getFD());
//            bw = new BufferedWriter(fw);
//            bw = new BufferedWriter(fw);

        } catch (IOException e) {
            e.printStackTrace();
        }


        speedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speed = editSpeed.getText().toString();
                String msg = "Recording for the speed of " + speed + " kmph";
                speedMsg.setText(msg);
            }
        });

        configTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    configureSign();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                configTxt.setText("Configuring the sign");
                configTxt.setBackgroundColor(Color.RED);
//                configSubmit.setActivated(true);
//                configSubmit.setClickable(true);
//                closeBtn.setClickable(true);
//                closeBtn.setActivated(true);
//                resetBtn.setClickable(true);
//                resetBtn.setActivated(true);
                configSubmit.setVisibility(View.VISIBLE);
                closeBtn.setVisibility(View.VISIBLE);
                resetBtn.setVisibility(View.VISIBLE);
                getBtn.setVisibility(View.VISIBLE);
                dropdown.setVisibility(View.VISIBLE);
                messageView.setVisibility(View.VISIBLE);
                sign_details.setVisibility(View.VISIBLE);
                findViewById(R.id.textView10).setVisibility(View.VISIBLE);
                sendLoc.setVisibility(View.VISIBLE);
                findViewById(R.id.textView15).setVisibility(View.VISIBLE);
                editHead.setVisibility(View.VISIBLE);

            }
        });

        configSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIPandPort();
                String rsu_message="";
                String signtypetosend="";
                if(signtype.equals("T Junction")){
                    signtypetosend="eU";
                }
                else if(signtype.equals("Four Way Junction")){
                    signtypetosend="FW";
                }
                else if(signtype.equals("Traffic Light Straight Road")){
                    signtypetosend="TL";
                }
                else if(signtype.equals("Railway Gate")){
                    signtypetosend="RG";
                }
                else if(signtype.equals("Emergency Sign")){
                    signtypetosend="PX";
                }

                signmessage = String.valueOf(sign_details.getSelectedItemId()+1);
                System.out.println(signmessage);
                int headint = Integer.parseInt(String.valueOf(editHead.getText()));
                int headint_1= headint/60;
                int headint_0= headint%60;



                if(lat_new==null){
                     rsu_message = "#" + "AGkyLZ" + "#" + "BTpj3T" + "#" + alnum[headint_1]+alnum[headint_0] + "#" + signtypetosend+"#"+signmessage;
                }else{
                     rsu_message = "#" + lat_new + "#" + lon_new + "#" + alnum[headint_1]+alnum[headint_0] + "#" + signtypetosend+"#"+signmessage;
                }
                CMD = rsu_message;
                Socket_AsyncTask cmd_submit = new Socket_AsyncTask();
                cmd_submit.execute();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIPandPort();
                CMD = "*Close";
                Socket_AsyncTask cmd_close = new Socket_AsyncTask();
                cmd_close.execute();

                configSubmit.setVisibility(View.GONE);
                closeBtn.setVisibility(View.GONE);
                resetBtn.setVisibility(View.GONE);
                getBtn.setVisibility(View.GONE);
                dropdown.setVisibility(View.GONE);
                messageView.setVisibility(View.GONE);
                sign_details.setVisibility(View.GONE);
                findViewById(R.id.textView10).setVisibility(View.GONE);
                sendLoc.setVisibility(View.GONE);
                findViewById(R.id.textView15).setVisibility(View.GONE);
                editHead.setVisibility(View.GONE);

                configTxt.setText("Click To Configure the sign");
                configTxt.setBackgroundColor(Color.parseColor("#4D6F8EF7"));

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                wifiManager.saveConfiguration();

                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                List<String> ThisList = new ArrayList<String>();
                int i = 0;

                for(WifiConfiguration k : list)
                {
                    if(k.SSID.contains("esp32-activers"))
                    {
                        int networkId = wifiManager.getConnectionInfo().getNetworkId();
                        ThisList.add(k.SSID);
                        i++;
                        wifiManager.removeNetwork(k.networkId);
                        wifiManager.saveConfiguration();
                    }
                }
//                task.notify();
            }
        });
//        closeBtn.setActivated(false);
//        closeBtn.setClickable(false);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Are you sure to RESET the RSU. All data will be erased.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getIPandPort();
                                CMD = "$Reset";
                                Socket_AsyncTask cmd_close = new Socket_AsyncTask();
                                cmd_close.execute();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
//                getIPandPort();
//                CMD = "Reset";
//                Socket_AsyncTask cmd_close = new Socket_AsyncTask();
//                cmd_close.execute();
            }
        });


        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIPandPort();
                CMD = "&Get";
                Async_Get cmd_get = new Async_Get();
                String g = "";
                String[] Signs = {""};
                String dialogData = "No data on RSU";
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                try {
                    g = cmd_get.execute().get();
                    Signs = g.split(",");
                    if (Signs.length > 0) {
                        dialogData = "";
                        for (int j = 0; j < Signs.length; j++) {
                            dialogData += Signs[j] + "\n";
                        }
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.setMessage("No data in the RSU");
                }

                System.out.println(dialogData);

                if (dialogData == "")
                    dialog.setMessage("No data in the RSU");
                else
                    dialog.setMessage(dialogData);

                dialog.setTitle("RSU");
                dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getApplicationContext(),"cancel is clicked",Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

            }
        });

        sendLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIPandPort();
                CMD = "@" + lat_new  +"@"+ lon_new;
                System.out.println(CMD);
                Socket_AsyncTask cmd_close = new Socket_AsyncTask();
                cmd_close.execute();
            }
        });
//        resetBtn.setActivated(false);
//        resetBtn.setClickable(false);
        configSubmit.setVisibility(View.GONE);
        closeBtn.setVisibility(View.GONE);
        resetBtn.setVisibility(View.GONE);
        getBtn.setVisibility(View.GONE);
        dropdown.setVisibility(View.GONE);
        messageView.setVisibility(View.GONE);
        sign_details.setVisibility(View.GONE);
        sendLoc.setVisibility(View.GONE);
        findViewById(R.id.textView10).setVisibility(View.GONE);
        editHead.setVisibility(View.GONE);
        findViewById(R.id.textView15).setVisibility(View.GONE);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
//        listView.setAdapter(adapter);
//        scanWifi();


        task = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    scanWifi();
                }
            }
        });
        task.start();
//        location = new Location(this);
        button.performClick();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }


    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
            }
        });

    }

    private void configureSign() throws InterruptedException {
//        task.wait();
        String networkSSID = "esp32-activers";
        String networkPass = "dummypassword";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.hiddenSSID = true;

        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        conf.preSharedKey = "\"" + networkPass + "\"";

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }


    private void scanWifi() {
        arrayList.clear();
        calendar = Calendar.getInstance();

        long hour=calendar.get(Calendar.HOUR_OF_DAY);
        long min=calendar.get(Calendar.MINUTE);
        long sec=calendar.get(Calendar.SECOND);

        unixTime= hour*3600+min*60+sec;

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        //        if(i==0) {
        //            Double heading = getHeading();
        //            System.out.println(heading);
//            preLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()); //Update current value to get next direction
//            i=10;
//        }
//        i--;
//        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
//        System.out.println(arrayList);
    }

    public void getIPandPort() {
        String iPandPort = "192.168.1.1:3333";
        Log.d("MYTEST", "IP String: " + iPandPort);
        String temp[] = iPandPort.split(":");
        wifiModuleIp = temp[0];
        wifiModulePort = Integer.valueOf(temp[1]);
        Log.d("MY TEST", "IP:" + wifiModuleIp);
        Log.d("MY TEST", "PORT:" + wifiModulePort);
    }

    public class Socket_AsyncTask extends AsyncTask<Void, Void, Void> {
        Socket socket;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                InetAddress inetAddress = InetAddress.getByName(MainActivity.wifiModuleIp);
                socket = new java.net.Socket(inetAddress, MainActivity.wifiModulePort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeBytes(CMD);
                dataOutputStream.close();
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(),"IO Error",Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public class Async_Get extends AsyncTask<Void, Void, String> {
        Socket socket;
        String responseLine = "";
        char b;
        byte[] msg;
        boolean EOF;
        StringBuilder t = new StringBuilder();

        @Override
        protected String doInBackground(Void... voids) {
            try {
                InetAddress inetAddress = InetAddress.getByName(MainActivity.wifiModuleIp);
                socket = new java.net.Socket(inetAddress, MainActivity.wifiModulePort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeBytes(CMD);


                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                while (!EOF) {
                    b = (char) dataInputStream.readByte();
//                    System.out.println(b);
                    if (b == '<') {
                        EOF = true;
                    } else {
                        t.append(b);
                    }
                }
                System.out.println(t.toString());
                responseLine = t.toString();
                dataInputStream.close();
                dataOutputStream.close();

                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseLine;
        }
    }

    private String decryptCFB(String encryptedtext, String key, String iv) throws Exception{


        //byte [] bytekey = decryptkey.getBytes();
        //byte [] byteiv = iv.getBytes();

        byte[] bytekey = Base64.decode(String.valueOf(key), Base64.DEFAULT);
        byte[] byteiv = Base64.decode(String.valueOf(iv), Base64.DEFAULT);

        SecretKeySpec keySpec = new SecretKeySpec(bytekey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(byteiv);

        Cipher c = Cipher.getInstance("AES/CFB/NoPadding");
        c.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decodedValue = Base64.decode(String.valueOf(encryptedtext), Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);

        String decryptedValue = new String(decValue);
        decryptedString = Base64.encodeToString(decValue, Base64.DEFAULT);
        return decryptedString;
    }

    private String decrypt(String ssid, String bssid) {

        try {
            ssid=decryptCFB(ssid, key2, iv2);
            ssid = ssid.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int key = 11;
        String message = "";

        String[] alnum = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "1", "2", "3", "4", "5", "6", "7", "8"};

        System.out.println("SSID:"+ssid);
        String timest = ssid.substring(0,4);
        String lat_new = ssid.substring(4, 10);
        String lon_new = ssid.substring(10, 16);
        String headin = ssid.substring(16,18);

        double n = 0;
        double btimestamp = 0;
        System.out.println(timest);

        for (int i = 1; i < timest.length(); i++) {
            String a = timest.substring(i, i + 1);
            n = obj.indexOf(a);
            System.out.println(n);
            if(i==1){
                btimestamp += n * Math.pow(60, 2);
            }
            else {
                btimestamp += n * Math.pow(60, 3 - i);
            }
            //System.out.println(btimestamp);
        }
        System.out.println(btimestamp);
        beacontime= btimestamp;

        double p = 0;
        double bhead = 0;
        System.out.println(headin);
        for (int i = 0; i < headin.length(); i++) {
            String a = headin.substring(i, i + 1);
            p = obj.indexOf(a);
            System.out.println(p);
            bhead += p * Math.pow(60, 1 - i);
            System.out.println(bhead);
        }
        beaconhead = bhead;

        ArrayList<String> obj = new ArrayList<String>(Arrays.asList(alnum));
        System.out.println(obj);
        String lat_new_d = lat_new.substring(0, 2);
        String lat_new_b = lat_new.substring(2, 6);
        double m = 0;
        double lat_i = 0;
        for (int i = 0; i < lat_new_d.length(); i++) {
            String a = lat_new_d.substring(i, i + 1);
            m = obj.indexOf(a);
            System.out.println(m);
            lat_i += m * Math.pow(60, 1 - i);
            System.out.println(lat_i);
        }
        double lat_f = 0;
        for (int i = 0; i < lat_new_b.length(); i++) {
            String a = lat_new_b.substring(i, i + 1);
            lat_f += Math.pow(60, 3 - i) * obj.indexOf(a);
            System.out.println(lat_f);
        }
        lat_f = lat_f * 0.0000001;
        System.out.println(lat_i + lat_f);

        String lon_new_d = lon_new.substring(0, 2);
        String lon_new_b = lon_new.substring(2, 6);
        double lon_i = 0;
        for (int i = 0; i < lon_new_d.length(); i++) {
            String a = lon_new_d.substring(i, i + 1);
            lon_i += (Math.pow(60, 1 - i)) * obj.indexOf(a);
        }
        double lon_f = 0;
        for (int i = 0; i < lon_new_b.length(); i++) {
            String a = lon_new_b.substring(i, i + 1);
            lon_f += (Math.pow(60, 3 - i)) * obj.indexOf(a);
        }

        lon_f = lon_f * 0.0000001;
        System.out.println(lon_i + lon_f);

        double sign_lat = lat_i + lat_f;
        double sign_lon = lon_i + lon_f;

        System.out.println(sign_lat);
        System.out.println(sign_lon);
        String signLocTxt= "Lat: " + sign_lat + "\n" + "Lon: " + sign_lon;
        signLoc.setText(signLocTxt);

        String encrypted = ssid.substring(18);
        char a[] = encrypted.toCharArray();
        System.out.println(encrypted);

        String symbol_new="";
        for(int i=0; i<encrypted.length();i++){
            int k=obj.indexOf(Character.toString(a[i]))-key;
//            System.out.print(k+" ");
            if(k<0){
                k=60+k;
            }
            symbol_new+=obj.get(k);
        }
        System.out.println(symbol_new);

//        for (int i = 0; i < encrypted.length(); i++) {
//            char symbol;
//            if (Character.isLetter(a[i])) {
//                int num = (int) a[i];
//
//                if ((num >= 65) & (num <= 90)) {
//                    num -= key;
//                    if (num < 65)
//                        num += 26;
//                    if (num > 90)
//                        num -= 26;
//                    symbol = (char) num;
//                } else if (a[i] == 'w') {
//                    symbol = '-';
//                } else {
//                    num -= 49;
//                    symbol = (char) num;
//                }
//            } else {
//                symbol = a[i];
//            }
//            message += symbol;
//        }

        return symbol_new;
    }



//    private Double getHeading() {
//        LatLng CurrentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        Double head = SphericalUtil.computeHeading(CurrentLocation, preLocation);
//        if (head < 0) {
//            return head + 360;
//        } else return head;
//    }
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            try {
                unregisterReceiver(this);
                for (ScanResult scanResult : results) {
                    arrayList.add(scanResult.SSID + " ; " + scanResult.level + " ; " + scanResult.capabilities + " ; " + scanResult.timestamp + " ; " + scanResult.BSSID);
                    adapter.notifyDataSetChanged();
                }

                System.out.println(arrayList);

                for (int i = 0; i < arrayList.size(); i++) {
                    String rsu[] = arrayList.get(i).split(";");
                    if (rsu.length == 5) {
                        String ssid = arrayList.get(i).split(";")[0];
                        if (ssid.contains("R2v")) {
                            String rssi_val = arrayList.get(i).split(";")[1];
                            String wifi_info = arrayList.get(i).split(";")[2];
                            String timestamp_val = arrayList.get(i).split(";")[3];
                            String mac_val = arrayList.get(i).split(";")[4];

                            if ((prev_mac_val.equals(mac_val)) && (prev_timestamp_val > Long.parseLong(timestamp_val.replaceAll(" ", "")))) {
                                System.out.println("Continue");
                                continue;
                            }
                            if (unixTimeNow!=unixTime) {
                                unixTimeTxt.setText(String.valueOf(unixTime));
                                unixTimeNow=unixTime;
                            }
                            status.setText(ssid);
                            status.setTextColor(Color.RED);
                            String dec = decrypt(ssid.substring(3), mac_val);
                            String dec_ssid = dec;
                            System.out.println(dec_ssid);

                            txtDec.setText(dec);
                            rssi.setText(rssi_val);
                            String dec_s = dec.toLowerCase();
                            dec_s = dec_s.replace('-', '0') + ".jpg";
                            dec_s = dec_s.replace(" ", "");
                            String path = "drawable/" + dec_s;
                            System.out.println(dec_s);
//                            InputStream stream = getAssets().open(dec_s);
//                            Drawable d = Drawable.createFromStream(stream, null);
//                            int signResource= getResources().getIdentifier("@drawable/aps001",null,getPackageName());
//                            System.out.println(signResource);
//                            signView.setImageDrawable(d);
                            rssi.setTextColor(Color.BLUE);
                            timestamp.setText(timestamp_val);
                            timestamp.setTextColor(Color.GREEN);
                            timestamptxt.setText(String.valueOf(beacontime));
                            info.setText(String.valueOf(beaconhead));
                            info.setTextColor(Color.MAGENTA);

                            //String locdisplay = dec_lat+","+dec_lon;
                            //signLoc.setText(locdisplay);
                            //timestamptxt.setText(dec_time);
                            headtxt.setText(Double.toString(beaconhead));


                            button.performClick();
//                            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//                            lonTxt.setText(longitude + "");
//                            latTxt.setText(latitude + "");
//                            if(location!=null){
                            savewithLocation(ssid, rssi_val, timestamp_val, mac_val, latitude, longitude);
//                            }
//                            else {
//                                save(ssid, rssi_val, timestamp_val, mac_val);//,loc.getLatitude(),loc.getLongitude());
//                            }
                            prev_mac_val = mac_val;
                            prev_timestamp_val = Long.parseLong(timestamp_val.replaceAll(" ", ""));
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    private void savewithLocation(String ssid, String rssi, String timestamp, String bssid, double latitude, double longitude) {
        try {

            String entry = speed + "," + timestamp + "," +
                    ssid + "," +
                    rssi + "," +
                    bssid + "," +
                    latitude + "," +
                    longitude + "\n";
            fw = new FileWriter(fos.getFD());
            fw.append(entry);
            fw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public void save(String ssid, String rssi, String timestamp, String bssid) {
        try {

            String entry = speed + "," + timestamp + "," +
                    ssid + "," +
                    rssi + "," +//+
                    bssid + "\n";// +
//                    lon + "\n";
            fw = new FileWriter(fos.getFD());
            fw.append(entry);
//            fw.newLine();

            fw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}


