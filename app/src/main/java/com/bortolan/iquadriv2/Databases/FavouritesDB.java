package com.bortolan.iquadriv2.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem;

import java.util.LinkedList;
import java.util.List;

public class FavouritesDB extends SQLiteOpenHelper {
    private final static String TABLE = "Schedules";

    private final String columns[] = new String[]{"id", "name", "url"};

    public FavouritesDB(Context context) {
        super(context, DB.NAME, null, DB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                columns[0] + " integer primary key autoincrement," +
                columns[1] + " text," +
                columns[2] + " text" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void add(GitHubItem item) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        /**START**/
        ContentValues values = new ContentValues();
        values.put(columns[1], item.getName());
        values.put(columns[2], item.getUrl());
        db.insert(TABLE, null, values);
        /**END**/

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public boolean isFavourite(GitHubItem item) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE + " WHERE " + columns[1] + "=? OR " + columns[2] + "=?", new String[]{item.getName(), item.getUrl()});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public List<GitHubItem> getAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + columns[1] + "," + columns[2] + " FROM " + TABLE, null);
        List<GitHubItem> list = new LinkedList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new GitHubItem(c.getString(0), c.getString(1)));
        }

        c.close();
        return list;
    }

    public boolean remove(GitHubItem remove) {
        return getWritableDatabase().delete(TABLE, columns[1] + "=? OR " + columns[2] + "=?", new String[]{remove.getName(), remove.getUrl()}) != 0;
    }
}
