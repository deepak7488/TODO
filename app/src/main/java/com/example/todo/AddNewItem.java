package com.example.todo;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.data.WorkContract;

public class AddNewItem extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mInitialValue;
    private EditText mFinalValue;
    private Toast toast;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        setTitle(getString(R.string.add_work));
        mNameEditText=findViewById(R.id.name_text_editor);
        mInitialValue=findViewById(R.id.initial_text_editor);
        mFinalValue=findViewById(R.id.final_text_editor);
        Button mSaveButton = findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(view -> saveWork());
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
            if(toast!=null)
                toast.cancel();
            toast=Toast.makeText(this, "Must Enter All Fields", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!TextUtils.isEmpty(InitialString)) {
            try{
                initial= Integer.parseInt(InitialString);
            }catch (Exception e){
                if(toast!=null)
                    toast.cancel();
                toast=Toast.makeText(this, "Must Enter Integer Value in Initial Value Column", Toast.LENGTH_SHORT);
                toast.show();
                return;

            }
        }

        if (!TextUtils.isEmpty(FinalString)) {
            try{
            final_val = Integer.parseInt(FinalString);
            }catch (Exception e){
                if(toast!=null)
                    toast.cancel();
                toast=Toast.makeText(this, "Must Enter Integer Value in Final Value Column", Toast.LENGTH_SHORT);
                toast.show();
                return;

            }
        }

        if(initial>final_val){
            if(toast!=null)
                toast.cancel();
           toast= Toast.makeText(this, "Initial Must Be lower tha finish value", Toast.LENGTH_SHORT);
                   toast.show();
            return;
        }

        // Created a ContentValues object where column names are the keys,
        // and Work attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(WorkContract.WorkEntry.COLUMN_WORK_NAME, nameString);
        values.put(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE, initial);
        values.put(WorkContract.WorkEntry.COLUMN_WORK_FINAL_VALUE, final_val);

            // Insert a new Work into the provider, returning the content URI for the new Work.
            Uri newUri = getContentResolver().insert(WorkContract.WorkEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if(toast!=null)
                toast.cancel();
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                toast=  Toast.makeText(this, getString(R.string.editor_insert_Work_failed),
                        Toast.LENGTH_SHORT);
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                toast=  Toast.makeText(this, getString(R.string.editor_insert_Work_successful),
                        Toast.LENGTH_SHORT);
            }
            toast.show();

    }
}