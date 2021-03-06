package com.willy.will.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.willy.will.R;
import com.willy.will.common.controller.App;

public class DBAccess extends SQLiteOpenHelper {

    private static DBAccess dbHelper = null;

    static {
        Context context = App.getContext();
        Resources resources = context.getResources();
        dbHelper = new DBAccess(context, resources.getString(R.string.database_file_name), null, resources.getInteger(R.integer.database_version));
    }

    public static DBAccess getDbHelper() {
        return dbHelper;
    }

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBAccess(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        Resources resources = App.getContext().getResources();
        String query = null;
        // 새로운 테이블 생성
        query = "CREATE TABLE " + resources.getString(R.string.calendar_table) + "( " +
                    resources.getString(R.string.calendar_id_column) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    resources.getString(R.string.calendar_date_column) + " TEXT NOT NULL, " +
                    resources.getString(R.string.item_id_column) + " INTEGER" +
                " );";
        db.execSQL(query);

        db.execSQL("CREATE TABLE " + resources.getString(R.string.group_table) + "( " +
                    resources.getString(R.string.group_id_column) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    resources.getString(R.string.group_name_column) + " TEXT NOT NULL, " +
                    resources.getString(R.string.group_color_column) + " TEXT NOT NULL" +
                " );"
        );
        String noGroupColorStr = String.format("#%08X", (0xFFFFFFFF & resources.getColor(R.color.colorNoGroup, null)));
        db.execSQL("" +
                "INSERT INTO " + resources.getString(R.string.group_table) + "(" +
                    resources.getString(R.string.group_id_column) + ", " +
                    resources.getString(R.string.group_name_column) + ", " +
                    resources.getString(R.string.group_color_column) +
                ") " +
                "VALUES(" +
                    resources.getInteger(R.integer.no_group_id) + ", '" +
                    resources.getString(R.string.no_group) + "', '" +
                    noGroupColorStr +
                "')"
        );

        db.execSQL("CREATE TABLE " + resources.getString(R.string.item_table) + "( " +
                    resources.getString(R.string.item_id_column) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    resources.getString(R.string.group_id_column) + " INTEGER, " +
                    resources.getString(R.string.item_name_column) + " TEXT NOT NULL, " +
                    resources.getString(R.string.item_important_column) + " INTEGER, " +
                    resources.getString(R.string.item_memo_column) + " TEXT, " +
                    resources.getString(R.string.latitude_column) + " TEXT, " +
                    resources.getString(R.string.longitude_column) + " TEXT, " +
                    resources.getString(R.string.done_date_column) + " TEXT, " +
                    resources.getString(R.string.start_date_column) + " TEXT NOT NULL, " +
                    resources.getString(R.string.end_date_column) + " TEXT NOT NULL, " +
                    resources.getString(R.string.to_do_id_column) + " INTEGER," +
                resources.getString(R.string.user_place_name_column) + " TEXT" +
                " );"
        );

        db.execSQL("CREATE TABLE " + resources.getString(R.string.loop_info_table) + "( " +
                    resources.getString(R.string.loop_id_column) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    resources.getString(R.string.to_do_id_column) + " INTEGER NOT NULL, " +
                    resources.getString(R.string.loop_week_column) + " TEXT, " +
                    "UNIQUE (" + resources.getString(R.string.loop_id_column) + ", " + resources.getString(R.string.to_do_id_column) + ")" +
                " );"
        );
    }

    public SQLiteDatabase getDb(){ return this.getWritableDatabase(); }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Resources resources = App.getContext().getResources();

        if(oldVersion <= 1) {
            // DB 삭제하고 새로 만드는 소스 필요함
            return;
        }

        if(oldVersion <= 2) {
            Cursor cursor = db.rawQuery(
                    "SELECT *" +
                            " FROM pragma_table_info('" + resources.getString(R.string.item_table) + "')" +
                            " WHERE name = '" + resources.getString(R.string.item_memo_column) + "'",
                    null
            );
            if(cursor.getCount() == 0) {
                db.execSQL(
                        "ALTER TABLE " +
                                resources.getString(R.string.item_table) +
                                " ADD COLUMN " +
                                resources.getString(R.string.item_memo_column) + " TEXT"
                );
            }
        }

        if(oldVersion <= 3) {
            Cursor cursor = db.rawQuery(
                    "SELECT *" +
                            " FROM pragma_table_info('" + resources.getString(R.string.item_table) + "')" +
                            " WHERE name = '" + resources.getString(R.string.user_place_name_column) + "'",
                    null
            );
            if(cursor.getCount() == 0) {
                db.execSQL(
                        "ALTER TABLE " +
                                resources.getString(R.string.item_table) +
                                " ADD COLUMN " +
                                resources.getString(R.string.user_place_name_column) + " TEXT"
                );
            }
        }
    }
}

