package by.mksn.rememberthedictionary.model;

import java.util.Objects;

public class Phrase {

    private String phrase;
    private String translation;

    public Phrase(String phrase, String translation) {
        this.phrase = phrase;
        this.translation = translation;
    }

    public Phrase() {
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phrase that = (Phrase) o;
        return Objects.equals(phrase, that.phrase) &&
                Objects.equals(translation, that.translation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phrase, translation);
    }

    @Override
    public String toString() {
        return "Phrase(" +
                "phrase='" + phrase + '\'' +
                ", translation='" + translation + '\'' +
                ')';
    }
}
