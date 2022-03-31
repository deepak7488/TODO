/*
// * Copyright (C) 2016 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
package com.example.todo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.todo.data.WorkContract.WorkEntry;

/**
 * Database helper for Works app. Manages database creation and version management.
 */
public class WorkDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = com.example.todo.data.WorkDbHelper.class.getSimpleName();
//    Log.V(LOG_TAG,"hELLO");
    /** Name of the database file */
    private static final String DATABASE_NAME = "todo.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link com.example.todo.data.WorkDbHelper}.
     *
     * @param context of the app
     */
    public WorkDbHelper(Context context) {
//        super();

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
                Log.v("test","creat");
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the Works table
        String SQL_CREATE_WorkS_TABLE =  "CREATE TABLE " +WorkEntry.TABLE_NAME + " ("
                + WorkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WorkEntry.COLUMN_WORK_NAME + " TEXT NOT NULL, "
                + WorkEntry.COLUMN_WORK_INITIAL_VALUE + " INTEGER NOT NULL, "
                + WorkEntry.COLUMN_WORK_FINAL_VALUE+ " INTEGER NOT NULL DEFAULT 0);";
        Log.v("test","creat");
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_WorkS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}