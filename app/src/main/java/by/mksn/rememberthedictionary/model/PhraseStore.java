package by.mksn.rememberthedictionary.model;


public interface PhraseStore {

    Phrase selectRandom();

    void insert(Phrase phrase);

    void insertAll(Iterable<Phrase> phrases);

    void delete(String phrase);


}
