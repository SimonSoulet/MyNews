package com.soulet.simon.mynews2.utils.requests;

import com.soulet.simon.mynews2.models.Articles;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NYTService {

    String api_key = "api-key=8bfb96ec61c7449cb360fa023004f38b";

    @GET("topstories/v2/home.json?"+api_key)
    Observable<Articles> getTopStoriesArticles();

    @GET("mostpopular/v2/mostviewed/all-sections/7.json?"+api_key)
    Observable<Articles> getMostPopularArticles();

    @GET("search/v2/articlesearch.json?"+api_key)
    Observable<Articles> getInterestArticles(@Query("q") String query);

    @GET("search/v2/articlesearch.json?"+api_key)
    Observable<Articles> getSearchArticles(@Query("q") String query, @Query("begin_date") String beginDate, @Query("end_date") String endDate);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
