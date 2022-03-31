package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.data.WorkContract;
import com.example.todo.data.WorkDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private WorkDbHelper mDbHelper;
    private Cursor mCursor=null;
    private ListView WorkListView=null;
    private WorkCursorAdapter mWorkCursorAdapter=null;
    private static final int WORK_LOADER=0;
    private static final int EXIST_WORK_LOADER=1;
    private Button mIncrement;
    private Button mDecrement;
    private Button mDelete;
    private Uri mCurrentPetUri;
    private int id=0;

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
        mIncrement=findViewById(R.id.increment);
        mDecrement=findViewById(R.id.decrement);
        mDelete=findViewById(R.id.delete);
        WorkListView = (ListView) findViewById(R.id.list_view);
        mWorkCursorAdapter=new WorkCursorAdapter(this,null);
        WorkListView.setAdapter(mWorkCursorAdapter);
        WorkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                mCurrentPetUri=ContentUris.withAppendedId(WorkContract.WorkEntry.CONTENT_URI,id);
                //listView.get
                showPopUp(view,id);
            }
        });
        getLoaderManager().initLoader(WORK_LOADER,null,this);

//        mDecrement.setOnClickListener(this);
//        mDelete.setOnClickListener(this);
    }
    public void showPopUp(View view,long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();

        //this is custom dialog
        //custom_popup_dialog contains textview only
        View customView = layoutInflater.inflate(R.layout.custom_popup_dialog, null);
            mIncrement=customView.findViewById(R.id.increment);
            if(mIncrement==null){
                Log.v("testing","Hello i am null");
            }
            else{
            mIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                setIncrement(id);
            }
        }
        );}
//        // reference the textview of custom_popup_dialog
//        TextView tv = (TextView) customView.findViewById(R.id.tvpopup);
//
//
//        //this textview is from the adapter
//        TextView text = (TextView) view.findViewById(R.id.textView);
//        // get the text of the view clicked
//        String day = text.getText().toString();
//        //set the text to the view of custom_popop_dialog.
//        tv.setText(day);
//
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
        String selection=null;
        String[] selectionArgs=null;
        id=i;
        if(i==1){
            selection = WorkContract.WorkEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentPetUri))};
        }
        return new CursorLoader(this //parent activity context
                , WorkContract.WorkEntry.CONTENT_URI //provider content URI to query
                ,projection  //Column to include in a resulting cursor
                ,selection //no selection clause
                ,selectionArgs //no selection argumnet
                ,null); //default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(id==0){
        mWorkCursorAdapter.swapCursor(data);
        }
//        else{
            mCursor=data;
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if(id==0){
        mWorkCursorAdapter.swapCursor(null);
        }
       // mCurrentPetUri=null;
    }
    private void insertWork() {


        // Create a ContentValues object where column names are the keys,
        // and Toto's Work attributes are the values.
        ContentValues values = new ContentValues();
        values.put(WorkContract.WorkEntry.COLUMN_WORK_NAME, "Toto");
        values.put(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE, 60);
        values.put(WorkContract.WorkEntry.COLUMN_WORK_FINAL_VALUE, 100);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the Works table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        Uri newUri = getContentResolver().insert(WorkContract.WorkEntry.CONTENT_URI, values);
        // Show a toast message depending on whether or not the insertion was successful
        if (newUri== null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_Work_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_Work_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setIncrement(long id){
       // getLoaderManager().initLoader(EXIST_WORK_LOADER,null,this);
        ContentValues values = new ContentValues();
        if(mCursor!=null){
            mCursor.moveToPosition((int)(id-1));

            int ans= mCursor.getInt(mCursor.getColumnIndexOrThrow(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE))+1;
            int fin=mCursor.getInt(mCursor.getColumnIndexOrThrow(WorkContract.WorkEntry.COLUMN_WORK_FINAL_VALUE));
            values.put(WorkContract.WorkEntry.COLUMN_WORK_INITIAL_VALUE, ans);
           // progressBar.setMax(final_value);
            int rowsAffected=0;
            if(mCurrentPetUri!=null)
            rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);
            //mCurrentPetUri=null;
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_Work_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                mWorkCursorAdapter.notifyDataSetChanged();
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_Work_successful),
                        Toast.LENGTH_SHORT).show();

                //getLoaderManager().initLoader(WORK_LOADER,null,this);
            }
           // mCursor=null;

        }

    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.increment:
//
//                break;
//
//            case R.id.decrement:
//                break;
//            case R.id.delete:
//                break;
//            default:
//                break;
//        }
//    }
}