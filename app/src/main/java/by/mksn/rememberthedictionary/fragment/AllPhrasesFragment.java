package by.mksn.rememberthedictionary.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import java.util.List;
import java.util.concurrent.Callable;

import by.mksn.rememberthedictionary.R;
import by.mksn.rememberthedictionary.fragment.recycler.ListItemAdapter;
import by.mksn.rememberthedictionary.fragment.recycler.RecyclerTouchListener;
import by.mksn.rememberthedictionary.fragment.recycler.SimpleDividerItemDecoration;
import by.mksn.rememberthedictionary.model.Phrase;
import by.mksn.rememberthedictionary.model.PhraseStore;
import by.mksn.rememberthedictionary.util.ActivityUtil;

public class AllPhrasesFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected ListItemAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private PhraseStore phraseStore;
    private List<Phrase> phrases;
    private FloatingActionButton fab;

    public static AllPhrasesFragment newInstance(PhraseStore phraseStore, FloatingActionButton fab) {
        AllPhrasesFragment fragment = new AllPhrasesFragment();
        fragment.phraseStore = phraseStore;
        fragment.fab = fab;
        fragment.phrases = phraseStore.selectAll();
        Bundle args = new Bundle();
        //args here
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_phrases, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.all_phrases_list_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.scrollToPosition(0);
        mAdapter = new ListItemAdapter(phrases);
        // Set CustomAdapter as the adapter for RecyclerView.
        recyclerView.setAdapter(mAdapter);
        registerForContextMenu(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popup.setGravity(Gravity.END);
                }
                popup.getMenuInflater().inflate(R.menu.list_view_popup, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final Phrase phrase = phrases.get(position);
                        switch (item.getItemId()) {
                            case R.id.action_popup_remove:
                                phraseStore.delete(phrase.getPhrase());
                                notifyDelete(position);
                                Snackbar.make(fab, R.string.menu_remove_single_success_message, Snackbar.LENGTH_SHORT).show();
                                break;
                            case R.id.action_popup_update:
                                ActivityUtil.showAddSingleDialog(getActivity(), fab, phraseStore, phrase, new Callable<Void>() {
                                    @Override
                                    public Void call() throws Exception {
                                        notifyUpdate(position, phrase);
                                        return null;
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        return rootView;
    }

    public void reloadData() {
        phrases.clear();
        phrases.addAll(phraseStore.selectAll());
        mAdapter.notifyItemRangeChanged(0, phrases.size());
    }

    private void notifyDelete(int position) {
        phrases.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private void notifyUpdate(int position, Phrase phrase) {
        phrases.set(position, phrase);
        mAdapter.notifyItemChanged(position);
    }

    public interface ClickListener {

        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }
}
