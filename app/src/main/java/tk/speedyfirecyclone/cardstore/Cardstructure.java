package tk.speedyfirecyclone.cardstore;

public class Cardstructure {
    public String cardTitle;
    public String cardData;
    public String favorite;

    public Cardstructure(String title, String data) {
        this.cardTitle = title;
        this.cardData = data;
        this.favorite = title;
    }

    public Cardstructure(String title, String data, String favorite) {
        this.cardTitle = title;
        this.cardData = data;
        this.favorite = favorite;

    }

    public Cardstructure() {
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public String getCardData() {
        return cardData;
    }

    public String getFavorite() {
        return favorite;
    }


}
