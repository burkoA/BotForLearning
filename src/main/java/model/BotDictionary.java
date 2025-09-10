package model;

import jakarta.persistence.*;

@Entity
@Table(name = "dictionary")
public class BotDictionary {
    @Id
    private Long id;

    @Column(nullable = false)
    private String polishWord;

    @Column(nullable = false)
    private String ukrainianWord;

    public BotDictionary() {};

    public BotDictionary(String polishWord, String ukrainianWord) {
        this.polishWord = polishWord;
        this.ukrainianWord = ukrainianWord;
    }

    public Long getId() {
        return id;
    }

    public String getPolishWord() {
        return polishWord;
    }

    public void setPolishWord(String polishWord) {
        this.polishWord = polishWord;
    }

    public String getUkrainianWord() {
        return ukrainianWord;
    }

    public void setUkrainianWord(String ukrainianWord) {
        this.ukrainianWord = ukrainianWord;
    }
}
