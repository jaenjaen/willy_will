package com.willy.will.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.willy.will.R;
import com.willy.will.detail.controller.DetailController;
import com.willy.will.detail.view.DetailActivity;
import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {

    private DetailController detailCtrl;

    public AlarmReceiver(){
        detailCtrl = new DetailController();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, DetailActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingI = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelId");


        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            String channelName = "매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channelId", channelName, importance);
            channel.setDescription(description);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        } else{
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        List<String> list = detailCtrl.AlarmToDoItems();
        for(int i=0;i<list.size();i++){
            builder.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("{Time to watch some cool stuff!}")
                    .setContentTitle("오늘의 할일")
                    .setContentText(list.get(i))
                    .setContentInfo("INFO")
                    .setContentIntent(pendingI);

            if (notificationManager != null) {
                notificationManager.notify(i, builder.build());
            }
        }
    }

}
