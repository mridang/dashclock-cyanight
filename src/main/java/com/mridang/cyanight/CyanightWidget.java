package com.mridang.cyanight;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.acra.ACRA;
import org.joda.time.DateTime;
import org.joda.time.Days;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class is the main class that provides the widget
 */
public class CyanightWidget extends ImprovedExtension {

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cyanight.ImprovedExtension#getIntents()
	 */
	@Override
	protected IntentFilter getIntents() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cyanight.ImprovedExtension#getTag()
	 */
	@Override
	protected String getTag() {
		return getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cyanight.ImprovedExtension#getUris()
	 */
	@Override
	protected String[] getUris() {
		return null;
	}
	

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onUpdateData(int intReason) {

		Log.d(getTag(), "Checking nightly commits");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(false);

		try {

			Log.d(getTag(), "Checking if it is a nightly build");
			if (HelperFunctions.getType().equalsIgnoreCase("NIGHTLY")) {

				Date datBuild = new SimpleDateFormat("yyyyMMdd").parse(HelperFunctions.getDate());
				Integer intDays = Days.daysBetween(new DateTime(datBuild), new DateTime(new Date())).getDays();

				Log.d(getTag(), "Checking if it is older than zero days");
				if (intDays > 0) {

					ComponentName cmpActivity = new ComponentName("com.cyanogenmod.updater", "com.cyanogenmod.updater.UpdatesSettings");

					edtInformation.clickIntent(new Intent().setComponent(cmpActivity));
					edtInformation.visible(true);
					edtInformation.expandedTitle(getQuantityString(R.plurals.changes, intDays, intDays));
					edtInformation.expandedBody(HelperFunctions.getBuildString());
					edtInformation.status(intDays.toString());

				} else {
					Log.d(getTag(), "It isn't older than zero days");
				}

			} else {
				Log.d(getTag(), "Not a nightly build");
			}

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e(getTag(), "Encountered an error", e);
			ACRA.getErrorReporter().handleSilentException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		doUpdate(edtInformation);

	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cyanight.ImprovedExtension#onReceiveIntent(android.content.Context, android.content.Intent)
	 */
	@Override
	protected void onReceiveIntent(Context ctxContext, Intent ittIntent) {
		onUpdateData(UPDATE_REASON_MANUAL);
	}

}