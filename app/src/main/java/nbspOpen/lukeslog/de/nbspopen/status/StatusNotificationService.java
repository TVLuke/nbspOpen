package nbspOpen.lukeslog.de.nbspopen.status;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import nbspOpen.lukeslog.de.nbspopen.model.Item;
import nbspOpen.lukeslog.de.nbspopen.R;
import nbspOpen.lukeslog.de.nbspopen.activities.MyActivity;

public class StatusNotificationService extends Service {

    private static final int COLOR_GREEN = 0x00FF00;
    private static final int COLOR_RED = 0xFF0000;

    private static final int DURATION_HALF_SECOND = 500;
    private static final int DURATION_ONE_SECOND = 1000;

    private static final String DATE_PATTERN_IN = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static final String DATE_PATTERN_OUT = "dd.MM.yyy HH:mm";

    private static final String STATE_OPEN = "open";
    private static final String STATE_CLOSED="closed";

    private static final int NOTIFICATION_ID = 97542;

    private Updater updater;
    private Context ctx;
    private String lastChangeDate="";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ctx=this;
        startUpdater();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopUpdater();
        super.onDestroy();
    }

    private void startUpdater() {
        updater = new Updater();
        updater.run();
    }

    private void stopUpdater() {

        if (updater != null) {
            updater.onPause();
            updater = null;
        }
    }

    private class Updater implements Runnable {

        private Handler handler = new Handler();
        public static final int delay = DURATION_ONE_SECOND;
        long counter = 0;

        @Override
        public void run() {
            RssReader rssReader = new RssReader();

            if(everyTenMinutes() || !rssFeadInitialized(rssReader)) {
                rssReader.createStatusListFromRss();
            }

            if(rssFeadInitialized(rssReader)) {
                updateNotification(rssReader);
            }

            counter++;
            handler.removeCallbacks(this);
            handler.postDelayed(this, delay);
        }

        private boolean rssFeadInitialized(RssReader rssReader) {
            return rssReader.rssFeed != null;
        }

        private boolean everyTenMinutes() {
            return counter % 600 == 0;
        }

        private void updateNotification(RssReader rssReader) {
            if (!newUpdateOnFeed(rssReader)) {

                if(!lastChangeDate.equals("")) {
                    vibrate();
                }

                lastChangeDate= getFirstItemThatIsEitherOpenOrClosed(rssReader).getPubDate();

                int color = selectAppropriateColor(rssReader);

                DateTime lastEvent = lastEventDate(rssReader);

                PendingIntent pi = intentToMyActivity();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(ctx)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle(rssReader.rssFeed.getChannel().getTitle())
                                .setContentText(createNotificationText(rssReader, lastEvent))
                                .setColor(color)
                                .setContentIntent(pi);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }

        private boolean newUpdateOnFeed(RssReader rssReader) {
            return lastChangeDate.equals(getFirstItemThatIsEitherOpenOrClosed(rssReader).getPubDate());
        }

        private String createNotificationText(RssReader rssReader, DateTime lastEvent) {
            return lastEvent.toString(DATE_PATTERN_OUT) + ": " + getFirstItemThatIsEitherOpenOrClosed(rssReader).getTitle();
        }

        private PendingIntent intentToMyActivity() {
            Intent intent=new Intent(ctx, MyActivity.class);
            return PendingIntent.getActivity(ctx, 0, intent, 0);
        }

        private DateTime lastEventDate(RssReader rssReader) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN_IN);
            return formatter.withLocale(Locale.ENGLISH).parseDateTime(getFirstItemThatIsEitherOpenOrClosed(rssReader).getPubDate());
        }

        private int selectAppropriateColor(RssReader rssReader) {
            int color = COLOR_GREEN;

            if(getFirstItemThatIsEitherOpenOrClosed(rssReader).getTitle().equals("closed")) {
                color = COLOR_RED;
            }

            return color;
        }

        public void onPause() {
            handler.removeCallbacks(this);
        }
    }

    private void vibrate() {
        final Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        new Thread(new Runnable() {
            public void run() {
                v.vibrate(DURATION_HALF_SECOND);
            }
        }).start();
    }

    private Item getFirstItemThatIsEitherOpenOrClosed(RssReader rssReader) {
        if(rssReader.rssFeed != null) {
            for(Item item : rssReader.rssFeed.getChannel().getItems()) {
                if(item.getTitle().equals(STATE_OPEN) || item.getTitle().equals(STATE_CLOSED)) {
                    return item;
                }
            }
        }
        return null;
    }
}