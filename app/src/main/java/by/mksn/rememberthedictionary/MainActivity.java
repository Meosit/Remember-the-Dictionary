package by.mksn.rememberthedictionary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import by.mksn.rememberthedictionary.fragment.AllPhrasesFragment;
import by.mksn.rememberthedictionary.fragment.PracticeFragment;
import by.mksn.rememberthedictionary.fragment.SectionsPagerAdapter;
import by.mksn.rememberthedictionary.model.Phrase;
import by.mksn.rememberthedictionary.model.PhraseStore;
import by.mksn.rememberthedictionary.model.PhraseStoreFactory;
import by.mksn.rememberthedictionary.util.CannotReadFileException;

import static by.mksn.rememberthedictionary.util.ActivityUtil.showAddSingleDialog;
import static by.mksn.rememberthedictionary.util.ActivityUtil.showRemoveSingleDialog;
import static by.mksn.rememberthedictionary.util.ActivityUtil.verifyStoragePermissions;
import static by.mksn.rememberthedictionary.util.FileParserUtil.readDOCX;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private PhraseStore phraseStore;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        phraseStore = PhraseStoreFactory.getStore(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        final Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(rotateAnimation);
                PracticeFragment practiceFragment = (PracticeFragment) mSectionsPagerAdapter.getRegisteredFragment(0);
                practiceFragment.loadPhrase();
            }
        });
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), phraseStore, fab);


        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void performDocxFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                try {
                    String relativePath = uri.getLastPathSegment().replace("primary:", "");
                    String absolutePath = Environment.getExternalStoragePublicDirectory("") + "/" + relativePath;
                    List<Phrase> phrases = readExternalDocument(new File(absolutePath));
                    phraseStore.insertAll(phrases);
                    AllPhrasesFragment fragment = (AllPhrasesFragment) mSectionsPagerAdapter.getRegisteredFragment(1);
                    fragment.reloadData();
                    Snackbar.make(fab, R.string.menu_add_file_success_message, Snackbar.LENGTH_SHORT).show();
                } catch (CannotReadFileException e) {
                    Log.e("Cannot read file ", "", e);
                    Toast.makeText(this, R.string.menu_add_file_message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final AllPhrasesFragment fragment = (AllPhrasesFragment) mSectionsPagerAdapter.getRegisteredFragment(1);
        switch (item.getItemId()) {
            case R.id.action_add_single:
                showAddSingleDialog(this, fab, phraseStore, null, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        fragment.reloadData();
                        return null;
                    }
                });
                return true;
            case R.id.action_add_file:
                verifyStoragePermissions(this);
                performDocxFileSearch();
                return true;
            case R.id.action_remove_single:
                showRemoveSingleDialog(this, fab, phraseStore, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        fragment.reloadData();
                        return null;
                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Phrase> readExternalDocument(File file) throws CannotReadFileException {
        List<Phrase> phrases;
        try {
            phrases = readDOCX(file);
        } catch (Exception e) {
            throw new CannotReadFileException(e);
        }
        return phrases;
    }
}
