package com.soulet.simon.mynews2.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.models.Doc;

import java.util.List;

public class ArticleAdapterBis extends RecyclerView.Adapter<ArticleViewHolder> {

    //DATA
    private List<Doc> docs;
    private RequestManager glide;

    //CONSTRUCTOR
    public ArticleAdapterBis(List<Doc> docs, RequestManager glide){
        this.docs = docs;
        this.glide = glide;
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
        holder.updateUIInterest(this.docs.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return this.docs.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public Doc getDocs(int position){
        return this.docs.get(position);
    }
}
