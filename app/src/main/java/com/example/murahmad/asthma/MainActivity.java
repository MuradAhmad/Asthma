
package com.example.murahmad.asthma;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Calendar;
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
    Cursor cursor;

    private Timer timer;
    private Handler scanTimerHandler;
    private static int MAX_SCAN_TIME_MS = 1000;
    int morningHr,morningMin, eveningHr, eveningMin;





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


         android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        handler = new Database(this);
        db = handler.getReadableDatabase();


        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigationView);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){


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



        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard()).commit();
            bottomNavigationView.setSelectedItemId(R.id.dashboard);
        }



        // send User notification

        cursor = db.rawQuery("SELECT * FROM " + Database.SETTING_TABLE +" order by Timestamp desc limit 1", null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

                // get values from cursor here

                morningHr = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Database.MORNING_HR)));
                morningMin = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Database.MORNING_MIN)));
                eveningHr = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Database.EVENING_HR)));
                eveningMin = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Database.EVENING_MIN)));

                Log.d("Morning hour", String.valueOf(morningHr));
                Log.d("Morning Min", String.valueOf(morningMin));
               Log.d("Evening hour", String.valueOf(eveningHr));
               Log.d("Evening Min", String.valueOf(eveningMin));


            }

        }
        cursor.close();


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,morningHr);
        calendar.set(Calendar.MINUTE,morningMin);
        calendar.set(Calendar.SECOND,30);

        // set evening notification
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY,eveningHr);
        calendar2.set(Calendar.MINUTE,eveningMin);
        calendar2.set(Calendar.SECOND,30);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        // AlarmManager.Interval_Day set according to settings screen
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar2.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);








     /*   if(ruuviTag.getTemperature() != null) {
            txtTemperature.setText(ruuviTag.getTemperature());

        } else
            txtTemperature.setText("100");*/

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);


        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();


        beaconManager = BeaconManager.getInstanceForApplication(this);





      /*  runOnUiThread(new Runnable() {
            public void run() {
                //Whatever task you wish to perform
                //For eg. textView.setText("SOME TEXT")
               // cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE, null);
                cursor = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE, null);

                if (cursor != null) {
                    cursor.moveToFirst();

                    if (cursor.getCount() > 0) {
// get values from cursor here



                        Log.d("Device ID from Main: ", cursor.getString(cursor.getColumnIndex(Database.USERNAME)));
                        Log.d("Temperature from Main: ", cursor.getString(cursor.getColumnIndex(Database.DOB)));
                        Log.d("Humidity from Main: ", cursor.getString(cursor.getColumnIndex(Database.EMAIL)));



     *//*                   String deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
                        String temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));
                        String humidity = cursor.getString(cursor.getColumnIndex(Database.HUMIDITY));*//*

                  *//*      txtDeviceId.setText(deviceId);
                        txtTemperature.setText(temperature + " °C ");
                        txtHumidity.setText(humidity + " % ");*//*

                      *//*  Log.d("Device ID from Main: ", deviceId);
                        Log.d("Temperature from Main: ", temperature);
                        Log.d("Humidity from Main: ", humidity);*//*
                    }
                }
            }
        });

*/









       /* String time = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss").format(new Date());


        ContentValues values = new ContentValues();
        values.put(Database.DEVICE_ID, 12);
        values.put(Database.URL, "URL");
        values.put(Database.RSSI, "RSSI");
        values.put(Database.TEMPERATURE, "12345");
        //values.put(Database.HUMIDITY, ruuvitag.getHumidity());
        //values.put(Database.PRESSURE, ruuvitag.getPressure());
        values.put(Database.DATE, time);

        handler.insertDeviceData( values);


        cursor = db.rawQuery("SELECT * FROM " + Database.DEVICE_TABLE, null);

        if(cursor != null) {
            cursor.moveToFirst();

            if(cursor.getCount() > 0){
// get values from cursor here


            String deviceId = cursor.getString(cursor.getColumnIndex(Database.DEVICE_ID));
            String temperature = cursor.getString(cursor.getColumnIndex(Database.TEMPERATURE));

            txtTemperature.setText(temperature);
            }
        }
*/






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


    @Override
    protected void onStart() {

        Intent intent = new Intent(MainActivity.this, RuuviTagScanner.class);
        startService(intent);

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Permission check for Marshmallow and newer
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()){


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
}