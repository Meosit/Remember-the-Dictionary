package by.mksn.rememberthedictionary.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import by.mksn.rememberthedictionary.model.PhraseStore;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final PhraseStore phraseStore;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public SectionsPagerAdapter(FragmentManager fm, PhraseStore phraseStore) {
        super(fm);
        this.phraseStore = phraseStore;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PracticeFragment.newInstance(phraseStore);
            case 1:
                return PracticeFragment.newInstance(phraseStore);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Practice";
            case 1:
                return "All phrases";
        }
        return null;
    }
}
