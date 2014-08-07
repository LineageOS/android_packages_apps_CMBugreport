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
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BugReportTest extends Activity {

    Context mContext;
    ScrubberUtils.ScrubFileTask mTask;

    private static final String TAG = BugReportTest.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);

        mTask = new ScrubberUtils.ScrubFileTask(this);
        mTask.execute(findBugReport());
    }

    private File findBugReport() {
        File bugReportDir = new File("/data/bugreports/");
        File[] files = bugReportDir.listFiles();
        for (File file : files) {
            if (file.getName().startsWith("bugreport-") && file.getName().endsWith(".txt")) {
                return file;
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        mTask.cancel(true);
        super.onDestroy();
    }


}