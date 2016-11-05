package com.pokemonshowdown.fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pokemonshowdown.R;
import com.pokemonshowdown.application.MyApplication;
import com.pokemonshowdown.data.BattleFieldData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class BattleFieldFragment {
    public final static String BTAG = BattleFieldFragment.class.getName();
    private static final String OPTIONAL_ROOM_TAG = "OPTIONAL_ROOM_TAG";
    private BattleFieldPagerAdapter mBattleFieldPagerAdapter;
    private ViewPager mViewPager;

    private ArrayList<String> mRoomList;
    private int mPosition;

    public static View newInstance(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_placeholder, null);
    }

    private void removeCurrentRoom(boolean refresh) {
//        ActionBar actionBar = getActivity().getActionBar();
//        ActionBar.Tab tab = actionBar.getSelectedTab();
//        int tabPosition = tab.getPosition();
//        String roomId = mRoomList.get(tabPosition);
//        if (roomId.equals("global")) {
//            return;
//        }
//        BattleFragment fragment = (BattleFragment) getChildFragmentManager()
//                .findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + tab.getPosition());
//        if (fragment != null) {
//            if (fragment.getBattling() != 0 && !refresh) {
//                if (fragment.isBattleOver()) {
//                    MyApplication.getMyApplication().sendClientMessage(roomId + "|/leave");
//                } else {
//                    MyApplication.getMyApplication().sendClientMessage(roomId + "|/forfeit");
//                    Toast.makeText(getActivity(), R.string.forfeit, Toast.LENGTH_SHORT).show();
//                }
//            }
//            getChildFragmentManager().beginTransaction().remove(fragment).commit();
//        }
//        BattleFieldData.get(getActivity()).leaveRoom(roomId);
//        mBattleFieldPagerAdapter.notifyDataSetChanged();
//        mViewPager.setAdapter(mBattleFieldPagerAdapter);
//        actionBar.removeTab(tab);
//
//        FindBattleFragment findBattleFragment = (FindBattleFragment) getChildFragmentManager()
//                .findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + 0);
//
//        if (findBattleFragment != null) {
//            findBattleFragment.setQuota(false);
//        }
//        //decrementBattleFragmentTag(tabPosition, tabCount - 1);
    }

    public void setAvailableFormat() {
//        if (mPosition == 0) {
//            FindBattleFragment fragment = (FindBattleFragment) getChildFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + 0);
//            if (fragment != null) {
//                fragment.setAvailableFormat();
//            }
//        }
    }

    public void processServerMessage(String roomId, String message) {
//        int index = mRoomList.indexOf(roomId);
//        BattleFragment fragment = (BattleFragment) getChildFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + index);
//        if (fragment != null) {
//            fragment.processServerMessage(message);
//        }
    }
/*
    public void decrementBattleFragmentTag(int start, int end) {
        ArrayDeque<String> roomList = new ArrayDeque<>();
        ActionBar actionBar = getActivity().getActionBar();

        for (int i = start; i < end; i++) {
            BattleFragment fragment = (BattleFragment) getChildFragmentManager()
                    .findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + i);

            if (fragment != null) {
                roomList.addLast(fragment.getRoomId());

                getChildFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();

                mRoomList.remove(i);
            }

            actionBar.removeTabAt(i);
        }

        mBattleFieldPagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mBattleFieldPagerAdapter);

        while (!roomList.isEmpty()) {
            String roomId = roomList.pollFirst();
            mRoomList.add(roomId);

            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Battle" + (mRoomList.size() - 1))
                            .setTabListener(mTabListener)
            );
        }

        mBattleFieldPagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mBattleFieldPagerAdapter);

    }*/

    private class BattleFieldPagerAdapter extends FragmentPagerAdapter {
        public BattleFieldPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
//            if (i == 0) {
//                return FindBattleFragment.newInstance();
//            }
//            return BattleFragment.newInstance(mRoomList.get(i));
            return null;
        }

        @Override
        public int getCount() {
            return mRoomList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Battle" + position;
        }
    }

}
