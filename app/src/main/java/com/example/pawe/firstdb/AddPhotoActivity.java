package com.example.pawe.firstdb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPhotoActivity extends AppCompatActivity {

    private final String TAG = "AddPhotoActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String photoPath;
    private String oldPhotoPath;
    private Boolean flag;
    private DatabaseHelper mDbHelper = new DatabaseHelper(this);
    private Cursor cursor;
    private Intent intent;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        intent = getIntent();
        photoPath = "";
        oldPhotoPath = " ";
        flag = intent.getBooleanExtra(MainActivity.ISEDIT_EXTRA,false);
        if(flag)
        {
            ImageView imageViewZdj = (ImageView)findViewById(R.id.imageViewPhoto);
            id = intent.getLongExtra(MainActivity.EDIT_ID_EXTRA,0);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    DatabaseContract.DB._ID,
                    DatabaseContract.DB.COLUMN_IMAGE_PATH
            };
            String selection = DatabaseContract.DB._ID + " = ?";
            String[] selectionArgs = { Long.toString(id) };
            String sortOrder = DatabaseContract.DB._ID + " DESC";
            cursor = db.query(
                    DatabaseContract.DB.TABLE_PERSONS,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            cursor.moveToFirst();
            oldPhotoPath = cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_IMAGE_PATH));
            photoPath = oldPhotoPath;
            Glide.with(this).load(cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_IMAGE_PATH))).into(imageViewZdj);
        }

        if (savedInstanceState != null)
        {
            photoPath = savedInstanceState.getString(MainActivity.PATH_EXTRA);
            Log.e(TAG,"onCreate: photoPath = " + photoPath);
            ImageView imageViewZdj = (ImageView)findViewById(R.id.imageViewPhoto);
            Glide.with(this).load(photoPath).into(imageViewZdj);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString(MainActivity.PATH_EXTRA,photoPath);
        super.onSaveInstanceState(outState);
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        photoPath = image.getAbsolutePath();
        Log.e(TAG,"createImageFile : " + photoPath);
        return image;
    }

    public void takePhoto(View view)
    {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager())!= null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {
                Log.e(TAG,"takePhoto exception: "+ ex.toString());
            }

            if(photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Log.e(TAG,"onActivityResult: "+ "REQUEST_IMAGE_CAPTURE RESULT_OK ");
            Glide.with(this).load(photoPath).into((ImageView)findViewById(R.id.imageViewPhoto));
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED)
        {
            Log.e(TAG,"onActivityResult: "+ "REQUEST_IMAGE_CAPTURE RESULT_CANCELED ");
            photoPath = " ";
        }
    }

    public void continueClick(View view)
    {
        Intent _intent = new Intent(this,SummaryActivity.class);
        _intent.putExtra(MainActivity.OLD_PATH_EXTRA,oldPhotoPath);
        _intent.putExtra(MainActivity.PATH_EXTRA,photoPath);
        _intent.putExtra(MainActivity.FIRST_NAME_EXTRA,intent.getStringExtra(MainActivity.FIRST_NAME_EXTRA));
        _intent.putExtra(MainActivity.LAST_NAME_EXTRA,intent.getStringExtra(MainActivity.LAST_NAME_EXTRA));
        _intent.putExtra(MainActivity.DATE_EXTRA,intent.getStringExtra(MainActivity.DATE_EXTRA));
        _intent.putExtra(MainActivity.EDIT_ID_EXTRA,intent.getLongExtra(MainActivity.EDIT_ID_EXTRA,-1));
        _intent.putExtra(MainActivity.ISEDIT_EXTRA,intent.getBooleanExtra(MainActivity.ISEDIT_EXTRA,false));
        startActivity(_intent);
    }

    public void deletePhoto(View view)
    {
        File file = new File(photoPath);
        ImageView imageViewZdj = (ImageView) findViewById(R.id.imageViewPhoto);
        Glide.clear(imageViewZdj);
        file.deleteOnExit();
        Log.e(TAG, "deletePhoto: deleted");
        oldPhotoPath = " ";
        photoPath = "";
    }
}

