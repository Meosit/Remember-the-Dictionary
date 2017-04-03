package by.mksn.rememberthedictionary.model;


import java.util.List;

public interface PhraseStore {

    Phrase selectRandom();

    List<Phrase> selectAll();

    void insert(Phrase phrase);

    void insertAll(Iterable<Phrase> phrases);

    void delete(String phrase);


}
