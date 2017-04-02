package by.mksn.rememberthedictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import by.mksn.rememberthedictionary.model.Phrase;
import by.mksn.rememberthedictionary.model.PhraseStore;
import by.mksn.rememberthedictionary.model.PhraseStoreFactory;

public class MainActivity extends AppCompatActivity {

    private Animation rotateAnimation;
    private Animation blinkAnimation;
    private PhraseStore phraseStore;
    private TextView phraseView;
    private TextView translationView;
    private Phrase currentPhrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        phraseView = (TextView) findViewById(R.id.phraseText);
        translationView = (TextView) findViewById(R.id.translationText);
        translationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (translationView.getText().equals(getString(R.string.translation_text_placeholder))) {
                    if (currentPhrase != null && !currentPhrase.getTranslation().isEmpty()) {
                        translationView.setText(currentPhrase.getTranslation());
                    }
                } else {
                    translationView.setText(getString(R.string.translation_text_placeholder));
                }
            }
        });
        phraseStore = PhraseStoreFactory.getStore(this);

        loadPhrase();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(rotateAnimation);
                phraseView.startAnimation(blinkAnimation);
                loadPhrase();
            }
        });
    }

    private static final int READ_REQUEST_CODE = 42;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                Log.i("Uri: ", uri.toString());
                try {
                    readExternalDocument(new File(uri.getPath()));
                } catch (CannotReadFileException e) {
                    Toast.makeText(this, "Cannot read this file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add_single:
                showAddSingleDialog();
                return true;
            case R.id.action_add_file:
                performFileSearch();
                return true;
            case R.id.action_remove_single:
                showRemoveSingleDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadPhrase() {
        currentPhrase = phraseStore.selectRandom();
        if (currentPhrase != null) {
            if (currentPhrase.getTranslation().isEmpty()) {
                translationView.setVisibility(View.INVISIBLE);
            } else {
                translationView.setVisibility(View.VISIBLE);
                translationView.setText(getString(R.string.translation_text_placeholder));
            }
            phraseView.setText(currentPhrase.getPhrase());
        } else {
            translationView.setVisibility(View.INVISIBLE);
            phraseView.setText(R.string.phrase_text_not_found);
        }

    }

    private void showRemoveSingleDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.remove_single_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText phrase = (EditText) dialogView.findViewById(R.id.removeEdit);

        dialogBuilder.setTitle("Remove phrase");
        dialogBuilder.setMessage("Type phrase to remove");
        dialogBuilder.setPositiveButton("Add", null);
        dialogBuilder.setNegativeButton("Cancel", null);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!phrase.getText().toString().isEmpty()) {
                            phraseStore.delete(phrase.getText().toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Phrase field must be not empty!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }
                });

            }
        });
        dialog.show();
    }

    private void showAddSingleDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_single_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText phrase = (EditText) dialogView.findViewById(R.id.phraseEdit);
        final EditText translation = (EditText) dialogView.findViewById(R.id.translationEdit);

        dialogBuilder.setTitle("Add new phrase");
        dialogBuilder.setMessage("It will be replaced if it already exists");
        dialogBuilder.setPositiveButton("Add", null);
        dialogBuilder.setNegativeButton("Cancel", null);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!phrase.getText().toString().isEmpty()) {
                            Phrase newPhrase = new Phrase(
                                    phrase.getText().toString(),
                                    translation.getText().toString()
                            );
                            phraseStore.insert(newPhrase);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Phrase field must be not empty!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }
                });

            }
        });
        dialog.show();
    }

    private List<Phrase> readExternalDocument(File file) throws CannotReadFileException {
        List<Phrase> phrases;
        try {
            phrases = new ArrayList<>();
        } catch (Exception e) {
            throw new CannotReadFileException(e);
        }
        return phrases;
    }
}
