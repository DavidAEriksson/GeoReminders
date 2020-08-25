package davidaeriksson.github.io.georeminders.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Eriksson
 * ViewPagerAdapter.java
 * Custom ViewPagerAdapter that extends FragmentPagerAdapter to handle fragments.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentTitles = new ArrayList<>();

    /**
     * Constructor: ViewPagerAdapter
     * @param fm
     * @param behavior
     */
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    /**
     * Method: addFragment
     * @param fragment
     * @param title
     */
    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentTitles.add(title);
    }

    /**
     * Method: getItem
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * Method: getCount
     * @return
     */
    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     * Method getPageTitle
     * @param position
     * @return fragmentTitles.get(position) - List object at @position
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }
}
