package com.example.mypet;

import java.util.ArrayList;

public class Pet {

    public static final int DEFAULT_RESOURCE_VALUE = -1;

    public String name;
    public int buttonImageResource = DEFAULT_RESOURCE_VALUE, // картинка при выборе животных
            imageResource = DEFAULT_RESOURCE_VALUE; // картинка сверху в активнсоти со статьями
    public final int[] RGB_themeColor = new int[3]; // цвет текста, кружков у статей и т.д.
    public ArrayList<Article> articles = new ArrayList<>();

    public Pet() {}

    public static class Article {

        public String name, // имя в меню со статьями
                title, // заголовок в самой статье
                text;
        public int imageResource;
        public final int[] RGB_themeColor;

        public Article(String name, String title, String text, int imageResource, Pet pet) {
            this.name = name;
            this.title = title;
            this.text = text;
            this.imageResource = imageResource;
            RGB_themeColor = pet.RGB_themeColor;
        }
    }

    public static class Cat extends Pet {

        public Cat() {
            name = "Котята";
            buttonImageResource = R.drawable.two_kittens_in_garden;
            imageResource = R.drawable.four_kittens_with_flowers;
            // 168, 76, 199
            RGB_themeColor[0] = 168;
            RGB_themeColor[1] = 76;
            RGB_themeColor[2] = 199;

            articles.add(new Article("Чем кормить котенка?", "", "", DEFAULT_RESOURCE_VALUE, this));
            articles.add(new Article("Поход к ветеринару", "", "", DEFAULT_RESOURCE_VALUE, this));
            articles.add(new Article("Игры с котенком", "Шарик в лабиринте",
                    "Теннисный шарик можно поместить в упаковку для яиц, содержащую несколько " +
                            "десятков ячеек, и перекатывать его из одной в другую. Кошка будет " +
                            "пытаться его достать, и эта игра позабавит вас обоих!",
                    R.drawable.kitten_plays_with_balls_of_thread, this));
            articles.add(new Article("Как воспитать идеального котика?", "", "", DEFAULT_RESOURCE_VALUE, this));
        }
    }

    public static class Dog extends Pet {

        public Dog() {
            buttonImageResource = R.drawable.dog_with_flowers;
        }
    }

    public static class Hamster extends Pet {

        public Hamster() {
            buttonImageResource = R.drawable.hamster_eats_seeds;
        }
    }

    public static class Parrot extends Pet {

        public Parrot() {
            buttonImageResource = R.drawable.green_parrot_on_lilac;
        }
    }

    public static class Goldfish extends Pet {

        public Goldfish() {
            buttonImageResource = R.drawable.goldfish_in_aquarium;
        }
    }

    public static class Snail extends Pet {

        public Snail() {
            buttonImageResource = R.drawable.snail_on_pink_flower;
        }
    }

    public static class Chinchilla extends Pet {

        public Chinchilla() {
            buttonImageResource = R.drawable.gray_chincilla_on_sofa;
        }
    }

    public static class Turtle extends Pet {

        public Turtle() {
            buttonImageResource = R.drawable.sea_turtle_swims_in_ocean;
        }
    }
}
