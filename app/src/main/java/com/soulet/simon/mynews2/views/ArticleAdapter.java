package com.soulet.simon.mynews2.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.models.Result;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    //DATA
    private List<Result> results;
    private RequestManager glide;
    private int type;
    public static final int type1 = 1;
    public static final int type2 = 2;

    //CONSTRUCTOR
    public ArticleAdapter(List<Result> results, RequestManager glide, int type){
        this.results = results;
        this.glide = glide;
        this.type = type;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.fragment_item, parent,false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        if(type == 1) {
            holder.updateUITopStories(this.results.get(position), this.glide);
        }
        if(type == 2){
            holder.updateUIMostPopular(this.results.get(position), this.glide);
        }
    }

    @Override
    public int getItemCount() {
        return this.results.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public Result getResult(int position){
        return this.results.get(position);
    }
}
