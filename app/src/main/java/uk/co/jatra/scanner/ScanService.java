package uk.co.jatra.scanner;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ScanService extends Service {

    private static final String TAG = "ScanService";
    public static final String STOP_SERVICE = "stop.service";

    private Looper looper;
    private Handler handler;
    private long startTime;

    public static void start(Context context) {
        Intent intent = new Intent(context, ScanService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, ScanService.class);
        context.stopService(intent);
    }

    private int runCount;
    private Runnable scan = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run() on thread "+Thread.currentThread().toString());
            if (SystemClock.elapsedRealtime() - startTime > 10000) {
                Log.d(TAG, "stoppingself");
                stopSelf();
            }
            else {
                Notifier.notify(ScanService.this, "ScanService", ++runCount);
                handler.postDelayed(this, 3000);
            }
        }
    };

    public ScanService() {
        Log.d(TAG, "Constructor on thread "+Thread.currentThread().toString());
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
        super.onCreate();
        HandlerThread thread = new HandlerThread("Scan", Thread.NORM_PRIORITY);
        thread.start();
        looper = thread.getLooper();
        handler = new Handler(looper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() on thread "+Thread.currentThread().toString());
        startTime = SystemClock.elapsedRealtime();
        handler.removeCallbacks(scan);
        handler.postDelayed(scan, 5000);
        toForeground();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        handler.removeCallbacks(scan);
        looper.quit();
        super.onDestroy();
    }

    private void toForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(STOP_SERVICE, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_menu_compass)
                        .setContentTitle("Scanning")
                        .setContentText("Scanning for this that and everything. (Pretending)")
                .addAction(android.R.drawable.ic_input_delete, "Stop scan", pendingIntent);
        startForeground(1002, builder.build());
    }
}
