package com.pokemonshowdown.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.pokemonshowdown.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by McBeengs on 22/10/2016.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private int count = 1;
    private Context context;
    private List<View> views;
//    private List<Fragment> fragments;
//    private List<String> titles;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        this.context = context;
        views = new ArrayList<View>();
    }

    /**
     * Used by ViewPager. "Object" represents the page; tell the ViewPager where the
     * page should be displayed, from left-to-right.  If the page no longer exists, return POSITION_NONE.
     */
    @Override
    public int getItemPosition(Object object) {
        int index = views.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    /**
     * Used by ViewPager.  Called when ViewPager needs a page to display; it is our job
     * to add the page to the container, which is normally the ViewPager itself.  Since
     * all our pages are persistent, we simply retrieve it from our "views" ArrayList.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = views.get(position);
        container.addView(v);
        return v;
    }

    /**
     * Used by ViewPager.  Called when ViewPager no longer needs a page to display; it
     * is our job to remove the page from the container, which is normally the
     * ViewPager itself.  Since all our pages are persistent, we do nothing to the
     * contents of our "views" ArrayList.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
        notifyDataSetChanged();
    }

    /**
     * Add "view" to right end of "views".
     * Returns the position of the new view.
     * The app should call this to add pages; not used by ViewPager.
     */
    public int addView(View v) {
        return addView(v, views.size());
    }

    /**
     * Add "view" at "position" to "views".
     * Returns position of new view.
     * The app should call this to add pages; not used by ViewPager.
     */
    public int addView(View v, int position) {
        views.add(position, v);
        //notifyDataSetChanged();
        return position;
    }

    /**
     * Removes "view" from "views".
     * Retuns position of removed view.
     * The app should call this to remove pages; not used by ViewPager.
     */
    public int removeView(ViewPager pager, View v) {
        return removeView(pager, views.indexOf(v));
    }

    /**
     * Removes the "view" at "position" from "views".
     * Retuns position of removed view.
     * The app should call this to remove pages; not used by ViewPager.
     */
    public int removeView(ViewPager pager, int position) {
        // ViewPager doesn't have a delete method; the closest is to set the adapter
        // again.  When doing so, it deletes all its views.  Then we can delete the view
        // from from the adapter and finally set the adapter to the pager again.  Note
        // that we set the adapter to null before removing the view from "views" - that's
        // because while ViewPager deletes all its views, it will call destroyItem which
        // will in turn cause a null pointer ref.
        pager.setAdapter(null);
        views.remove(position);
        notifyDataSetChanged();
        pager.setAdapter(this);

        return position;
    }

    /**
     * Returns the "view" at "position".
     * The app should call this to retrieve a view; not used by ViewPager.
     */
    public View getView(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (views.get(position).findViewById(R.id.battles_display) != null) { // is fragment "Home"
            return "Home";
        } else if (views.get(position).findViewById(R.id.room_title) != null) { // is fragment "BattleLobby"
            return "Battle Lobby";
        }
        return "Stub!";
    }
}
