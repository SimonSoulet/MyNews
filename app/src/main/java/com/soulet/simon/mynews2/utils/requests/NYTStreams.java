package com.soulet.simon.mynews2.utils.requests;

import com.soulet.simon.mynews2.models.Articles;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NYTStreams {

    public static Observable<Articles> getTopStoriesArticles(){
        NYTService nytService = NYTService.retrofit.create(NYTService.class);
        return nytService.getTopStoriesArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<Articles> getMostPopularArticles(){
        NYTService nytService = NYTService.retrofit.create(NYTService.class);
        return nytService.getMostPopularArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<Articles> getInterestArticles(String query){
        NYTService nytService = NYTService.retrofit.create(NYTService.class);
        return nytService.getInterestArticles(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<Articles> getSearchArticles(String query, String beginDate, String endDate){
        NYTService nytService = NYTService.retrofit.create(NYTService.class);
        return nytService.getSearchArticles(query, beginDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
