package gr.atc.yds.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import gr.atc.yds.R;
import gr.atc.yds.activities.ProjectActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by ipapas on 08/09/17.
 */

public class NotificationsManager {

    private static NotificationsManager instance = null;
    private Context context;

    private NotificationsManager(){
        context = App.getContext();
    }

    public static NotificationsManager getInstance(){
        if(instance == null)
            instance = new NotificationsManager();

        return instance;
    }

    /**
     * Shows notification when user is near a project
     *
     * @param projectId ID of near project
     * @param projectTitle Title of near project
     */
    public void showCloseProjectNotification(Long projectId, String projectTitle){

        int notificationId = 001;
        String title = App.getContext().getString(R.string.notificationCloseProjectTitle) + " " + projectTitle;
        String text = App.getContext().getString(R.string.notificationCloseProjectText);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        //Open ProjectActivity when notification is clicked
        Intent i = new Intent(App.getContext(), ProjectActivity.class);
        i.putExtra("projectId", projectId);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.img_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Notification notification = mBuilder.build();

        //Issue notification
        notificationManager.notify(notificationId, notification);

    }

}
