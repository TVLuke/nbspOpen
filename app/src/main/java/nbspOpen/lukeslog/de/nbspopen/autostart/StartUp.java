package nbspOpen.lukeslog.de.nbspopen.autostart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nbspOpen.lukeslog.de.nbspopen.status.StatusNotificationService;

public class StartUp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent startIntent = new Intent(context, StatusNotificationService.class);
        context.startService(startIntent);
    }
}
