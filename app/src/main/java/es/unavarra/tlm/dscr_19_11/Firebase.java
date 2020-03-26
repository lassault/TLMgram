package es.unavarra.tlm.dscr_19_11;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.loopj.android.http.SyncHttpClient;

import org.greenrobot.greendao.database.Database;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.StringEntity;
import es.unavarra.tlm.dscr_19_11.Activities.ChatMessagesActivity;
import es.unavarra.tlm.dscr_19_11.Activities.ChatsListActivity;
import es.unavarra.tlm.dscr_19_11.Entities.Access;
import es.unavarra.tlm.dscr_19_11.Entities.AccessDao;
import es.unavarra.tlm.dscr_19_11.Entities.DaoMaster;
import es.unavarra.tlm.dscr_19_11.Entities.DaoSession;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.Notification;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.NotificationTokenObject;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.NotificationTokenResponseHandler;

public class Firebase extends FirebaseMessagingService {
    private String TAG;
    private ChatsListActivity chatsListActivity;
    private ChatMessagesActivity chatMessagesActivity;

    public Firebase() {
        this.TAG = "firebase";
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Gson gson = new Gson();

        JSONObject notificationJSON = new JSONObject(remoteMessage.getData());  // Fields aren't "quoted", so it fails to do it properly

        final Notification notification = gson.fromJson(notificationJSON.toString(), Notification.class);

        if ( ((Chat) getApplicationContext()).getCurrentActivity() != null ) {
            Log.d("firebase", ((Chat) getApplicationContext()).getCurrentActivity().getComponentName().toString());
            final String chatsListActivityString = "ComponentInfo{es.unavarra.tlm.dscr_19_11/es.unavarra.tlm.dscr_19_11.Activities.ChatsListActivity}";

            Activity currentActivity = ((Chat) getApplicationContext()).getCurrentActivity();
            Handler handler = new Handler(Looper.getMainLooper());

            if (currentActivity.getComponentName().toString().equals(chatsListActivityString)) {
                chatsListActivity = (ChatsListActivity) currentActivity;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), String.format("%1$s: %2$s", notification.getSender(), notification.getMessage()), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 10);
                        toast.show();

                        meterDB(notification);
                        //chatsListActivity.getChats(); -> Set something to update the lastMsg

                        View chatsList = chatsListActivity.findViewById(R.id.chatsListLayout).getRootView().findViewById(R.id.chatsList);

                        TextView numOfMsg = chatsList.findViewById(notification.getChat()).findViewById(R.id.mensajesNoLeidos);
                        numOfMsg.setText(String.format(Locale.getDefault(),"Mensajes no leidos: %1$1d", Integer.valueOf(numOfMsg.getText().toString().substring(numOfMsg.getText().toString().length() - 1)) + 1));

                        TextView lastMsg = chatsList.findViewById(notification.getChat()).findViewById(R.id.chatLastMessage);
                        lastMsg.setText(String.format(Locale.getDefault(), "Last msg: %1$s", notification.getMessage()));

                        chatsListActivity.findViewById(R.id.chatsListLayout).getRootView().findViewById(R.id.chatsList).findViewById(notification.getChat()).setBackgroundColor(getColor(R.color.colorPrimary));
                    }
                });
            }

            String chatsMessagesActivityString = "ComponentInfo{es.unavarra.tlm.dscr_19_11/es.unavarra.tlm.dscr_19_11.Activities.ChatMessagesActivity}"; // -> Toast en la parte superior de la pantalla e imagen verde

            if (currentActivity.getComponentName().toString().equals(chatsMessagesActivityString)) {
                chatMessagesActivity = (ChatMessagesActivity) currentActivity;
                if (chatMessagesActivity.getChat().getId() == notification.getChat()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chatMessagesActivity.getMessages();
                            meterDBdesdeChat(notification);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), String.format("%1$s: %2$s", notification.getSender(), notification.getMessage()), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 10);
                            toast.show();
                        }
                    });
                }
            }
        } else {
            sendNotification(notification);
        }
        // When logout, we should regenerate token
    }
public void meterDB(Notification notification){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(chatsListActivity, "access_db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        AccessDao accessDao = daoSession.getAccessDao();

        List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(notification.getChat())).list();

        list.get(0).setLast_msg(notification.getMessage());
        list.get(0).setNew_num(list.get(0).getNew_num()+1);



        accessDao.update(list.get(0));


    }
    public void meterDBdesdeChat(Notification notification){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(chatMessagesActivity, "access_db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        AccessDao accessDao = daoSession.getAccessDao();

        List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(notification.getChat())).list();

        list.get(0).setLast_msg(notification.getMessage());
        list.get(0).setNew_num(list.get(0).getNew_num()+1);
        list.get(0).setLast_num(list.get(0).getLast_num()+1);



        accessDao.update(list.get(0));


    }




    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String firebaseToken) {
        Log.d(TAG, "Refreshed token: " + firebaseToken);

        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);

        while (token == null) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);
        }

        sendRegistrationToServer(firebaseToken, token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer (String firebaseToken, String token) {
        Gson gson =  new Gson();
        NotificationTokenObject firebaseTokenObject = new NotificationTokenObject(firebaseToken);

        String notificationToken = gson.toJson(firebaseTokenObject);

        SyncHttpClient client = new SyncHttpClient();

        Log.d("firebase", token + ": " + notificationToken);

        try {
            client.addHeader("X-AUTH-TOKEN", token);
            client.post(
                    this,
                    "https://api.messenger.tatai.es/v3/notification/token",
                    new StringEntity(notificationToken),
                    "application/json",
                    new NotificationTokenResponseHandler(gson)
            );
            Log.d("firebase", "sended-2");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void sendNotification(Notification notification) {
        Intent intent = new Intent(this, ChatsListActivity.class); // Change this, should open the chat of the notification
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.chat)
                        .setContentTitle(notification.getSender())
                        .setContentText(notification.getMessage())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
