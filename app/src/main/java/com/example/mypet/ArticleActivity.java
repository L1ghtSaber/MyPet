package com.example.mypet;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_article);

        Pet.Article article = new Gson().fromJson(getIntent().getStringExtra(PetInfoActivity.ARTICLE_KEY), Pet.Article.class);

        ((LinearLayout) findViewById(R.id.article_title_background_LL))
                .setBackgroundColor(Color.argb(64, article.RGB_themeColor[0], article.RGB_themeColor[1], article.RGB_themeColor[2]));

        ((TextView) findViewById(R.id.article_title_TV)).setText(article.title);
        ((TextView) findViewById(R.id.article_text_TV)).setText(article.text);
        ((ImageView) findViewById(R.id.article_image_IV)).setBackgroundResource(article.imageResource);
    }
}