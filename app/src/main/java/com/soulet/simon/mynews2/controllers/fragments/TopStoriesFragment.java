package com.soulet.simon.mynews2.controllers.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.controllers.activities.WebViewActivity;
import com.soulet.simon.mynews2.models.Articles;
import com.soulet.simon.mynews2.models.Result;
import com.soulet.simon.mynews2.utils.others.ItemClickSupport;
import com.soulet.simon.mynews2.utils.requests.NYTStreams;
import com.soulet.simon.mynews2.views.ArticleAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopStoriesFragment extends Fragment {

    //DESIGN
    @BindView(R.id.fragment_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    //DATA
    private Disposable disposable;
    private List<Result> results;
    private ArticleAdapter adapter;
    private SharedPreferences preferences;
    //private Set<String> listReadArticles = new HashSet<>();
    public static final String PREF_KEY_TOPSTORIES = "PREF_KEY_TOPSTORIES";

    public TopStoriesFragment() {
        // Required empty public constructor
    }

    public static TopStoriesFragment newInstance(){
        return (new TopStoriesFragment());
    }

    //----------------------------------------------------------------------------------------------
    //                                    OVERRIDE METHODS
    //----------------------------------------------------------------------------------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_stories, container, false);
        ButterKnife.bind(this, view);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //listReadArticles = preferences.getStringSet(PREF_KEY_TOPSTORIES, null);
        this.configureRecyclerView();
        this.configureSwipeRefreshLayout();
        this.executeGetTopStoriesArticlesRequest();
        this.configureOnClickRecyclerView();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.adapter.notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------
    //                                      CONFIGURATIONS
    //----------------------------------------------------------------------------------------------

    private void configureRecyclerView(){
        this.results = new ArrayList<>();
        this.adapter = new ArticleAdapter(this.results, Glide.with(this), 1);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeGetTopStoriesArticlesRequest();
            }
        });
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Result result = adapter.getResult(position);
                        result.setRead(true);
                        Set<String> oldList = preferences.getStringSet(PREF_KEY_TOPSTORIES, null);
                        Set<String> newList = new HashSet<>(oldList);
                        newList.add(String.valueOf(oldList.size()));
                        newList.add(result.getTitle());
                        System.out.println(newList);
                        for(String string : newList){
                            System.out.println(string);
                        }
                        preferences.edit().putStringSet(PREF_KEY_TOPSTORIES, newList).apply();
                        //SharedPreferences.Editor editor = preferences.edit();
                        //editor.putStringSet(PREF_KEY_TOPSTORIES, listReadArticles);
                        //editor.apply();
                        //editor.commit();
                        System.out.println(result.getRead());
                        Intent webviewIntent = new Intent(getContext(), WebViewActivity.class);
                        webviewIntent.putExtra(WebViewActivity.WebContent, result.getUrl());
                        startActivity(webviewIntent);
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    //                            REQUESTS with Retrofit & Rxjava
    //----------------------------------------------------------------------------------------------

    private void executeGetTopStoriesArticlesRequest(){
        this.disposable = NYTStreams.getTopStoriesArticles().subscribeWith(new DisposableObserver<Articles>() {
            @Override
            public void onNext(Articles articles) {
                System.out.println("next TS");
                /*System.out.println(articles.getNumResults());
                System.out.println(articles.getResults());
                StringBuilder stringBuilder = new StringBuilder();
                for(Articles.Result result : articles.getResults()){
                    stringBuilder.append("-"+result.getTitle()+"\n");
                }
                System.out.println(stringBuilder);*/
                /*StringBuilder stringBuilder = new StringBuilder();
                for(Articles.Result result : articles.getResults()){
                    for(Articles.Result.Multimedium multimedium : result.getMultimedia())
                    stringBuilder.append("-"+multimedium.getUrl()+"\n");
                }
                System.out.println(stringBuilder);*/
                updateUI(articles.getResults());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("erreur TS");
            }

            @Override
            public void onComplete() {
                System.out.println("complete TS");
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    //----------------------------------------------------------------------------------------------
    //                                      UPDATE UI
    //----------------------------------------------------------------------------------------------

    /**
     *
     * @param results
     */
    private void updateUI(List<Result> results){
        swipeRefreshLayout.setRefreshing(false);
        this.results.clear();
        this.results.addAll(results);
        this.adapter.notifyDataSetChanged();
    }
}