package edu.uiuc.mosyg.logcollectorviewer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AndroidException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewerActivity extends FragmentActivity implements ActionBar.TabListener {
	public final static int NOTIFICATION_ID = 6913;
	public final static String EXTRA_SEVERITY = "severity";
	public final static int SEVERITY_NONE = 1;
	public final static int SEVERITY_SUSPICIOUS = 2;
	public final static int SEVERITY_DANGEROUS = 3;
	public final static String EXTRA_FROMINSTALL = "frominstall";
	
	
	public String targetPackagename;
	public String targetAppname;
	
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupIntent();
		setupUI();
		dismissIntents();
	}
	
	void setupIntent() {
		try {
			Log.d("ViewerActivity", "Intent; "+getIntent());
			Uri data = getIntent().getData();
			if (data == null || data.getHost() == null) 
				targetPackagename = "com.hulu.plus";
			else
				targetPackagename = data.getHost();
			targetAppname = appNameFromPackage(targetPackagename);
			setTitle(targetAppname);
//			List<PermissionEvent> events = LogManager.grabAppEvents(this, targetPackagename);
//			Log.d("ViewerActivity", "grabbing "+events.size()+" events for app "+targetPackagename);
//			upload(events, targetPackagename);
		} catch (Exception e) {
			e.printStackTrace();
			//don't care
		}
	}
	
	void setupUI() {
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	void dismissIntents() {
		 NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		 nm.cancel(NOTIFICATION_ID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.viewer, menu);
		return true;
	}
	
	
	
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if (position == 0) {
				Fragment fragment = new EventListFragment();
				Bundle args = new Bundle();
				args.putString(EventListFragment.ARG_PACKAGENAME, targetPackagename);
				args.putString(EventListFragment.ARG_APPNAME, targetAppname);
				fragment.setArguments(args);
				return fragment;
			} else if (position == 1) {
				Fragment fragment = new ReportListFragment();
				Bundle args = new Bundle();
				args.putString(EventListFragment.ARG_PACKAGENAME, targetPackagename);
				args.putString(EventListFragment.ARG_APPNAME, targetAppname);
				fragment.setArguments(args);
				return fragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "On Device";
			case 1:
				return "Reported";
			}
			return null;
		}
	}

	

	
	
	
	
	
	
	
	
	
	
	
	private String appNameFromPackage(String packagename) throws AndroidException {
        PackageManager pm = getPackageManager();
        ApplicationInfo appinfo = pm.getApplicationInfo(packagename, 0);
        return pm.getApplicationLabel(appinfo).toString();
	}

	
	

}
