package com.soulet.simon.mynews2.views;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.controllers.fragments.TopStoriesFragment;
import com.soulet.simon.mynews2.models.Doc;
import com.soulet.simon.mynews2.models.Result;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    private SharedPreferences preferences;
    private Set<String> listReadArticles;

    @BindView(R.id.fragment_item_title) TextView textView;
    @BindView(R.id.fragment_item_image) ImageView imageView;
    @BindView(R.id.fragment_item_layout) LinearLayout linearLayout;

    public ArticleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
    }

    public void updateUITopStories(Result result, RequestManager glide){
        this.textView.setText(result.getTitle());
        if(result.getMultimedia() != null && result.getMultimedia().size() > 0) {
            glide.load(result.getMultimedia().get(0).getUrl()).apply(RequestOptions.centerCropTransform()).into(imageView);
        }else{
            imageView.setImageResource(R.drawable.no_image);
        }
        System.out.println("holder :"+result.getRead());
        isArticleRead(result.getTitle());
    }

    public void updateUIMostPopular(Result result, RequestManager glide){
        this.textView.setText(result.getTitle());
        if(result.getMedia() != null && result.getMedia().size() > 0) {
            if(result.getMedia().get(0).getMediaMetadata() != null && result.getMedia().get(0).getMediaMetadata().size() > 0) {
               glide.load(result.getMedia().get(0).getMediaMetadata().get(0).getUrl()).apply(RequestOptions.centerCropTransform()).into(imageView);
             }else{
               imageView.setImageResource(R.drawable.no_image);
            }
        }else{
           imageView.setImageResource(R.drawable.no_image);
        }
        isArticleRead(result.getTitle());
    }

    public void updateUIInterest(Doc docs, RequestManager glide){
        this.textView.setText(docs.getHeadline().getMain());
        if(docs.getMultimedia() != null && docs.getMultimedia().size() > 0) {
            String endUrl = docs.getMultimedia().get(0).getUrl();
            glide.load("https://static01.nyt.com/"+endUrl).apply(RequestOptions.centerCropTransform()).into(imageView);
        }else{
            imageView.setImageResource(R.drawable.no_image);
        }
        //isArticleRead(docs.get);
    }

    public void isArticleRead(String title){
        listReadArticles = preferences.getStringSet(TopStoriesFragment.PREF_KEY_TOPSTORIES,null);
        if(listReadArticles != null){
            if(listReadArticles.contains(title)){
                linearLayout.setBackgroundColor(itemView.getResources().getColor(R.color.readArticle));
            }else{
                linearLayout.setBackgroundColor(itemView.getResources().getColor(R.color.white));
            }
        }
    }
}
