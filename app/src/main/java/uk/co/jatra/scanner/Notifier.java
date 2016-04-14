package uk.co.jatra.scanner;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by tim on 13/04/2016.
 */
public class Notifier {

    public static void notify(Context context, String id, int count) {
        NotificationManagerCompat mgr=
                NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setNumber(count)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle(id+" "+Integer.toString(count))
                        .setContentText("Hello. I'm a notification");
        mgr.notify(count+100, builder.build());
    }
}
