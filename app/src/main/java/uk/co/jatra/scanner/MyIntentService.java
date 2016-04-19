package uk.co.jatra.scanner;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.Semaphore;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    private static final String TAG = "MyIntentService";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "uk.co.jatra.scanner.action.FOO";
    private static final String ACTION_BAZ = "uk.co.jatra.scanner.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "uk.co.jatra.scanner.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "uk.co.jatra.scanner.extra.PARAM2";
    public static final String STOP_INTENT_SERVICE = "stop.intent.service";

    private Handler handler;
    private long startTime;
    private Semaphore semaphore;


    public MyIntentService() {
        super("MyIntentService");
        Log.d(TAG, "Constructor on thread "+Thread.currentThread().toString());
        handler = new Handler();
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void start(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        context.stopService(intent);
    }

    private int runCount;
    private Runnable scan = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run() on thread "+Thread.currentThread().toString());
            if (SystemClock.elapsedRealtime() - startTime > 15000) {
                Log.d(TAG, "allowing to finish");
                semaphore.release();
            }
            else {
                Notifier.notify(MyIntentService.this, "IntentService", ++runCount);
                handler.postDelayed(this, 3000);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() on thread "+Thread.currentThread().toString());
        semaphore = new Semaphore(0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() on thread "+Thread.currentThread().toString());
        if (intent.getBooleanExtra(STOP_INTENT_SERVICE, false)) {
            Log.d(TAG, "Stopping: onHandleIntent() called with: " + "intent = [" + intent + "]");
            semaphore.release();
            return;
        }
        startTime = SystemClock.elapsedRealtime();
        handler.removeCallbacks(scan);
        handler.postDelayed(scan, 1000);
        toForeground();
        semaphore.acquireUninterruptibly();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        handler.removeCallbacks(scan);
        super.onDestroy();
    }

    private void toForeground() {
        Intent notificationIntent = new Intent(this, MyIntentService.class);
        notificationIntent.putExtra(STOP_INTENT_SERVICE, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_menu_compass)
                        .setContentTitle("Scanning")
                        .setContentText("Scanning for this that and everything. (Pretending)")
                        .addAction(android.R.drawable.ic_input_delete, "Stop scan", pendingIntent);
        startForeground(1001, builder.build());
    }

}
