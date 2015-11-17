package io.ripple.iris;

import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements DatumLogListener, DatumOutListener {
    GpsData gpsData;
    WifiData wifiData;
    TeleData teleData;
    boolean logging;
    ArrayList<String> logText;
    private FileOutputStream os;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        logText = new ArrayList<String>();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        logging = false;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alertText = "";
                if (!logging) {
                    startTrace();
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                    alertText = "Trace started";
                    logging = true;
                    TextView tv = (TextView) findViewById(R.id.textlog);
                    tv.setText("test");
                } else {
                    stopTrace();
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    alertText = "Trace stopped";
                    logging = false;
                }
                Snackbar.make(view, alertText, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
    private void initializeFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "iris_log.txt");
        try {
            this.os = new FileOutputStream(file, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeFile() {
        if (this.os != null) {
            try {
                this.os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startTrace () {
        gpsData = new GpsData(this);
        wifiData = new WifiData(this);
        teleData = new TeleData(this);
    }

    private void stopTrace() {
        gpsData.close();
        wifiData.close();
        teleData.close();
    }

    @Override
    public void logReceived(String text) {
        int log_max = 11;
        if (logText.size() == log_max) {
            logText.remove(log_max-1);
        }
        logText.add(0, text);
        String outString = "";
        for (String item : logText) {
            outString += item + "\n";
        }
        TextView tv = (TextView) findViewById(R.id.textlog);
        tv.setText(outString);
        /*
        try {
            os.write((System.nanoTime() + "," + text).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void outReceived(int[] nums) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
