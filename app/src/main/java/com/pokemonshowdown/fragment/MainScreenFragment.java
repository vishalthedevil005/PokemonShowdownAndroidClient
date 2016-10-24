package com.pokemonshowdown.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.pokemonshowdown.R;
import com.pokemonshowdown.SlidingTabLayout;
import com.pokemonshowdown.adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by McBeengs on 20/10/2016.
 */

public class MainScreenFragment extends Fragment implements View.OnClickListener {

    private View mView;
    //private FloatingActionMenu battleButton;
    private SlidingTabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private FloatingActionsMenu battleMenu;
    private View bluredBackground;
    private ArrayList<View> tabs = new ArrayList<>();
    private boolean isFABVisible = true;
    public static TabsHolderAccessor TABS_HOLDER_ACCESSOR;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        TABS_HOLDER_ACCESSOR = new TabsHolderAccessor();
        View v = inflater.inflate(R.layout.fragment_main_screen, container, false);
        mView = v;
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTabLayout = (SlidingTabLayout) mView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) mView.findViewById(R.id.view_pager);
        battleMenu = (FloatingActionsMenu) mView.findViewById(R.id.battle_menu);
        bluredBackground = mView.findViewById(R.id.blurred_background);
        mViewPager.setOffscreenPageLimit(3);

        // Can't assign alpha = 0 directly on XML, so...
        Animation fix = new AlphaAnimation(1, 0);
        fix.setFillAfter(true);
        fix.setDuration(2);
        bluredBackground.startAnimation(fix);

        mAdapter = new ViewPagerAdapter(getContext(), getFragmentManager());
        if (TabsHolder.getTabs().size() < 1) {
            TabsHolder.addTab(HomeFragment.newInstance(getLayoutInflater(null)));
        }

        for (View v : TabsHolder.getTabs()) {
            addView(v);
        }
        mViewPager.setAdapter(mAdapter);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.color_accent);
            }
        });
        mTabLayout.setViewPager(mViewPager);

        mTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                animateFloatingButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        battleMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(200);
                fadeIn.setFillAfter(true);
                bluredBackground.startAnimation(fadeIn);
            }

            @Override
            public void onMenuCollapsed() {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut.setDuration(200);
                fadeOut.setFillAfter(true);
                bluredBackground.startAnimation(fadeOut);
            }
        });

        mView.findViewById(R.id.add_battle).setOnClickListener(this);
        mView.findViewById(R.id.challenge).setOnClickListener(this);
//        battleButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                mTabLayout.setViewPager(mViewPager);
//                mTabLayout.invalidate();
//                return true;
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_battle:
                battleMenu.collapse();
                TabsHolder.addTab(BattleLobbyFragment.newInstance(getLayoutInflater(null), "normal"));
                addView(TabsHolder.getTabs().get(TabsHolder.getTabsCount() - 1));
                break;
            case R.id.challenge:
                battleMenu.collapse();
                TabsHolder.addTab(BattleLobbyFragment.newInstance(getLayoutInflater(null), "challenge"));
                addView(TabsHolder.getTabs().get(TabsHolder.getTabsCount() - 1));
                break;
        }
    }

    private void animateFloatingButton(int pos) {
        if (pos == 0) {
            ScaleAnimation grow = new ScaleAnimation(
                    0.0f, 1.0f, // X inicial e final
                    0.0f, 1.0f, // Y inicial e final
                    Animation.RELATIVE_TO_SELF, 0.5f, // Eixo X
                    Animation.RELATIVE_TO_SELF, 0.5f  // Eixo Y
            );

            grow.setDuration(200);
            grow.setFillAfter(true);
            battleMenu.startAnimation(grow);
            isFABVisible = true;
        } else {
            if (isFABVisible) {
                ScaleAnimation shrink = new ScaleAnimation(
                        1.0f, 0.0f, // X inicial e final
                        1.0f, 0.0f, // Y inicial e final
                        Animation.RELATIVE_TO_SELF, 0.5f, // Eixo X
                        Animation.RELATIVE_TO_SELF, 0.5f  // Eixo Y
                );

                shrink.setDuration(200);
                shrink.setFillAfter(true);
                battleMenu.startAnimation(shrink);
                isFABVisible = false;
            }
        }
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to add a view to the ViewPager.
    public void addView(View newPage) {
        int pageIndex = mAdapter.addView(newPage);
        mAdapter.notifyDataSetChanged();
        // You might want to make "newPage" the currently displayed page:
        mViewPager.setCurrentItem(pageIndex, true);

        //We need check if the home panel is already inserted before setting the mTabLayout visible
        if (newPage.findViewById(R.id.meloetta_icon) == null && mTabLayout.getVisibility() == View.GONE) {
            mTabLayout.setVisibility(View.VISIBLE);
        }

        mTabLayout.setViewPager(mViewPager);
        mTabLayout.invalidate();
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to remove a view from the ViewPager.
    public int removeView(View defunctPage) {
        int pageIndex = mAdapter.removeView(mViewPager, defunctPage);
        mAdapter.notifyDataSetChanged();
        // You might want to choose what page to display, if the current page was "defunctPage".
        if (pageIndex == mAdapter.getCount())
            pageIndex--;
        mViewPager.setCurrentItem(pageIndex);
        if (pageIndex == 0) {
            mTabLayout.setVisibility(View.GONE);
        }
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.invalidate();
        animateFloatingButton(pageIndex);

        return pageIndex;
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to get the currently displayed page.
    public View getCurrentPage() {
        return mAdapter.getView(mViewPager.getCurrentItem());
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to set the currently displayed page.  "pageToShow" must
    // currently be in the adapter, or this will crash.
    public void setCurrentPage(View pageToShow) {
        mViewPager.setCurrentItem(mAdapter.getItemPosition(pageToShow), true);
    }

    private static class TabsHolder {

        private static ArrayList<View> tabs = new ArrayList<>();

        public static void addTab(View view) {
            if (!tabs.contains(view)) {
                tabs.add(view);
            }
        }

        public static void removeTab(View view) {
            tabs.remove(view);
        }

        public static ArrayList<View> getTabs() {
            return tabs;
        }

        public static int getTabsCount() {
            return tabs.size();
        }
    }

    public class TabsHolderAccessor {

        public void addTab(View view) {
            TabsHolder.addTab(view);
            addView(TabsHolder.getTabs().get(TabsHolder.getTabsCount() - 1));
        }

        public void removeTab() {
            View lastCurrentPage = getCurrentPage();
            removeView(lastCurrentPage);
            TabsHolder.removeTab(lastCurrentPage);
        }
    }
}
