package com.soulet.simon.mynews2.controllers.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostPopularFragment extends Fragment {

    //DESIGN
    @BindView(R.id.fragment_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    //DATA
    private Disposable disposable;
    private List<Result> results;
    private ArticleAdapter adapter;

    public MostPopularFragment() {
        // Required empty public constructor
    }

    public static MostPopularFragment newInstance(){
        return (new MostPopularFragment());
    }

    //----------------------------------------------------------------------------------------------
    //                                    OVERRIDE METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_most_popular, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        this.configureSwipeRefreshLayout();
        this.executeGetMostPopularArticlesRequest();
        this.configureOnClickRecyclerView();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    //----------------------------------------------------------------------------------------------
    //                                      CONFIGURATIONS
    //----------------------------------------------------------------------------------------------

    private void configureRecyclerView(){
        this.results = new ArrayList<>();
        this.adapter = new ArticleAdapter(this.results, Glide.with(this), 2);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeGetMostPopularArticlesRequest();
            }
        });
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Result result = adapter.getResult(position);
                        System.out.println(result);
                        result.setRead(true);
                        Intent webviewIntent = new Intent(getContext(), WebViewActivity.class);
                        webviewIntent.putExtra(WebViewActivity.WebContent, result.getUrl());
                        startActivity(webviewIntent);
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    //                            REQUESTS with Retrofit & Rxjava
    //----------------------------------------------------------------------------------------------

    private void executeGetMostPopularArticlesRequest(){
        this.disposable = NYTStreams.getMostPopularArticles().subscribeWith(new DisposableObserver<Articles>() {
            @Override
            public void onNext(Articles articles) {
                System.out.println("next MP");
                updateUI(articles.getResults());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("erreur MP : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("complete MP");
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    //----------------------------------------------------------------------------------------------
    //                                      UPDATE UI
    //----------------------------------------------------------------------------------------------

    private void updateUI(List<Result> results){
        swipeRefreshLayout.setRefreshing(false);
        this.results.clear();
        this.results.addAll(results);
        this.adapter.notifyDataSetChanged();
    }
}