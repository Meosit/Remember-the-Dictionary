package by.mksn.rememberthedictionary.model;

import android.content.Context;

public final class PhraseStoreFactory {

    public static PhraseStore getStore(Context context) {
        return new DatabaseHelper(context);
    }

}
