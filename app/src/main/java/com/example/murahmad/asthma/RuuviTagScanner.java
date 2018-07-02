package com.example.murahmad.asthma;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RuuviTagScanner extends Service {
    public RuuviTagScanner() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
