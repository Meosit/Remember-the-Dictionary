package by.mksn.rememberthedictionary.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper implements PhraseStore {


    private static final String DATABASE_NAME = "dictionary";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_DICTIONARY = "dictionary";
    private static final String KEY_PHRASE = "phrase";
    private static final String KEY_TRANSLATION = "translation";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDictionaryTableQuery = "" +
                "CREATE TABLE " + TABLE_DICTIONARY + "(" +
                KEY_PHRASE + " TEXT PRIMARY KEY NOT NULL, " +
                KEY_TRANSLATION + " TEXT NOT NULL" +
                ")";

        db.execSQL(createDictionaryTableQuery);
        populateDictionary(db);
    }

    private void populateDictionary(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(KEY_PHRASE, "to get up");
        values.put(KEY_TRANSLATION, "");
        db.insert(TABLE_DICTIONARY, null, values);
        values = new ContentValues();
        values.put(KEY_PHRASE, "a ticket for 1 May / a concert");
        values.put(KEY_TRANSLATION, "билет на...");
        db.insert(TABLE_DICTIONARY, null, values);
        values = new ContentValues();
        values.put(KEY_PHRASE, "to dream of smth vs to dream about smth");
        values.put(KEY_TRANSLATION, "мечтать о чём-то vs видеть сон о чём-то");
        db.insert(TABLE_DICTIONARY, null, values);
        values = new ContentValues();
        values.put(KEY_PHRASE, "to get to work = to reach work");
        values.put(KEY_TRANSLATION, "добираться до работы");
        db.insert(TABLE_DICTIONARY, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
        onCreate(sqLiteDatabase);
    }

    @Override
    public Phrase selectRandom() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectRandomQuery = "SELECT " +
                KEY_PHRASE + ", " + KEY_TRANSLATION +
                " FROM " + TABLE_DICTIONARY +
                " ORDER BY RANDOM() LIMIT 1";
        try (Cursor cursor = db.rawQuery(selectRandomQuery, null)) {
            if (cursor.moveToFirst()) {
                Phrase phrase = new Phrase();
                phrase.setPhrase(cursor.getString(0));
                phrase.setTranslation(cursor.getString(1));
                return phrase;
            }
        }
        return null;
    }

    @Override
    public List<Phrase> selectAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectRandomQuery = "SELECT " +
                KEY_PHRASE + ", " + KEY_TRANSLATION +
                " FROM " + TABLE_DICTIONARY +
                " ORDER BY " + KEY_PHRASE;
        List<Phrase> phrases = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(selectRandomQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Phrase phrase = new Phrase();
                    phrase.setPhrase(cursor.getString(0));
                    phrase.setTranslation(cursor.getString(1));
                    phrases.add(phrase);
                } while (cursor.moveToNext());
            }
        }
        return phrases;
    }

    @Override
    public void insert(Phrase phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHRASE, phrase.getPhrase());
        values.put(KEY_TRANSLATION, phrase.getTranslation());

        db.insertWithOnConflict(TABLE_DICTIONARY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void insertAll(Iterable<Phrase> phrases) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Phrase phrase : phrases) {
            ContentValues values = new ContentValues();
            values.put(KEY_PHRASE, phrase.getPhrase());
            values.put(KEY_TRANSLATION, phrase.getTranslation());

            db.insertWithOnConflict(TABLE_DICTIONARY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    @Override
    public void delete(String phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DICTIONARY, KEY_PHRASE + " = ?", new String[]{phrase});
    }
}
