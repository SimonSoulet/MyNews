package com.soulet.simon.mynews2.controllers.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.models.Articles;
import com.soulet.simon.mynews2.models.Doc;
import com.soulet.simon.mynews2.utils.others.ItemClickSupport;
import com.soulet.simon.mynews2.utils.requests.NYTStreams;
import com.soulet.simon.mynews2.views.ArticleAdapterBis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class SearchResultActivity extends AppCompatActivity {

    //DESIGN
    //@BindView(R.id.fragment_recycler_view) RecyclerView recyclerView;

    //DATA
    private RecyclerView recyclerView;
    private Disposable disposable;
    private List<Doc> docs;
    private ArticleAdapterBis adapter;
    public static String QueryTerm = "QueryTerm";
    public static String BeginDate = "BeginDate";
    public static String EndDate = "EndDate";

    //----------------------------------------------------------------------------------------------
    //                                    OVERRIDE METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        recyclerView = (RecyclerView) findViewById(R.id.fragment_recycler_view);
        this.configureRecyclerView();
        this.executeGetSearchArticlesRequest();
        this.configureOnClickRecyclerView();
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
        this.docs = new ArrayList<>();
        this.adapter = new ArticleAdapterBis(this.docs, Glide.with(SearchResultActivity.this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Doc docs = adapter.getDocs(position);
                        Intent webviewIntent = new Intent(SearchResultActivity.this, WebViewActivity.class);
                        webviewIntent.putExtra(WebViewActivity.WebContent, docs.getWebUrl());
                        startActivity(webviewIntent);
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    //                            REQUESTS with Retrofit & Rxjava
    //----------------------------------------------------------------------------------------------

    private void executeGetSearchArticlesRequest(){
        String queryTerm = getIntent().getStringExtra(QueryTerm);
        String beginDate = getIntent().getStringExtra(BeginDate);
        String endDate = getIntent().getStringExtra(EndDate);
        this.disposable = NYTStreams.getSearchArticles(queryTerm, beginDate, endDate).subscribeWith(new DisposableObserver<Articles>() {
            @Override
            public void onNext(Articles articles) {
                System.out.println("next SA");
                updateUI(articles.getResponse().getDocs());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("erreur SA : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("complete SA");
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
        if(docs.size() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultActivity.this);
            builder.setMessage("No result for your search");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create();
            builder.show();
        }else{
            this.docs.clear();
            this.docs.addAll(docs);
            this.adapter.notifyDataSetChanged();
        }
    }
}
