
package com.ubicomp.murahmad.asthma;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;


public class MainActivity extends AppCompatActivity {

    private class LeScanResult {
        BluetoothDevice device;
        int rssi;
        byte[] scanData;
    }

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private static final int REQUEST_READ_PHONE_STATE = 100;

   // Fused Location and Location request



    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 102;
    private boolean permissionIsGranted = false;



    // array lists for local database tables
    private List<String> symptomsList;
    private List<String> feedbackList;
    private List<String> deviceList;

    private static final String TAG = "MainActivity";


    //   private static final String ALTBEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";


    private ArrayList<LeScanResult> scanResults;

    private BluetoothAdapter bluetoothAdapter;
    private boolean scanning;


    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService alertScheduler;

    SharedPreferences settings;

    RuuviTag ruuviTag;


    Database handler;
    SQLiteDatabase db;
    Cursor cursor, cursor1, cursor2;

    private Timer timer;
    private Handler scanTimerHandler;
    private static int MAX_SCAN_TIME_MS = 1000;
    String morningTime;
    String eveningTime;


    private BeaconManager beaconManager = null;

    private Region beaconRegion = null;


    private boolean entryMessageRaised = false;
    private boolean exitMessageRaised = false;
    private boolean rangingMessageRaised = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // toolBar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //user Location

        // final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);


       /* fusedLocationProviderClient = new FusedLocationProviderClient(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

*/

        // send symptoms, feedback, location. device data to online server
        // get data from local DB

       /* handler = new Database(this);
        db = handler.getReadableDatabase();*/

        // array list to store data from local DB
        symptomsList = new ArrayList<>();
        feedbackList = new ArrayList<>();
        deviceList = new ArrayList<String>();




        // Bluetooth manager

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        beaconManager = BeaconManager.getInstanceForApplication(this);

        //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_FINE_LOCATION);
        } else {
            permissionIsGranted = true;
        }



        // permissions for UUID

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }

        //navigation and fragment

        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {


                    case R.id.dashboard:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard()).commit();
                        break;

                    case R.id.medication:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Medication()).commit();
                        break;

                    case R.id.symptoms:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Symptoms()).commit();
                        break;


                }
                return true;
            }
        });


        // display dashboard as main screen

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard()).commit();
            bottomNavigationView.setSelectedItemId(R.id.dashboard);
        }


    }





    /*
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.))
        beaconManager.bind(this);*//*


     */
/*  beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
    beaconManager.bind(this);

    beaconManager.setMonitorNotifier(new MonitorNotifier() {
        @Override
        public void didEnterRegion(Region region) {
            if(!entryMessageRaised){

                Toast.makeText(MainActivity.this, "Beacon found"+"Beacon Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                                +"minor"+region.getId3() ,
                        Toast.LENGTH_LONG).show();

                Log.d("TAG","Beacon found"+"Beacon Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                        +"minor"+region.getId3());
                entryMessageRaised = true;
            }
        }

        @Override
        public void didExitRegion(Region region) {

            if(!exitMessageRaised) {

                Toast.makeText(MainActivity.this, "Beacon not found/ Leaving" + "Beacon Unique Id" + region.getUniqueId() + "Beacon UUID/Major/Minor" + region.getId1() + "major" + region.getId2()
                                + "minor" + region.getId3(),
                        Toast.LENGTH_LONG).show();
                Log.d("TAG","Beacon not found/ Leaving"+"Beacon Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                                +"minor"+region.getId3());

                exitMessageRaised = true;

            }

        }

        @Override
        public void didDetermineStateForRegion(int i, Region region) {

        }
    });


    beaconManager.setRangeNotifier(new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beaconCollection, Region region) {
            if(!rangingMessageRaised && beaconCollection != null && !beaconCollection.isEmpty()){
                for(Beacon beacon:beaconCollection ){

                    Toast.makeText(MainActivity.this, "Beacon exits" + "Beacon ranging Unique Id" + region.getUniqueId() + "Beacon UUID/Major/Minor" + region.getId1() + "major" + region.getId2()
                                    + "minor" + region.getId3(),
                            Toast.LENGTH_LONG).show();
                    Log.d("TAG","Beacon exits"+"Beacon ranging Unique Id"+region.getUniqueId() +"Beacon UUID/Major/Minor" + region.getId1() +"major" +region.getId2()
                            +"minor"+region.getId3());
                    rangingMessageRaised = true;

                }
            }


        }
    });




    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    @Override
    public void onBeaconServiceConnect() {

    }
    private  void startBeaconMonitong (){
        Log.d("TAG", "Beacon  Monitoring Started");
        try {

            beaconRegion = new Region("MyBeacons", Identifier.parse("unique id"), Identifier.parse("4"),Identifier.parse("200"));

        beaconManager.startMonitoringBeaconsInRegion(beaconRegion);
        beaconManager.startRangingBeaconsInRegion(beaconRegion);
        }
          catch (RemoteException e){
            e.printStackTrace();

          }
        
    }
    private void stopBeaconMonitoring (){
        Log.d("TAG", "Beacon  Monitoring Stoped");
        try {

               beaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
               beaconManager.stopRangingBeaconsInRegion(beaconRegion);


        }   catch (RemoteException e){
            e.printStackTrace();
        }
*//*


}

*/

/*

    @Override
    public void onConnected(Bundle bundle){
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        Toast.makeText(getApplicationContext(), String.valueOf(myLatitude) + String.valueOf(myLongitude) +"Satellite.. "+ String.valueOf(location.getExtras().getInt("satellites")), Toast.LENGTH_SHORT).show();
        Log.d("satellites",String.valueOf(location.getExtras().getInt("satellites")));


    }



    private void requestLocationUpdates(){


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
*/

    @Override
    protected void onStart() {

        Intent intent = new Intent(MainActivity.this, RuuviTagScanner.class);
        startService(intent);

        super.onStart();

        // googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Permission check for Marshmallow and newer
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }


      /*  if (permissionIsGranted) {
            // if (googleApiClient.isConnected()) {
            //requestLocationUpdates();
            //}
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        //if (permissionIsGranted) LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if (permissionIsGranted)
        // googleApiClient.disconnect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfile()).commit();
                break;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Setting()).commit();
                break;

            case R.id.logout:
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 1)
            switch (requestCode) {
                case MY_PERMISSION_REQUEST_FINE_LOCATION:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission granted
                        permissionIsGranted = true;
                    } else {
                        //permission denied
                        permissionIsGranted = false;
                        Toast.makeText(getApplicationContext(), "This app requires location permission to be granted", Toast.LENGTH_SHORT).show();

                    }
                    break;
                case MY_PERMISSION_REQUEST_COARSE_LOCATION:
                    // do something for coarse location
                    break;
            }
    }



}