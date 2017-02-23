package com.example.pawe.firstdb;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    public final static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    public final static String ISEDIT_EXTRA = "com.example.pawe.firstdb.ifedit";
    public final static String EDIT_ID_EXTRA = "com.example.pawe.firstdb.id";
    public final static String OLD_PATH_EXTRA = "com.example.pawe.firstdb.oldpath";
    public final static String PATH_EXTRA = "com.example.pawe.firstdb.path";
    public final static String FIRST_NAME_EXTRA = "com.example.pawe.firstdb.firstname";
    public final static String LAST_NAME_EXTRA = "com.example.pawe.firstdb.lastname";
    public final static String DATE_EXTRA = "com.example.pawe.firstdb.date";
    private final String TAG = "MainActivity";
    private boolean isEdit;
    private long editId;
    private DatabaseHelper mDbHelper = new DatabaseHelper(this);
    private CustomAdapter customAdapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FORMATTER.setLenient(false);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        listView = (ListView) findViewById(android.R.id.list);
        listView.setEmptyView(findViewById(android.R.id.empty));
        mDbHelper = new DatabaseHelper(this);
        customAdapter = new CustomAdapter(this, getCursorForCustomAdapter());
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.e(TAG, "onItemClick: " + String.valueOf(id));
            }
        });
        registerForContextMenu(listView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        customAdapter.changeCursor(getCursorForCustomAdapter());
    }

    public void addPerson(View view)
    {
        Intent intent = new Intent(this, AddUserActivity.class);
        isEdit = false;
        intent.putExtra(ISEDIT_EXTRA, isEdit);
        startActivity(intent);
    }

    private boolean deleteRowById(long rowId)
    {
        db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.DB._ID,
                DatabaseContract.DB.COLUMN_IMAGE_PATH
        };
        String selection = DatabaseContract.DB._ID + " = ?";
        String[] selectionArgs = {Long.toString(rowId)};
        Cursor cursor = db.query(
                DatabaseContract.DB.TABLE_PERSONS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_IMAGE_PATH));
        File file = new File(imagePath);
        file.delete();
        cursor.close();
        db = mDbHelper.getWritableDatabase();
        return db.delete(DatabaseContract.DB.TABLE_PERSONS, selection, selectionArgs) > 0;
    }

    private Cursor getCursorForCustomAdapter()
    {
        db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.DB._ID,
                DatabaseContract.DB.COLUMN_FIRSTNAME,
                DatabaseContract.DB.COLUMN_LASTNAME,
                DatabaseContract.DB.COLUMN_DATE,
                DatabaseContract.DB.COLUMN_IMAGE_PATH
        };
        Cursor cursor = db.query(
                DatabaseContract.DB.TABLE_PERSONS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list)
        {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            editId = customAdapter.getItemId(adapterContextMenuInfo.position);
            menu.add(Menu.NONE, 0, Menu.NONE, R.string.edit);
            menu.add(Menu.NONE, 1, Menu.NONE, R.string.delete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0)
        {
            isEdit = true;
            Intent intent = new Intent(this, AddUserActivity.class);
            intent.putExtra(EDIT_ID_EXTRA, editId);
            intent.putExtra(ISEDIT_EXTRA, isEdit);
            startActivity(intent);
        }
        else if (menuItemIndex == 1)
        {
            if (deleteRowById(editId))
            {
                Toast.makeText(this, R.string.dataDeleted, Toast.LENGTH_SHORT).show();
                customAdapter.changeCursor(getCursorForCustomAdapter());
            }
            else
            {
                Toast.makeText(this, "Błąd przy usuwaniu danych", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    public void setLang(String lang)
    {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        Locale.setDefault(myLocale);
        res.updateConfiguration(conf,dm);
        Intent refresh = new Intent(this,MainActivity.class);
        startActivity(refresh);
        finish();
    }

    public void changeLanguageOnClick(View view)
    {
        Locale locale = Locale.getDefault();
        Locale enLocale = new Locale("en");
        if (locale.getLanguage().equals(enLocale.getLanguage()))
        {
            setLang("pl");
        }
        else
        {
            setLang("en");
        }
    }

    private class CustomAdapter extends CursorAdapter // potrzebne do poprawnego wyświetlania się listView
    {
        private LayoutInflater mLayoutInflater; // dynamicznie ładowany layout z xmla za pomocą layoutInflater na systemie android

        public CustomAdapter(Context context, Cursor cursor)
        {
            super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            View view = mLayoutInflater.inflate(R.layout.listview_item, parent, false);
            return view;
        }

        @Override
        public void bindView(View v, Context context, Cursor cursor)
        {
            String firstName = cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_FIRSTNAME));
            String lastName = cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_LASTNAME));
            String date = cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_DATE));
            String imagePath = cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_IMAGE_PATH));
            TextView textViewFirstName = (TextView) v.findViewById(R.id.textView_firstName_lastName_listView);
            TextView textViewDate = (TextView) v.findViewById(R.id.textView_date_listView);
            ImageView imageViewPhoto = (ImageView) v.findViewById(R.id.imageView_photo_listView);

            if (textViewFirstName != null)
            {
                textViewFirstName.setText(firstName + " " + lastName);
            }
            if (textViewDate != null)
            {
                textViewDate.setText(date);
            }
            if (imageViewPhoto != null && !imagePath.equals(""))
            {
                Glide.with(context).load(imagePath).into(imageViewPhoto);
            }
        }
    }
}
