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

package org.cyanogenmod.cmbugreport;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;

public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "org.cyanogenmod.cmbugreport.MESSAGE";
    private Uri reportURI;
    private Button submitButton;
    ArrayList<Uri> attachments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if ("application/vnd.android.bugreport".equals(type)) {
                handleBugReport(intent);
            }
        } else {
            attachments = new ArrayList<Uri>();
            attachments.add(reportURI);
        }
    }

    private void handleBugReport(Intent intent) {
        attachments = new ArrayList<Uri>();
        attachments = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendBug(View view){
        // Disable send button to avoid doubleposts
        submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setEnabled(false);

        // Grab entered text
        EditText summaryEditText = (EditText) findViewById(R.id.summary);
        EditText descriptionEditText = (EditText) findViewById(R.id.description);

        String summary = summaryEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (summary != null && description != null
                && !summary.isEmpty() && !description.isEmpty()) {
            Intent intent = new Intent(this, CMLogService.class);
            intent.putExtra(Intent.EXTRA_SUBJECT, summary);
            intent.putExtra(Intent.EXTRA_TEXT, description);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments);
            startService(intent);

            // Make the screen go away
            finish();
        } else {
            // Error message for blank text
            NoTextDialog nope = new NoTextDialog();
            nope.show(getFragmentManager(), "notext");

            // Re-enable the button so they can put in text and hit button again
            submitButton.setEnabled(true);
        }
    }

    class NoTextDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.noText)
                .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            return builder.create();
        }
    }
}
