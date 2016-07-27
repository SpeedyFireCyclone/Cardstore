package com.speedyfirecyclone.cardstore;

public class Cardstructure {
    String title;
    String data;
    String favorite;

    public Cardstructure(String title, String data) {
        this.title = title;
        this.data = data;
        this.favorite = title;
    }

    public Cardstructure(String title, String data, String favorite) {
        this.title = title;
        this.data = data;
        this.favorite = favorite;

    }

    public Cardstructure() {
    }

    public String getCardTitle() {
        return title;
    }

    public void setCardTitle(String title) {
        this.title = title;
    }

    public String getCardData() {
        return data;
    }

    public void setCardData(String data) {
        this.data = data;
    }

    public String getFavorite() {
        return favorite;
    }


}
