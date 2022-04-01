package com.example.todo.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todo.data.WorkContract.WorkEntry;
public class WorkProvider extends ContentProvider{
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = WorkProvider.class.getSimpleName();
    //Database helper object
    private WorkDbHelper mDbHelper;
    /**
     * URI matcher code for the content URI for the Works table
     */
    private static final int WORKS = 100;

    /**
     * URI matcher code for the content URI for a single Work in the Works table
     */
    private static final int WORK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(WorkContract.CONTENT_AUTHORITY, WorkContract.PATH_WORKS, WORKS);
        sUriMatcher.addURI(WorkContract.CONTENT_AUTHORITY, WorkContract.PATH_WORKS + "/#", WORK_ID);
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new WorkDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

//        if (!mDbHelper.db.isOpen())
//            mDbHelper.open();
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
            Cursor cursor;
            int match = sUriMatcher.match(uri);
            switch (match) {
                case WORKS:
                    /* TODO: ACT ON WorkS TABLE */
                    cursor = database.query(WorkEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case WORK_ID:
                    /* TODO: ACT ON SINGLE Work */
                    selection = WorkEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    cursor = database.query(WorkEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                default:
                    /* TODO: NO MATCH FOUND */
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);

            }
            //set notification uri to cursor
            //so we know  what content uri he cursor as created for
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
            return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        if (match == WORKS) {
            assert contentValues != null;
            return insertWork(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKS:
                return WorkEntry.CONTENT_LIST_TYPE;
            case WORK_ID:
                return WorkEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case WORKS:

                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(WorkEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case WORK_ID:
                // Delete a single row given by the ID in the URI
                selection = WorkEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(WorkEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKS:
                assert contentValues != null;
                return updateWork(uri, contentValues, selection, selectionArgs);
            case WORK_ID:
                // For the Work_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = WorkEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                assert contentValues != null;
                return updateWork(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateWork(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected Works in the Works database table with the given ContentValues
        if (values.containsKey(WorkEntry.COLUMN_WORK_NAME)) {
            String name = values.getAsString(WorkEntry.COLUMN_WORK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Work requires a name");
            }
        }

        // If the {@link WorkEntry#COLUMN_Work_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(WorkEntry.COLUMN_WORK_INITIAL_VALUE)) {
            Integer initial_value = values.getAsInteger(WorkEntry.COLUMN_WORK_INITIAL_VALUE);
        //            || !WorkEntry.isValidGender(gender)
            if (initial_value == null ) {
                throw new IllegalArgumentException("Work requires valid gender");
            }
        }

        // If the {@link WorkEntry#COLUMN_Work_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(WorkEntry.COLUMN_WORK_FINAL_VALUE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(WorkEntry.COLUMN_WORK_FINAL_VALUE);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Work requires valid weight");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        // TODO: Return the number of rows that were affected
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(WorkEntry.TABLE_NAME, values, selection, selectionArgs);


        if (rowsUpdated != 0) {
            //notify all listeners that data change for Work content uri
            Log.v("Updated","Hello");
            getContext().getContentResolver().notifyChange(uri, null);
        }


//        getContext().getContentResolver().notifyChange(uri,null);
        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }
    private Uri insertWork(Uri uri, ContentValues values) {
        //TODO: Insert a New Work into Works Database table with given content values
        //Once we know the of the new row in the table return the new URI;


        Integer initial_value = values.getAsInteger(WorkEntry.COLUMN_WORK_INITIAL_VALUE);
        String name = values.getAsString(WorkEntry.COLUMN_WORK_NAME);
        Integer final_value = values.getAsInteger(WorkEntry.COLUMN_WORK_FINAL_VALUE);
        if (name == null) {
            throw new IllegalArgumentException("Works requires a name");
        }
//        !WorkEntry.isValidGender(gender)
        if (initial_value == null) {
            throw new IllegalArgumentException("Work requires valid gender");
        }
        if (final_value==null) {
            throw new IllegalArgumentException("Work requires valid weight");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(WorkEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //notify all listeners that data change for Work content uri
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }
}
