package com.example.mypet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PetsActivity extends AppCompatActivity {

    public static final String PET_KEY = "pet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pets);

        ArrayList<Pet[]> pets = new ArrayList<>();

        pets.add(new Pet[]{new Pet.Cat(), new Pet.Dog()});
        pets.add(new Pet[]{new Pet.Hamster(), new Pet.Parrot()});
        pets.add(new Pet[]{new Pet.Goldfish(), new Pet.Snail()});
        pets.add(new Pet[]{new Pet.Chinchilla(), new Pet.Turtle()});

        ((ListView) findViewById(R.id.pets_LV)).setAdapter(new PetAdapter(this, pets));
    }

    private class PetAdapter extends ArrayAdapter<Pet[]> {

        public PetAdapter(Context context, ArrayList<Pet[]> pets) {
            super(context, R.layout.two_pets_item, pets);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Pet[] pets = getItem(position);

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.two_pets_item, null);

            ImageButton petBtn1 = convertView.findViewById(R.id.pet_1_IB),
                    petBtn2 = convertView.findViewById(R.id.pet_2_IB);

            petBtn1.setBackgroundResource(pets[0].buttonImageResource);
            petBtn2.setBackgroundResource(pets[1].buttonImageResource);

            final Intent petInfo = new Intent(PetsActivity.this, PetInfoActivity.class);
            petBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pets[0].imageResource == Pet.DEFAULT_RESOURCE_VALUE) return;

                    petInfo.putExtra(PET_KEY, new Gson().toJson(pets[0]));

                    startActivity(petInfo);
                }
            });
            petBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pets[1].imageResource == Pet.DEFAULT_RESOURCE_VALUE) return;

                    petInfo.putExtra(PET_KEY, new Gson().toJson(pets[1]));

                    startActivity(petInfo);
                }
            });

            return convertView;
        }
    }
}