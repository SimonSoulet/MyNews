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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.controllers.activities.WebViewActivity;
import com.soulet.simon.mynews2.models.Articles;
import com.soulet.simon.mynews2.models.Doc;
import com.soulet.simon.mynews2.models.Response;
import com.soulet.simon.mynews2.models.Result;
import com.soulet.simon.mynews2.utils.others.ItemClickSupport;
import com.soulet.simon.mynews2.utils.requests.NYTStreams;
import com.soulet.simon.mynews2.views.ArticleAdapter;
import com.soulet.simon.mynews2.views.ArticleAdapterBis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class InterestFragment extends Fragment {

    //DESIGN
    @BindView(R.id.fragment_recycler_view) RecyclerView recyclerView;
    //@BindView(R.id.fragment_swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fragment_interest_spinner) Spinner spinner;

    //DATA
    private Disposable disposable;
    private List<Doc> docs;
    private ArticleAdapterBis adapter;

    public InterestFragment() {
        // Required empty public constructor
    }

    public static InterestFragment newInstance(){
        return (new InterestFragment());
    }

    //----------------------------------------------------------------------------------------------
    //                                    OVERRIDE METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interest, container, false);
        ButterKnife.bind(this, view);
        this.configureSpinner();
        this.configureRecyclerView();
        //this.configureSwipeRefreshLayout();
        this.executeGetInterestArticlesRequest();
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

    private void configureSpinner(){
        List list =  new ArrayList();
        list.add("Art");
        list.add("Business");
        list.add("Entrepreneurs");
        list.add("Politics");
        list.add("Sports");
        list.add("Travel");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.category));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private void configureRecyclerView(){
        this.docs = new ArrayList<>();
        this.adapter = new ArticleAdapterBis(this.docs, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /*private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeGetInterestArticlesRequest();
            }
        });
    }*/

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Doc docs = adapter.getDocs(position);
                        System.out.println(docs);
                        docs.setRead(true);
                        Intent webviewIntent = new Intent(getContext(), WebViewActivity.class);
                        webviewIntent.putExtra(WebViewActivity.WebContent, docs.getWebUrl());
                        startActivity(webviewIntent);
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    //                            REQUESTS with Retrofit & Rxjava
    //----------------------------------------------------------------------------------------------

    private void executeGetInterestArticlesRequest(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disposable = NYTStreams.getInterestArticles(parent.getSelectedItem().toString()).subscribeWith(new DisposableObserver<Articles>() {
                    @Override
                    public void onNext(Articles articles) {
                        System.out.println("next I");
                        updateUI(articles.getResponse().getDocs());
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("erreur I : " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("complete I");
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    //----------------------------------------------------------------------------------------------
    //                                      UPDATE UI
    //----------------------------------------------------------------------------------------------

    private void updateUI(List<Doc> docs){
        //swipeRefreshLayout.setRefreshing(false);
        this.docs.clear();
        this.docs.addAll(docs);
        this.adapter.notifyDataSetChanged();
    }
}
