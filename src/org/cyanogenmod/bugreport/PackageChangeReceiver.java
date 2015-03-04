/*
 * Copyright (C) 2014 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cyanogenmod.bugreport;

import android.app.Activity;
import android.app.CustomTile;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;

import org.cyanogenmod.bugreport.R;

public class PackageChangeReceiver extends BroadcastReceiver {

    private static final String XPOSED_INSTALLER_PACKAGE = "de.robv.android.xposed.installer";

    private static final int TILE_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int newState = PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
        if (isPackageInstalled(context, XPOSED_INSTALLER_PACKAGE)) {
            // disable bug reporting
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }

        ComponentName crashActivity = new ComponentName(context, CrashFeedbackActivity.class);
        ComponentName bugActivity = new ComponentName(context, MainActivity.class);

        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(crashActivity, newState, 0);
        pm.setComponentEnabledSetting(bugActivity, newState, 0);
        if (newState != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            createTile(context);
        } else {
            removeTile(context);
        }
    }

    public static boolean isPackageInstalled(Context context, String pkg) {
        if (pkg == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(pkg, 0);
            if (!pi.applicationInfo.enabled) {
                return false;
            } else {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void createTile(Context context) {
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
        CustomTile customTile = new CustomTile.Builder(context)
                .setIcon(R.drawable.ic_launcher)
                .setLabel(R.string.qs_tile_title)
                .setContentDescription(R.string.qs_tile_description)
                .setOnClickIntent(pendingIntent)
                .setVisibility(true)
                .build();
        StatusBarManager statusBarManager
                = (StatusBarManager) context.getSystemService(Context.STATUS_BAR_SERVICE);
        statusBarManager.publishTile(TILE_ID, customTile);
    }

    private void removeTile(Context context) {
        StatusBarManager statusBarManager
                = (StatusBarManager) context.getSystemService(Context.STATUS_BAR_SERVICE);
        statusBarManager.removeTile(TILE_ID);
    }

}
