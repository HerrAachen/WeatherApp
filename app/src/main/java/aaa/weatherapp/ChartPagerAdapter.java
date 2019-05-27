package aaa.weatherapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ChartPagerAdapter extends FragmentPagerAdapter {
    public ChartPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return ChartFragment.newInstance(ChartView.DAY);
        } else if (i==1) {
            return ChartFragment.newInstance(ChartView.FIVE_DAYS);
        }
        else throw new RuntimeException("getItem: Unexpected item number:" + i);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "24 Hours";
        } else if (position==1) {
            return "5 Days";
        }
        else throw new RuntimeException("getPageTitle: Unexpected item number:" + position);
    }
}
