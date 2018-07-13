package com.example.murahmad.asthma;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    //DBHandler handler;
    //SQLiteDatabase db;
    //Cursor cursor;
    Region region;
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
            //save((RuuviTag) data.getParcelable("favorite"));
        }
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(mListener);
        notificationId = 1512;

        // commited by me

      /*  titles = new String[]{ getString(R.string.alert_notification_title0),
                getString(R.string.alert_notification_title1),
                getString(R.string.alert_notification_title2),
                getString(R.string.alert_notification_title3),
                getString(R.string.alert_notification_title4),
                getString(R.string.alert_notification_title5),
                getString(R.string.alert_notification_title6),
                getString(R.string.alert_notification_title7)
        };*/

        if (settings.getBoolean("pref_bgscan", false))
            startFG();


        Foreground.init(getApplication());
        Foreground.get().addListener(listener);

        bps = new BackgroundPowerSaver(this);

        ruuvitagArrayList = new ArrayList<>();

        // commit by me
        /*
        handler = new DBHandler(getApplicationContext());
        db = handler.getWritableDatabase();
*/
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
            }
        }, 0, scanInterval - MAX_SCAN_TIME_MS + 1, TimeUnit.MILLISECONDS);

      /*  timer = new Timer();
        TimerTask alertManager = new RuuviTagScanner.alertManager();
        timer.scheduleAtFixedRate(alertManager, 2500, 2500);*/
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
                            Log.d("Ruuvitag ID", real.getId());
                            Log.d("Temperature", real.getTemperature());

                            Log.d("Humidity", real.getHumidity());
                          /*  update(real);
                            scanEvent.addRuuvitag(real);*/
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
                                Log.d("Ruuvitag ID", real.getId());
                                Log.d("Temperature", real.getTemperature());

                                Log.d("Humidity", real.getHumidity());
                              /*  update(real);
                                scanEvent.addRuuvitag(real);*/
                            }
                        }
                    }


                }
            }

/*
            if(beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                // Parse url from beacon advert
                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
            }*/

          /*  if (backendUrl != null) {
                //JsonObject json = new JsonObject();
                //  JsonObject json = JSON.(scanEvent);
                String jsonData = new Gson().toJson(scanEvent);
                Ion.with(getApplicationContext())
                        .load(backendUrl)
                        .setJsonPojoBody(scanEvent)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                            }
                        });*/
            }
          /*  plotSource.addScanEvent(scanEvent);

            exportRuuvitags();*/
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

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

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
                .setContentIntent(pendingIntent);*/

        startForeground(notificationId, notification.build());
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
                timer.cancel();
                scheduler.shutdown();
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

