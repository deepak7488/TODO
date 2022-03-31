package com.example.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todo.data.WorkContract;

public class AddNewItem extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mInitialValue;
    private EditText mFinalValue;
    private Uri mCurrentWorkUri;
    private Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.add_work));
        mNameEditText=findViewById(R.id.name_text_editor);
        mInitialValue=findViewById(R.id.initial_text_editor);
        mFinalValue=findViewById(R.id.final_text_editor);
        mSaveButton=findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                saveWork();
            }
        });
    }
    private void saveWork () {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String InitialString = mInitialValue.getText().toString().trim();
        String FinalString = mFinalValue.getText().toString().trim();
        int initial = 0;
        int final_val=0;
        if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(InitialString) &&
                TextUtils.isEmpty(FinalString)) {
            return;
        }

        if (!TextUtils.isEmpty(FinalString)) {
            final_val = Integer.parseInt(FinalString);
        }
        if (!TextUtils.isEmpty(InitialString)) {
            initial = Integer.parseInt(InitialString);
        }

        // Create a ContentValues object where column names are the keys,
        // and Work attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(WorkContract.WorkEntry.COLUMN_WORK_NAME, nameString);
        values.put(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE, initial);
        values.put(WorkContract.WorkEntry.COLUMN_WORK_FINAL_VALUE, final_val);


//        // Insert a new row for Work in the database, returning the ID of that new row.
//        lo newRowId = db.insert(WorkEntry.TABLE_NAME, null, values);
//
//        // Show a toast message depending on whether or not the insertion was successful
//        if (newRowId == -1) {
//            // If the row ID is -1, then there was an error with insertion.
//            Toast.makeText(this, "Error with saving Work", Toast.LENGTH_SHORT).show();
//        } else {
//            // Otherwise, the insertion was successful and we can display a toast with the row ID.
//            Toast.makeText(this, "Work saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
//        }
        if (mCurrentWorkUri == null) {
            // Insert a new Work into the provider, returning the content URI for the new Work.
            Uri newUri = getContentResolver().insert(WorkContract.WorkEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_Work_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_Work_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING Work, so update the Work with content URI: mCurrentWorkUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentWorkUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentWorkUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_Work_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_Work_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}