package by.mksn.rememberthedictionary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import by.mksn.rememberthedictionary.R;
import by.mksn.rememberthedictionary.model.Phrase;
import by.mksn.rememberthedictionary.model.PhraseStore;

public class PracticeFragment extends Fragment {

    private PhraseStore phraseStore;
    private TextView phraseView;
    private TextView translationView;
    private Phrase currentPhrase;
    private Animation blinkAnimation;

    public static PracticeFragment newInstance(PhraseStore phraseStore) {
        PracticeFragment fragment = new PracticeFragment();
        fragment.phraseStore = phraseStore;
        Bundle bundle = new Bundle();
        //args here
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_practice, container, false);
        phraseView = (TextView) rootView.findViewById(R.id.phraseText);
        translationView = (TextView) rootView.findViewById(R.id.translationText);

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
        blinkAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        phraseView.startAnimation(blinkAnimation);
        loadPhrase();
        return rootView;
    }


    public void loadPhrase() {
        phraseView.startAnimation(blinkAnimation);
        currentPhrase = phraseStore.selectRandom();
        if (currentPhrase != null) {
            if (currentPhrase.getTranslation().isEmpty()) {
                translationView.setVisibility(View.INVISIBLE);
            } else {
                translationView.setVisibility(View.VISIBLE);
                translationView.startAnimation(blinkAnimation);
                translationView.setText(getString(R.string.translation_text_placeholder));
            }
            phraseView.setText(currentPhrase.getPhrase());
        } else {
            translationView.setVisibility(View.INVISIBLE);
            phraseView.setText(R.string.phrase_text_not_found);
        }
    }

}
