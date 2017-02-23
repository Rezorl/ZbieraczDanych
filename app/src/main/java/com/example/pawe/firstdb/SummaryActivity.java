package com.example.pawe.firstdb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

public class SummaryActivity extends AppCompatActivity
{
    private final String TAG = "SummaryActivity";
    private Intent intent;
    private String firstName;
    private String lastName;
    private String date;
    private String oldPhotoPath;
    private String photoPath;
    private DatabaseHelper mDbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        intent = getIntent();
        loadDataToViews();
    }

    private void loadDataToViews()
    {
        TextView textViewFirstName = (TextView) findViewById(R.id.sumUp_textView_firstName);
        TextView textViewLastName = (TextView) findViewById(R.id.sumUp_textView_lastName);
        TextView textViewDate= (TextView) findViewById(R.id.sumUp_textView_date);
        ImageView imageViewShowCapturedImage = (ImageView) findViewById(R.id.sumUP_imageView_showCapturedImage);
        firstName = intent.getStringExtra(MainActivity.FIRST_NAME_EXTRA);
        lastName = intent.getStringExtra(MainActivity.LAST_NAME_EXTRA);
        date = intent.getStringExtra(MainActivity.DATE_EXTRA);
        photoPath = intent.getStringExtra(MainActivity.PATH_EXTRA);
        oldPhotoPath = intent.getStringExtra(MainActivity.OLD_PATH_EXTRA);
        textViewFirstName.setText(firstName);
        textViewLastName.setText(lastName);
        textViewDate.setText(date);

        if(!photoPath.equals(""))
        {
            Glide.with(this).load(photoPath).into(imageViewShowCapturedImage);
            Log.e(TAG,"loadDataToViews : " + photoPath);
        }
    }

    public void addToDatabaseOnClick(View view)
    {
        boolean flag = intent.getBooleanExtra(MainActivity.ISEDIT_EXTRA,false);
        Long id = intent.getLongExtra(MainActivity.EDIT_ID_EXTRA,0);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DB.COLUMN_FIRSTNAME,firstName);
        values.put(DatabaseContract.DB.COLUMN_LASTNAME,lastName);
        values.put(DatabaseContract.DB.COLUMN_DATE,date);
        values.put(DatabaseContract.DB.COLUMN_IMAGE_PATH,photoPath);
        if(flag)
        {
            String selection = DatabaseContract.DB._ID + " LIKE ?";

            String[] selectionArgs = {Long.toString(id)};
            Log.e(TAG,"addToDatabaseOnClick : update ");
            db.update(DatabaseContract.DB.TABLE_PERSONS, values, selection, selectionArgs);
            if (!photoPath.equals(oldPhotoPath))
            {
                Log.e(TAG,"addToDatabaseOnClick : delete file  " + oldPhotoPath);
                File f = new File(oldPhotoPath);
                f.delete();
            }
        }
        else
        {
            Log.e(TAG,"addToDatabaseOnClick : insert to database");
            if (db.insert(DatabaseContract.DB.TABLE_PERSONS,null,values) == -1)
            {
                Log.e(TAG,"addToDatabaseOnClick : insert to database error");
            }
        }
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void cancelOnClick(View view)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
