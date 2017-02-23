package com.example.pawe.firstdb;

    import android.provider.BaseColumns;

    public final class DatabaseContract
    {
        private DatabaseContract()
        {

        }
        public static class DB implements BaseColumns
        {
            public static final String TABLE_PERSONS = "persons";
            public static final String COLUMN_FIRSTNAME = "firstname";
            public static final String COLUMN_LASTNAME = "lastname";
            public static final String COLUMN_DATE = "data";
            public static final String COLUMN_IMAGE_PATH = "img_path";
        }
}
