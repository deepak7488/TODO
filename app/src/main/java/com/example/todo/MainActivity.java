package com.example.todo;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.data.WorkContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener {


    private Cursor mCursor=null;
    private WorkCursorAdapter mWorkCursorAdapter=null;
    private static final int WORK_LOADER=0;
    private Uri mCurrentWorkUri;
    private int ID=0;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        //mDbHelper=new WorkDbHelper(MainActivity.this);
        fab.setOnClickListener(view -> {
           // insertWork();
            Intent intent = new Intent(MainActivity.this, AddNewItem.class);
            startActivity(intent);
        });


        ListView workListView = (ListView) findViewById(R.id.list_view);
        mWorkCursorAdapter=new WorkCursorAdapter(this,null);
        workListView.setAdapter(mWorkCursorAdapter);
        workListView.setOnItemClickListener((listView, view, position, id) -> {
            mCurrentWorkUri=ContentUris.withAppendedId(WorkContract.WorkEntry.CONTENT_URI,id);
            ID=(int)position;
            showPopUp();
        });
        getLoaderManager().initLoader(WORK_LOADER,null,this);


    }
    public void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //this is custom dialog
        //custom_popup_dialog contains textview only
        View customView = getLayoutInflater().inflate(R.layout.custom_popup_dialog, null);
        Button mIncrement = customView.findViewById(R.id.increment);
        Button mDecrement = customView.findViewById(R.id.decrement);
        Button mDelete = customView.findViewById(R.id.delete);
        mIncrement.setOnClickListener(this);
        mDecrement.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        builder.setView(customView);
        builder.create();
        builder.show();




    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                WorkContract.WorkEntry._ID,
                WorkContract.WorkEntry.COLUMN_WORK_NAME,
                WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE,
                WorkContract.WorkEntry.COLUMN_WORK_FINAL_VALUE
        };

        return new CursorLoader(this //parent activity context
                , WorkContract.WorkEntry.CONTENT_URI //provider content URI to query
                ,projection  //Column to include in a resulting cursor
                ,null //no selection clause
                ,null //no selection argument
                ,null); //default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        mWorkCursorAdapter.swapCursor(data);

            mCursor=data;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        mWorkCursorAdapter.swapCursor(null);

    }

    public void setIncrement(){
        ContentValues values = new ContentValues();
        if(mCursor!=null){
            mCursor.moveToPosition(ID);
            int ans= mCursor.getInt(mCursor.getColumnIndexOrThrow(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE))+1;
            int fin= mCursor.getInt(mCursor.getColumnIndexOrThrow(WorkContract.WorkEntry.COLUMN_WORK_FINAL_VALUE));
            if(ans>fin){
                if(toast!=null)
                    toast.cancel();
              toast=  Toast.makeText(this, getString(R.string.task_already_completed),
                        Toast.LENGTH_SHORT);
              toast.show();
                return;
            }
            values.put(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE, ans);
            int rowsAffected=0;
            if(mCurrentWorkUri!=null)
            rowsAffected = getContentResolver().update(mCurrentWorkUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                if(toast!=null)
                    toast.cancel();
                // If no rows were affected, then there was an error with the update.
                toast=  Toast.makeText(this, getString(R.string.editor_update_Work_failed),
                        Toast.LENGTH_SHORT);

            } else {
                mWorkCursorAdapter.notifyDataSetChanged();
                if(toast!=null)
                    toast.cancel();
                // Otherwise, the update was successful and we can display a toast.
                toast= Toast.makeText(this, getString(R.string.editor_update_Work_successful),
                        Toast.LENGTH_SHORT);

            }
            toast.show();

        }

    }
    public void setDecrement(){
        ContentValues values = new ContentValues();
        if(mCursor!=null){
            mCursor.moveToPosition(ID);
            int ans= mCursor.getInt(mCursor.getColumnIndexOrThrow(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE))-1;
            int fin= mCursor.getInt(mCursor.getColumnIndexOrThrow(WorkContract.WorkEntry.COLUMN_WORK_FINAL_VALUE));
            if(ans>fin || ans<0){
                if(toast!=null)
                    toast.cancel();
                toast= Toast.makeText(this, getString(R.string.task_already_completed),
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            values.put(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE, ans);
            int rowsAffected=0;
            if(mCurrentWorkUri!=null)
                rowsAffected = getContentResolver().update(mCurrentWorkUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                if(toast!=null)
                    toast.cancel();
                // If no rows were affected, then there was an error with the update.
                toast=   Toast.makeText(this, getString(R.string.editor_update_Work_failed),
                        Toast.LENGTH_SHORT);

            } else {
                mWorkCursorAdapter.notifyDataSetChanged();
                if(toast!=null)
                    toast.cancel();
                // Otherwise, the update was successful and we can display a toast.
                toast=   Toast.makeText(this, getString(R.string.editor_update_Work_successful),
                        Toast.LENGTH_SHORT);

            }
            toast.show();

        }

    }
    public void mSetDelete(){

            int rowsAffected;
            rowsAffected = getContentResolver().delete(mCurrentWorkUri,  null, null);
            // Show a toast message depending on whether or not the update was successful.
        if(toast!=null)
            toast.cancel();
        if (rowsAffected == 0) {
            toast=     Toast.makeText(this, getString(R.string.editor_deletion_Work_failed),
                        Toast.LENGTH_SHORT);
        } else {
            mWorkCursorAdapter.notifyDataSetChanged();
                toast=     Toast.makeText(this, getString(R.string.editor_deletion_Work_successful),
                        Toast.LENGTH_SHORT);
        }
        toast.show();


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.increment:
                setIncrement();
                break;
            case R.id.decrement:
                setDecrement();
                break;
            case R.id.delete:
                mSetDelete();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }
    }
}

