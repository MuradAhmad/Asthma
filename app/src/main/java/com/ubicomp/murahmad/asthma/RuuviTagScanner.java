package com.ubicomp.murahmad.asthma;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.neovisionaries.bluetooth.ble.advertising.ADManufacturerSpecific;
import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.EddystoneURL;

import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RuuviTagScanner extends Service {
    public RuuviTagScanner() {
    }

    private ArrayList<LeScanResult> scanResults;
    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService alertScheduler;

    private class LeScanResult {
        BluetoothDevice device;
        int rssi;
        byte[] scanData;
    }

    List<RuuviTag> ruuvitagArrayList;
    //private BeaconManager beaconManager;
    private BackgroundPowerSaver bps;
    SharedPreferences settings;
    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;
    Region region;

    String morningTime;
    String eveningTime;


    private String[] titles;
    private String backendUrl;
    //private PlotSource plotSource;
    private Integer[] alertValues;
    private int notificationId;
    // private int MAX_NUM_NOTIFICATIONS = 5;
    private Timer timer;
    // private NotificationCompat.Builder notification;


    private BluetoothAdapter bluetoothAdapter;
    private Handler scanTimerHandler;
    private static int MAX_SCAN_TIME_MS = 1000;
    private boolean scanning;





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle data = intent.getExtras();
        if (data != null) {
           save((RuuviTag) data.getParcelable("favorite"));
        }


        startNotification();


        return Service.START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(mListener);
        notificationId = 1512;

        // commited by me


        if (settings.getBoolean("pref_bgscan", false))
            startFG();
            //startNotification();


        Foreground.init(getApplication());
        Foreground.get().addListener(listener);

        bps = new BackgroundPowerSaver(this);

        ruuvitagArrayList = new ArrayList<>();



        dbHandler = new Database(getApplicationContext());
        db = dbHandler.getWritableDatabase();



/*
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.))
        beaconManager.bind(this);*/

        backendUrl = settings.getString("pref_backend", null);

        //by me    plotSource =  PlotSource.getInstance();

        scanTimerHandler = new Handler();

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();


        scheduler = Executors.newSingleThreadScheduledExecutor();
        int scanInterval = Integer.parseInt(settings.getString("pref_scaninterval", "5")) * 1000;
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!scheduler.isShutdown())
                    startScan();
                // added by me notification method here to see if it sends notification or not
                //startNotification();
            }
        }, 0, scanInterval - MAX_SCAN_TIME_MS + 1, TimeUnit.MILLISECONDS);

       /* timer = new Timer();
        TimerTask alertManager = new RuuviTagScanner.alertManager();
        timer.scheduleAtFixedRate(alertManager, 2500, 2500);*/


     //  startNotification();


    }


    private void startScan() {
        if (scanning)
            return;

        scanTimerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanning = false;
                bluetoothAdapter.stopLeScan(mLeScanCallback);
                processFoundDevices();
            }
        }, MAX_SCAN_TIME_MS);

        scanResults = new ArrayList<LeScanResult>();
        scanning = true;
        bluetoothAdapter.startLeScan(mLeScanCallback);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Iterator<LeScanResult> itr = scanResults.iterator();

                    LeScanResult dev = new LeScanResult();
                    dev.device = device;
                    dev.rssi = rssi;
                    dev.scanData = scanRecord;

                    boolean devFound = false;

                    while (itr.hasNext()) {
                        LeScanResult element = itr.next();
                        if (device.getAddress().equalsIgnoreCase(element.device.getAddress()))
                            devFound = true;
                    }


                    if (!devFound) {
                        scanResults.add(dev);
                    }
                }
            };


    void processFoundDevices() {
        ruuvitagArrayList.clear();
       // ScanEvent scanEvent = new ScanEvent(getApplicationContext(), DeviceIdentifier.id(getApplicationContext()));

        Iterator<LeScanResult> itr = scanResults.iterator();
        while (itr.hasNext()) {
            LeScanResult element = itr.next();

            // Parse the payload of the advertisement packet
            // as a list of AD structures.
            List<ADStructure> structures =
                    ADPayloadParser.getInstance().parse(element.scanData);

            // For each AD structure contained in the advertisement packet.
            for (ADStructure structure : structures) {
                if (structure instanceof EddystoneURL) {
                    // Eddystone URL
                    EddystoneURL es = (EddystoneURL) structure;
                    if (es.getURL().toString().startsWith("https://ruu.vi/#") || es.getURL().toString().startsWith("https://r/")) {
                        // Creates temporary ruuvitag-object, without heavy calculations
                        RuuviTag temp = new RuuviTag(element.device.getAddress(), es.getURL().toString(), null, "" + element.rssi, true);
                        if (checkForSameTag(temp)) {
                            // Creates real object, with temperature etc. calculated
                            RuuviTag real = new RuuviTag(element.device.getAddress(), es.getURL().toString(), null, "" + element.rssi, false);
                            ruuvitagArrayList.add(real);
                            Log.d("Ruuvitag ID Scanner:", real.getId());
                            Log.d("Temperature Scanner: ", real.getTemperature());

                            Log.d("Humidity Scanner:", real.getHumidity());

                            update(real);



                        }
                    }

                }
                // If the AD structure represents Eddystone TLM.
                else if (structure instanceof ADManufacturerSpecific) {
                    ADManufacturerSpecific es = (ADManufacturerSpecific) structure;
                    if (es.getCompanyId() == 0x0499) {

                        byte[] data = es.getData();
                        if (data != null) {

                            RuuviTag tempTag = new RuuviTag(element.device.getAddress(), null, data, "" + element.rssi, true);
                            if (checkForSameTag(tempTag)) {
                                // Creates real object, with temperature etc. calculated
                                RuuviTag real = new RuuviTag(element.device.getAddress(), null, data, "" + element.rssi, false);
                                ruuvitagArrayList.add(real);
                                Log.d("Ruuvitag ID Scanner :", real.getId());
                                Log.d("Temperature Scanner :", real.getTemperature());

                                Log.d("Humidity Scanner :", real.getHumidity());
                                update(real);


                            }
                        }
                    }


                }
            }


            }

        }




    public SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (settings.getBoolean("pref_bgscan", false))
                startFG();


            if (!settings.getBoolean("pref_bgscan", false))
                stopForeground(true);

            backendUrl = settings.getString("pref_backend", null);

            scheduler.shutdown();

            scheduler = Executors.newSingleThreadScheduledExecutor();
            int scanInterval = Integer.parseInt(settings.getString("pref_scaninterval", "5")) * 1000;
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (!scheduler.isShutdown())
                        startScan();
                }
            }, 0, scanInterval - MAX_SCAN_TIME_MS + 1, TimeUnit.MILLISECONDS);

            /*
            try
            {
                beaconManager.setForegroundBetweenScanPeriod
                        (Long.parseLong(settings.getString("pref_scaninterval", "1")) * 1000 - 1000l);
                beaconManager.updateScanPeriods();
            } catch (RemoteException e)
            {
                e.printStackTrace();
            }*/
        }
    };



    public void startFG()
    {

 Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent1111 = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder notification;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        notification
                = new NotificationCompat.Builder(getApplicationContext());


/*.setContentTitle(this.getString(R.string.scanner_notification_title))
                .setSmallIcon(R.mipmap.ic_launcher_small)
                .setTicker(this.getString(R.string.scanner_notification_ticker))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(this.getString(R.string.scanner_notification_message)))
                .setContentText(this.getString(R.string.scanner_notification_message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent);*//*
*/


        startForeground(notificationId, notification.build());






    }





/*
    public void startNotification()
    {
        // Notification code from main activity

        // send User notification

        cursor = db.rawQuery("SELECT * FROM " + Database.SETTING_TABLE +" order by Timestamp desc limit 1", null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

                // get values from cursor here

                morningTime = cursor.getString(cursor.getColumnIndex(Database.MORNING_TIME));
                eveningTime = cursor.getString(cursor.getColumnIndex(Database.EVENING_TIME));

                Log.d("Morning Time", String.valueOf(morningTime));
                Log.d("Evening Time", String.valueOf(eveningTime));


            }

        }
        cursor.close();


     String[] parts = morningTime.split(":");
        String part1 = parts[0]; // hour
        String part2 = parts[1]; // minutes

        Log.d("Morning Hour", String.valueOf(part1));
        Log.d("Morning Minutes", String.valueOf(part2));


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND,0);
        long currentTime = calendar.getTimeInMillis();
        // System.currentTimeMillis();

        Log.d("current Time", String.valueOf(currentTime));


     calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(part1));
        calendar.set(Calendar.MINUTE, Integer.valueOf(part2));
        calendar.set(Calendar.SECOND, 0);


        ///Log.d("Morning time from DB", morningTime);

           Long morningTimeLong= Long.valueOf(morningTime);
        Log.d("Morning time from DB", String.valueOf(morningTimeLong));
        if (morningTimeLong.compareTo(currentTime)==0) {//morningTime == String.valueOf(currentTime)

            Intent notificationIntent = new Intent(this, NotificationReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this,App.CHANNEL_1_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notification_24dp)
                    //.addAction(R.drawable.ic_notification_24dp, "Call", pendingIntent)
                    .setContentTitle("FEEDBACK")
                    .setContentText("Submit daily symptoms")
                    .setShowWhen(true)
                    .setAutoCancel(true)

                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);

            startForeground(notificationId, notification.build());

        }



    }*/



    public void startNotification(){


        cursor = db.rawQuery("SELECT * FROM " + Database.SETTING_TABLE +" order by Timestamp desc limit 1", null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

                // get values from cursor here

                morningTime = cursor.getString(cursor.getColumnIndex(Database.MORNING_TIME));
                eveningTime = cursor.getString(cursor.getColumnIndex(Database.EVENING_TIME));

                Log.d("Morning Time Sc", String.valueOf(morningTime));
                Log.d("Evening Time Sc", String.valueOf(eveningTime));


            }

        }
        cursor.close();


        if((morningTime != null && !morningTime.isEmpty()) || (eveningTime != null && !eveningTime.isEmpty()) ) {



            Long morningTimeLong = Long.valueOf(morningTime);
            Long eveningTimeLong = Long.valueOf(eveningTime);

            Log.d("Morning time from Set", String.valueOf(morningTimeLong));




            // set notification time here
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 30);
            calendar.set(Calendar.MILLISECOND, 0);
            Long currentTime = calendar.getTimeInMillis();
            // System.currentTimeMillis();

            Log.d("current Time", String.valueOf(currentTime));



            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if ((morningTimeLong.compareTo(currentTime)>=0) || (morningTimeLong.compareTo(currentTime)>=0) ) {


                Intent intent = new Intent(this, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_ONE_SHOT);
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_ONE_SHOT);


                // AlarmManager.Interval_Day set according to settings screen
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, morningTimeLong, AlarmManager.INTERVAL_DAY, pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, eveningTimeLong , AlarmManager.INTERVAL_DAY, pendingIntent1);

            }

        }




    }



/*

    public void startNotification(){


        cursor = db.rawQuery("SELECT * FROM " + Database.SETTING_TABLE +" order by Timestamp desc limit 1", null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {

                // get values from cursor here

                morningTime = cursor.getString(cursor.getColumnIndex(Database.MORNING_TIME));
                eveningTime = cursor.getString(cursor.getColumnIndex(Database.EVENING_TIME));

                Log.d("Morning Time Sc", String.valueOf(morningTime));
                Log.d("Evening Time Sc", String.valueOf(eveningTime));


            }

        }
        cursor.close();


        if(morningTime != null && !morningTime.isEmpty()) {


            // set notification time here
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 30);
            calendar.set(Calendar.MILLISECOND, 0);
            Long currentTime = calendar.getTimeInMillis();
            // System.currentTimeMillis();

            Log.d("current Time", String.valueOf(currentTime));

            Long morningTimeLong = Long.valueOf(morningTime);
            //Long eveningTimeLong = Long.valueOf(eveningTime);

            Log.d("Morning time from Set", String.valueOf(morningTimeLong));


            */
/*
            if(currentDate.compareTo(datadb)>=0) {
                myCheckBox.setChecked(true);
                myCheckBox.setEnabled(false);
            }
            *//*


            if (morningTimeLong.equals(currentTime))  {

                Intent intent = new Intent(this, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                // AlarmManager.Interval_Day set according to settings screen
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            }

        }

        //||

        if(eveningTime != null && !eveningTime.isEmpty()) {

            // set notification time here
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 30);
            calendar.set(Calendar.MILLISECOND, 0);
            Long currentTime = calendar.getTimeInMillis();
            // System.currentTimeMillis();

            Log.d("current Time", String.valueOf(currentTime));


            Long eveningTimeLong = Long.valueOf(eveningTime);

            Log.d("Evening time from Set", String.valueOf(eveningTimeLong));

            if (eveningTimeLong.equals(currentTime)) {

                Intent intent = new Intent(this, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                // AlarmManager.Interval_Day set according to settings screen
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            }

        }


    }
*/


    public void save(RuuviTag ruuvitag) {
        String time = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss").format(new Date());

        if(!Exists(ruuvitag.getId())) {
            ContentValues values = new ContentValues();
            values.put(Database.DEVICE_ID, ruuvitag.getId());
            values.put(Database.URL, ruuvitag.getUrl());
            values.put(Database.RSSI, ruuvitag.getRssi());
            values.put(Database.TEMPERATURE, ruuvitag.getTemperature());
            values.put(Database.HUMIDITY, ruuvitag.getHumidity());
            //values.put(Database.PRESSURE, ruuvitag.getPressure());
            values.put(Database.DATE, time);


            dbHandler.insertDeviceData(values);
        }
    }



    public void update(RuuviTag ruuvitag) {
        String time = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss").format(new Date());

        if(Exists(ruuvitag.getId())) {
            ContentValues values = new ContentValues();
            values.put(Database.DEVICE_ID, ruuvitag.getId());
            values.put(Database.URL, ruuvitag.getUrl());
            values.put(Database.RSSI, ruuvitag.getRssi());
            values.put(Database.TEMPERATURE, ruuvitag.getTemperature());
            values.put(Database.HUMIDITY, ruuvitag.getHumidity());
            //values.put(Database.PRESSURE, ruuvitag.getPressure());
            values.put(Database.DATE, time);

            db.update(Database.DEVICE_TABLE, values, "id="+ DatabaseUtils.sqlEscapeString(ruuvitag.getId()), null);
        }else{
            save(ruuvitag);
        }
    }

    public boolean Exists(String id) {
        cursor = db.rawQuery("select 1 from DEVICE_TABLE where "+Database.DEVICE_ID+"= ? ",
                new String[] { id });
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }






    @Override
    public void onDestroy() {


        scheduler.shutdown();
        /*
        try {
            beaconManager.stopRangingBeaconsInRegion(new Region("uusi", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.unbind(this);*/

        settings.unregisterOnSharedPreferenceChangeListener(mListener);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Foreground.Listener listener = new Foreground.Listener() {
        public void onBecameForeground() {
            if (!isRunning(RuuviTagScanner.class))
                startService(new Intent(RuuviTagScanner.this, RuuviTagScanner.class));
        }

        public void onBecameBackground() {
            if (!settings.getBoolean("pref_bgscan", false)) {
               if (timer!=null) timer.cancel();
                if (scheduler!= null) scheduler.shutdown();
                stopSelf();
            }
        }
    };
    private boolean isRunning(Class<?> serviceClass) {
        ActivityManager mgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : mgr.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


private boolean checkForSameTag(RuuviTag ruuvi) {
        for(RuuviTag ruuvitag : ruuvitagArrayList) {
        if(ruuvi.getId().equals(ruuvitag.getId())) {
        return false;
        }
        }
        return true;
        }
    public Integer[] readSeparated(String data) {
        String[] linevector;
        int index = 0;

        linevector = data.split(",");

        Integer[] values = new Integer[linevector.length];

        for(String l : linevector) {
            try {
                values[index] = Integer.parseInt(l);
            } catch (NumberFormatException e) {
                values[index] = null;
            }
            index++;
        }

        return values;
    }
}

