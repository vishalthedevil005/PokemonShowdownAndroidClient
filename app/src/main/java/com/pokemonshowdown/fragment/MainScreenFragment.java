package com.pokemonshowdown.fragment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.pokemonshowdown.R;
import com.pokemonshowdown.SlidingTabLayout;
import com.pokemonshowdown.adapter.FragmentViewPagerAdapter;
import com.pokemonshowdown.application.BroadcastSender;
import com.pokemonshowdown.application.MyApplication;

import java.util.ArrayList;

/**
 * Created by McBeengs on 20/10/2016.
 */

public class MainScreenFragment extends Fragment implements View.OnClickListener {

    private SlidingTabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentViewPagerAdapter mAdapter;
    private FloatingActionMenu battleMenu;
    private boolean isFABVisible = true;
    public static TabsHolderAccessor TABS_HOLDER_ACCESSOR;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TABS_HOLDER_ACCESSOR = new TabsHolderAccessor();
        mAdapter = new FragmentViewPagerAdapter(getContext(), getFragmentManager());
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(View mView, @Nullable Bundle savedInstanceState) {
        mTabLayout = (SlidingTabLayout) mView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) mView.findViewById(R.id.view_pager);
        battleMenu = (FloatingActionMenu) mView.findViewById(R.id.battle_menu);
        mViewPager.setOffscreenPageLimit(3);

        ViewGroup layout = (ViewGroup) mView.findViewById(R.id.main_screen_frame);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        ViewGroup pager = (ViewGroup) mView.findViewById(R.id.pager_layout);
        pager.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        if (TabsHolder.getTabs().size() < 1) {
            TabsHolder.addTab(HomeFragment.class.getName(), null);
        }

        for (int i = 0; i < TabsHolder.getTabs().size(); i++) {
            addFragment(TabsHolder.getTabs().get(i), TabsHolder.getArgs().get(i));
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
                if (TabsHolder.getTabs().get(position).contains("BattleFragment")) {
                    mTabLayout.setVisibility(View.GONE);
                } else {
                    mTabLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                animateFloatingButton(position);
                FragmentViewPagerAdapter.LAST_TAB = position;
                mViewPager.setCurrentItem(FragmentViewPagerAdapter.LAST_TAB);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(FragmentViewPagerAdapter.LAST_TAB);

        battleMenu.setClosedOnTouchOutside(true);
        battleMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                //Toast.makeText(battleMenu.getContext(), "YES", Toast.LENGTH_SHORT).show();
            }
        });

        mView.findViewById(R.id.add_battle).setOnClickListener(this);
        mView.findViewById(R.id.watch_battle).setOnClickListener(this);
        mView.findViewById(R.id.challenge).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //There is no point letting players get into lobbies without internet, so...
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            BroadcastSender.get(getContext()).sendBroadcastFromMyApplication(
                    BroadcastSender.EXTRA_NO_INTERNET_CONNECTION);
            battleMenu.close(true);
            return;
        }

        switch (view.getId()) {
            case R.id.add_battle:
                battleMenu.close(true);
                Bundle args = new Bundle();
                args.putString("format", "normal");
                TabsHolder.addTab(BattleLobbyFragment.class.getName(), args);
                addFragment(TabsHolder.getTabs().get(TabsHolder.getTabsCount() - 1),
                        TabsHolder.getArgs().get(TabsHolder.getArgsCount() - 1));
                break;
            case R.id.watch_battle:
                battleMenu.close(true);
                TabsHolder.addTab(WatchBattleFragment.class.getName(), null);
                addFragment(TabsHolder.getTabs().get(TabsHolder.getTabsCount() - 1),
                        TabsHolder.getArgs().get(TabsHolder.getArgsCount() - 1));
                break;
            case R.id.challenge:
                battleMenu.close(true);
                args = new Bundle();
                args.putString("format", "challenge");
                TabsHolder.addTab(BattleLobbyFragment.class.getName(), args);
                addFragment(TabsHolder.getTabs().get(TabsHolder.getTabsCount() - 1),
                        TabsHolder.getArgs().get(TabsHolder.getArgsCount() - 1));
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

    public void addFragment(String newFragmentClass, Bundle args) {
        int pageIndex = mAdapter.addFragment(newFragmentClass, args);
        mAdapter.notifyDataSetChanged();
        // You might want to make "newPage" the currently displayed page:
        mViewPager.setCurrentItem(pageIndex, true);

        mTabLayout.setViewPager(mViewPager);
        mTabLayout.invalidate();
    }

    public int removeFragment(int defunctFragmentPosition) {
        int pageIndex = mAdapter.removeFragment(mViewPager, defunctFragmentPosition);
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

    public int changeFragment(int defunctFragmentPosition, String newFragmentClass, Bundle args) {
        int pageIndex = mAdapter.removeFragment(mViewPager, defunctFragmentPosition);
        pageIndex = mAdapter.addFragment(newFragmentClass, args, pageIndex);
        // You might want to choose what page to display, if the current page was "defunctPage".
        if (pageIndex == mAdapter.getCount())
            pageIndex--;
        mViewPager.setCurrentItem(pageIndex);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.invalidate();
        animateFloatingButton(pageIndex);

        return pageIndex;
    }

    public String getCurrentPageClass() {
        return mAdapter.getItemClass(mViewPager.getCurrentItem());
    }


    private static class TabsHolder {

        private static ArrayList<String> tabs = new ArrayList<>();
        private static ArrayList<Bundle> args = new ArrayList<>();

        public static void addTab(String fragment, Bundle bundle) {
            if (!tabs.contains(fragment)) {
                tabs.add(fragment);
                args.add(bundle);
            }
        }

        public static void addTab(String fragment, Bundle bundle, int position) {
            if (!tabs.contains(fragment)) {
                tabs.add(position, fragment);
                args.add(position, bundle);
            }
        }

        public static void removeTab(int fragment) {
            tabs.remove(fragment);
        }

        public static ArrayList<String> getTabs() {
            return tabs;
        }

        public static ArrayList<Bundle> getArgs() {
            return args;
        }

        public static int getTabsCount() {
            return tabs.size();
        }

        public static int getArgsCount() {
            return args.size();
        }
    }

    public class TabsHolderAccessor {

        public void addTab(String fragmentClass, Bundle args) {
            TabsHolder.addTab(fragmentClass, args);
            addFragment(TabsHolder.getTabs().get(TabsHolder.getTabsCount() - 1),
                    TabsHolder.getArgs().get(TabsHolder.getArgsCount() - 1));
        }

        public void removeTab() {
            String lastCurrentPage = getCurrentPageClass();
            int c = 0;
            for (String s : TabsHolder.getTabs()) {
                if (s.equals(lastCurrentPage)) {
                    break;
                }
                c++;
            }
            removeFragment(c);
            TabsHolder.removeTab(c);
        }

        public void changeTab(String newPageClass, Bundle args) {
            String lastCurrentPage = getCurrentPageClass();
            int c = 0;
            for (String s : TabsHolder.getTabs()) {
                if (s.equals(lastCurrentPage)) {
                    break;
                }
                c++;
            }
            changeFragment(c, newPageClass, args);
            int pos = TabsHolder.getTabs().indexOf(lastCurrentPage);
            TabsHolder.removeTab(pos);
            TabsHolder.addTab(newPageClass, args, pos);
        }

        public int getTabIndex() {
            return mViewPager.getCurrentItem();
        }
    }
}
