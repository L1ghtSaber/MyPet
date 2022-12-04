package com.example.mypet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PetInfoActivity extends AppCompatActivity {

    public static final String KEY_ARTICLE = "article";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pet_info);

        Pet pet = new Gson().fromJson(getIntent().getStringExtra(PetsActivity.KEY_PET), Pet.class);

        ((TextView) findViewById(R.id.pet_name_title_TV)).setText(pet.name);
        ((ImageView) findViewById(R.id.pet_image_IV)).setBackgroundResource(pet.imageResource);

        TextView name = findViewById(R.id.pet_name_TV);
        name.setTextColor(Color.rgb(pet.RGB_themeColor[0], pet.RGB_themeColor[1], pet.RGB_themeColor[2]));
        name.setText(pet.name);

        ((ListView) findViewById(R.id.pet_articles_LV)).setAdapter(new ArticleAdapter(this, pet.articles));
    }

    private class ArticleAdapter extends ArrayAdapter<Pet.Article> {

        public ArticleAdapter(Context context, ArrayList<Pet.Article> articles) {
            super(context, R.layout.article_item, articles);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Pet.Article article = getItem(position);

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_item, null);

            // смена цвета кружка
            ImageView articleCircle = convertView.findViewById(R.id.article_circle_IV);
            GradientDrawable circle = (GradientDrawable) articleCircle.getBackground();
            circle.setColor(Color.rgb(article.RGB_themeColor[0], article.RGB_themeColor[1], article.RGB_themeColor[2]));
            articleCircle.setBackground(circle);

            ((TextView) convertView.findViewById(R.id.article_name_TV)).setText(article.name);

            final Intent articleActivity = new Intent(PetInfoActivity.this, ArticleActivity.class);
            ((ImageButton) convertView.findViewById(R.id.article_button_IB)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (article.imageResource == Pet.DEFAULT_RESOURCE_VALUE) return;

                    articleActivity.putExtra(KEY_ARTICLE, new Gson().toJson(article));

                    startActivity(articleActivity);
                }
            });

            return convertView;
        }
    }
}