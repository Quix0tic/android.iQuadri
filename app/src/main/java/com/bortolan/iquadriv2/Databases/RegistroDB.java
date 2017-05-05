package com.bortolan.iquadriv2.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bortolan.iquadriv2.Interfaces.Average;
import com.bortolan.iquadriv2.Interfaces.Mark;
import com.bortolan.iquadriv2.Interfaces.MarkSubject;

import java.util.ArrayList;
import java.util.List;

public class RegistroDB extends SQLiteOpenHelper {
    private static int VERSION = 1;
    private static RegistroDB instance = null;

    public RegistroDB(Context c) {
        super(c, "RegistroDB", null, VERSION);
    }

    public static RegistroDB getInstance(Context c) {
        if (instance == null) instance = new RegistroDB(c);
        return instance;
    }

    @Override
    public synchronized void close() {
        super.close();
        instance = null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE marks (id TEXT PRIMARY KEY, subject TEXT NOT NULL, mark TEXT NOT NULL, description TEXT, date INTEGER NOT NULL, type TEXT NOT NULL, period TEXT NOT NULL, not_significant INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addMarks(List<MarkSubject> markSubjects) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String name;
        db.delete("marks", null, null);
        for (MarkSubject subject : markSubjects) {
            name = subject.getName();
            for (Mark mark : subject.getMarks()) {
                db.execSQL("INSERT OR IGNORE INTO marks VALUES(?,(?),?,?,?,?,?,?)", new Object[]{mark.getHash(), name, mark.getMark(), mark.getDesc(), mark.getDate().getTime(), mark.getType(), mark.getQ(), mark.isNs() ? 1 : 0});
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Average> getAverages(Period period, String sort_by) {
        List<Average> avg = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {};
        if (period != Period.ALL)
            args = new String[]{period.getValue()};
        Cursor c = db.rawQuery("SELECT subject, AVG(marks.mark) as _avg, COUNT(marks.mark) FROM marks WHERE marks.not_significant=0 " + ((period != Period.ALL) ? "AND marks.period=?" : "") + " GROUP BY subject " + sort_by, args);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            avg.add(new Average(c.getString(0), 0, c.getFloat(1), c.getInt(2), 7));
        }
        c.close();
        return avg;
    }

    public boolean isSecondPeriodStarted() {
        boolean second = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM marks WHERE period='q3'", null);
        second = c.moveToFirst();
        c.close();
        return second;
    }

    public enum Period {
        FIRST("q1"),
        SECOND("q3"),
        ALL("");
        private final String id;

        Period(String id) {
            this.id = id;
        }

        public String getValue() {
            return id;
        }
    }
}
