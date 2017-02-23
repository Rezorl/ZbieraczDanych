package com.example.pawe.firstdb;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AddUserActivity extends AppCompatActivity
{
    private Boolean flag;
    private DatabaseHelper mDbHelper = new DatabaseHelper(this);
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextDate;
    private DatePickerDialog datePickerDialog;
    private int day;
    private int month;
    private int year;
    private boolean isDateChanged = false;
    private SQLiteDatabase db;
    private Intent intent;
    private Cursor cursor;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        intent = getIntent();
        flag = intent.getBooleanExtra(MainActivity.ISEDIT_EXTRA,false);
        if(flag)
        {
            db = mDbHelper.getReadableDatabase();
            Button bt = (Button)findViewById(R.id.button_addUser);
            bt.setText(R.string.update);
            editTextFirstName = (EditText)findViewById(R.id.editTextName);
            editTextLastName = (EditText)findViewById(R.id.editTextSurname);
            editTextDate = (EditText) findViewById(R.id.editTextDate);
            id = intent.getLongExtra(MainActivity.EDIT_ID_EXTRA,-1);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    DatabaseContract.DB._ID,
                    DatabaseContract.DB.COLUMN_FIRSTNAME,
                    DatabaseContract.DB.COLUMN_LASTNAME,
                    DatabaseContract.DB.COLUMN_DATE
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
            editTextFirstName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_FIRSTNAME)));
            editTextLastName.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_LASTNAME)));
            editTextDate.setText((cursor.getString(cursor.getColumnIndex(DatabaseContract.DB.COLUMN_DATE))));
            String [] date = editTextDate.getText().toString().split("-");
            day = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1])-1;
            year = Integer.parseInt(date[2]);
            datePickerDialog = new DatePickerDialog(this,onDateSetListener,year,month,day);
            isDateChanged = true;
        }
        else
        {
            datePickerDialog = new DatePickerDialog(this, onDateSetListener, 2016, 0, 1);
        }
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)
                {
                    datePickerDialog.show();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            }
        });
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int _year, int _month, int dayOfMonth)
        {
            year = _year;
            month = _month;
            day = dayOfMonth;
            editTextDate.setText(String.valueOf(day) + "-" + String.valueOf(month+1) + "-" + String.valueOf(year));
            isDateChanged = true;
        }
    };

    public void continueClick(View view)
    {
        editTextFirstName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextSurname);
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String date = editTextDate.getText().toString();
        if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || !isDateChanged)
        {
            Toast.makeText(this, R.string.notEmptyField, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent _intent = new Intent(this,AddPhotoActivity.class);
            _intent.putExtra(MainActivity.FIRST_NAME_EXTRA,firstName);
            _intent.putExtra(MainActivity.LAST_NAME_EXTRA,lastName);
            _intent.putExtra(MainActivity.DATE_EXTRA,date);
            _intent.putExtra(MainActivity.ISEDIT_EXTRA,intent.getBooleanExtra(MainActivity.ISEDIT_EXTRA,false));
            _intent.putExtra(MainActivity.EDIT_ID_EXTRA,intent.getLongExtra(MainActivity.EDIT_ID_EXTRA,-1));
            startActivity(_intent);
        }
    }

    public void dateOnClick(View view)
    {
        datePickerDialog.show();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
