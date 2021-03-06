package by.mksn.rememberthedictionary.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Callable;

import by.mksn.rememberthedictionary.R;
import by.mksn.rememberthedictionary.model.Phrase;
import by.mksn.rememberthedictionary.model.PhraseStore;

public final class ActivityUtil {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void showAddSingleDialog(final Activity activity,
                                           final FloatingActionButton fab,
                                           final PhraseStore phraseStore,
                                           final Phrase phraseToUpdate,
                                           final Callable<Void> notifyCallback) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.add_single_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText phrase = (EditText) dialogView.findViewById(R.id.phraseEdit);
        final EditText translation = (EditText) dialogView.findViewById(R.id.translationEdit);

        if (phraseToUpdate != null) {
            phrase.setEnabled(false);
            phrase.setText(phraseToUpdate.getPhrase());
            translation.setText(phraseToUpdate.getTranslation());
            translation.requestFocus();
            dialogBuilder.setTitle(R.string.menu_update_single_dialog_title);
            dialogBuilder.setMessage(R.string.menu_update_single_dialog_message);
            dialogBuilder.setPositiveButton(R.string.menu_update_single_dialog_positive, null);
            dialogBuilder.setNegativeButton(R.string.menu_update_single_dialog_negative, null);
        } else {
            phrase.requestFocus();
            dialogBuilder.setTitle(R.string.menu_add_single_dialog_title);
            dialogBuilder.setMessage(R.string.menu_add_single_dialog_message);
            dialogBuilder.setPositiveButton(R.string.menu_add_single_dialog_positive, null);
            dialogBuilder.setNegativeButton(R.string.menu_add_single_dialog_negative, null);
        }


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
                            if (notifyCallback != null) {
                                try {
                                    notifyCallback.call();
                                } catch (Exception ignored) {
                                }
                            }
                            dialog.dismiss();
                            if (fab != null) {
                                Snackbar.make(fab, R.string.menu_add_single_success_message, Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(
                                    activity,
                                    R.string.menu_add_single_dialog_constraint_phrase_message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }
                });

            }
        });
        dialog.show();
    }

    public static void showRemoveSingleDialog(final Activity activity,
                                              final FloatingActionButton fab,
                                              final PhraseStore phraseStore,
                                              final Callable<Void> notifyCallback) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.remove_single_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText phrase = (EditText) dialogView.findViewById(R.id.removeEdit);

        dialogBuilder.setTitle(R.string.menu_remove_single_dialog_title);
        dialogBuilder.setMessage(R.string.menu_remove_single_dialog_message);
        dialogBuilder.setPositiveButton(R.string.menu_remove_single_dialog_positive, null);
        dialogBuilder.setNegativeButton(R.string.menu_remove_single_dialog_negative, null);
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
                            try {
                                notifyCallback.call();
                            } catch (Exception ignored) {
                            }
                            dialog.dismiss();
                            Snackbar.make(fab, R.string.menu_remove_single_success_message, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(
                                    activity,
                                    R.string.menu_remove_single_dialog_constraint_phrase_message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }
                });

            }
        });
        dialog.show();
    }


}
