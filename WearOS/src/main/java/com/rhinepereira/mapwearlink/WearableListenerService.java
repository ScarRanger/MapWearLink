package com.rhinepereira.mapwearlink;

import android.content.Intent;
import android.net.Uri;
import com.google.android.gms.wearable.MessageEvent;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService{





    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
