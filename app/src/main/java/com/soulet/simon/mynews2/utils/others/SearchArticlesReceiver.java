package com.soulet.simon.mynews2.utils.others;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.controllers.activities.NotifsActivity;
import com.soulet.simon.mynews2.controllers.activities.SearchResultActivity;
import com.soulet.simon.mynews2.models.Articles;
import com.soulet.simon.mynews2.utils.requests.NYTStreams;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class SearchArticlesReceiver extends BroadcastReceiver {

    private SharedPreferences preferences;
    public static final String PREF_KEY_TITLE = "PREF_KEY_TITLE";
    private String query;
    private int numberNewArticles;

    private Disposable disposable;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("TAG", "receiver");
        System.out.println("RECEIVER");
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        query = preferences.getString(NotifsActivity.PREF_KEY_QUERY, "");
        this.isChecked(preferences.getBoolean(NotifsActivity.PREF_KEY_ART,false), "art");
        this.isChecked(preferences.getBoolean(NotifsActivity.PREF_KEY_BUSINESS,false), "business");
        this.isChecked(preferences.getBoolean(NotifsActivity.PREF_KEY_ENTREPRENEURS,false), "entrepreneurs");
        this.isChecked(preferences.getBoolean(NotifsActivity.PREF_KEY_POLITICS,false), "politics");
        this.isChecked(preferences.getBoolean(NotifsActivity.PREF_KEY_SPORTS,false), "sports");
        this.isChecked(preferences.getBoolean(NotifsActivity.PREF_KEY_TRAVEL,false), "travel");

        this.disposable = NYTStreams.getInterestArticles(query).subscribeWith(new DisposableObserver<Articles>() {
            @Override
            public void onNext(Articles articles) {
                System.out.println("next RECEIVER");
                String titleArticles[] = new String[10];
                for(int i = 0; i < articles.getResponse().getDocs().size(); i++){
                    titleArticles[i] = articles.getResponse().getDocs().get(i).getHeadline().getMain();
                }
                for(int i = 0; i < 10; i++){
                    System.out.println("titre à l'emplacement "+i+" = "+titleArticles[i]);
                }
                String lastTitle = preferences.getString(PREF_KEY_TITLE, "");
                System.out.println("last title = "+lastTitle);
                if(!lastTitle.equals("")){
                    for(int i = 0; i < 10; i++){
                        if(titleArticles[i].equals(lastTitle)){
                            numberNewArticles = i;
                            break;
                        }else{
                            numberNewArticles = 10;
                        }
                    }
                }else{
                    numberNewArticles = 10;
                }
                sendNotification(context);
                preferences.edit().putString(PREF_KEY_TITLE, titleArticles[0]).apply();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("erreur RECEIVER : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("complete RECEIVER");
            }
        });

    }

    private void isChecked(boolean preferences, String category){
        if(preferences){
            query += " "+category;
        }
    }

    private void sendNotification(Context context){
        //if(numberNewArticles > 0) {
        Intent searchResultIntent = new Intent(context, SearchResultActivity.class);
        searchResultIntent.putExtra(SearchResultActivity.QueryTerm, query);
        searchResultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, searchResultIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New articles available")
                .setContentText("There is " + numberNewArticles + " new articles available for your search")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
        //}

    }
}
