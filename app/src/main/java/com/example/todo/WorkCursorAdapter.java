package com.example.todo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.todo.data.WorkContract.WorkEntry;

public class WorkCursorAdapter extends CursorAdapter {

    public WorkCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_work, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView initial_final = (TextView) view.findViewById(R.id.initial_final);
        ProgressBar progressBar=(ProgressBar) view.findViewById(R.id.progress);

        // Find the columns of Work attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(WorkEntry.COLUMN_WORK_NAME);
        int initial_value_Index=cursor.getColumnIndex(WorkEntry.COLUMN_WORK_INITIAL_VALUE);
        int final_value_Index=cursor.getColumnIndex(WorkEntry.COLUMN_WORK_FINAL_VALUE);


        // Read the Work attributes from the Cursor for the current Work
        String WorkName = cursor.getString(nameColumnIndex);
        int initial_value=cursor.getInt(initial_value_Index);
        int final_value=cursor.getInt(final_value_Index);

//        // If the Work breed is empty string or null, then use some default text
//        // that says "Unknown breed", so the TextView isn't blank.
//        if (TextUtils.isEmpty(WorkBreed)) {
//            WorkBreed = context.getString(R.string.unknown_breed);
//        }

        // Update the TextViews with the attributes for the current Work
        nameTextView.setText(WorkName);
        String s= initial_value +"/"+ final_value;
        initial_final.setText(s);
        progressBar.setProgress(initial_value);
        progressBar.setMax(final_value);
    }
}
