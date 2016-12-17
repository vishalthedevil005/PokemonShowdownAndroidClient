package com.pokemonshowdown.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.pokemonshowdown.R;
import com.pokemonshowdown.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by McBeengs on 22/10/2016.
 */

public class FragmentViewPagerAdapter extends FragmentStatePagerAdapter {

    private int count = 1;
    private Context context;
    private List<String> fragmentsClasses;
    private List<Bundle> bundles;
    public static int LAST_TAB = 0;

    public FragmentViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        fragmentsClasses = new ArrayList<>();
        bundles = new ArrayList<>();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(context, fragmentsClasses.get(position), bundles.get(position));
    }

    public String getItemClass(int position) {
        return fragmentsClasses.get(position);
    }

    public int getCount() {
        return fragmentsClasses.size();
    }

    /**
     * @param fragment Class.class.getName()
     *
     * @return
     */
    public int addFragment(String fragment, Bundle args) {
        return addFragment(fragment, args, fragmentsClasses.size());
    }

    public int addFragment(String fragment, Bundle args, int position) {
        fragmentsClasses.add(position, fragment);
        bundles.add(args);
        return fragmentsClasses.size();
    }

    public int removeFragment(ViewPager pager, int position) {
        // ViewPager doesn't have a delete method; the closest is to set the adapter
        // again.  When doing so, it deletes all its views.  Then we can delete the view
        // from the adapter and finally set the adapter to the pager again.  Note
        // that we set the adapter to null before removing the view from "views" - that's
        // because while ViewPager deletes all its views, it will call destroyItem which
        // will in turn cause a null pointer ref.
        pager.setAdapter(null);
        fragmentsClasses.remove(position);
        notifyDataSetChanged();
        pager.setAdapter(this);

        return position;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        Activity view = Fragment.instantiate(context, fragmentsClasses.get(position)).getActivity();
//        if (view.findViewById(R.id.battles_display) != null) { // is fragment "Home"
//            return "Home";
//        } else if (view.findViewById(R.id.user_text) != null) { // is fragment "BattleLobby"
//            return "Battle Lobby";
//        } else if (view.findViewById(R.id.battles_list_view) != null) { // is fragment "WatchBattle"
//            return "Watch Battle";
//        }
        return "Stub!";
    }
}
