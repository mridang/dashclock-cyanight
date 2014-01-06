package com.mridang.cyanight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class is the main class that provides the widget
 */
public class CyanightWidget extends DashClockExtension {

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
		Log.d("CyanightWidget", "Created");
		BugSenseHandler.initAndStartSession(this, getString(R.string.bugsense));

	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@Override
	protected void onUpdateData(int arg0) {

		Log.d("CyanightWidget", "Checking nightly commits");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(false);

		try {

			Log.d("CyanightWidget", "Checking if it is a nightly build");
			if (HelperFunctions.getType().equalsIgnoreCase("NIGHTLY")) {

				Date datBuild = new SimpleDateFormat("yyyyMMdd").parse(HelperFunctions.getDate());
				Integer intDays = Days.daysBetween(new DateTime(datBuild), new DateTime(new Date())).getDays();

				Log.d("CyanightWidget", "Checking if it is older than zero days");
				if (intDays > 0) {

					edtInformation.visible(true);
					edtInformation.expandedTitle(getResources().getQuantityString(R.plurals.changes, intDays, intDays));
					edtInformation.expandedBody(HelperFunctions.getBuildString());

					ComponentName comp = new ComponentName("com.cyanogenmod.updater", "com.cyanogenmod.updater.UpdatesSettings");
					Intent ittUpdater = new Intent().setComponent(comp);
					edtInformation.clickIntent(ittUpdater);

				} else {
					Log.d("CyanightWidget", "It isn't older than zero days");
				}

			} else {
				Log.d("CyanightWidget", "Not a nightly build");
			}

			if (new Random().nextInt(5) == 0) {

				PackageManager mgrPackages = getApplicationContext().getPackageManager();

				try {

					mgrPackages.getPackageInfo("com.mridang.donate", PackageManager.GET_META_DATA);

				} catch (NameNotFoundException e) {

					Integer intExtensions = 0;
				    Intent ittFilter = new Intent("com.google.android.apps.dashclock.Extension");
				    String strPackage;

				    for (ResolveInfo info : mgrPackages.queryIntentServices(ittFilter, 0)) {

				    	strPackage = info.serviceInfo.applicationInfo.packageName;
						intExtensions = intExtensions + (strPackage.startsWith("com.mridang.") ? 1 : 0); 

					}

					if (intExtensions > 1) {

						edtInformation.visible(true);
						edtInformation.clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=com.mridang.donate")));
						edtInformation.expandedTitle("Please consider a one time purchase to unlock.");
						edtInformation.expandedBody("Thank you for using " + intExtensions + " extensions of mine. Click this to make a one-time purchase or use just one extension to make this disappear.");
						setUpdateWhenScreenOn(true);

					}

				}

			} else {
				setUpdateWhenScreenOn(false);
			}

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e("CyanightWidget", "Encountered an error", e);
			BugSenseHandler.sendException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		publishUpdate(edtInformation);
		Log.d("CyanightWidget", "Done");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
		Log.d("CyanightWidget", "Destroyed");
		BugSenseHandler.closeSession(this);

	}

}