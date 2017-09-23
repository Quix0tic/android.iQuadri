package com.bortolan.iquadriv2.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bortolan.iquadriv2.API.SpaggiariREST.models.Grade;
import com.bortolan.iquadriv2.Interfaces.Average;
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubItem;
import com.bortolan.iquadriv2.Interfaces.GitHub.GitHubResponse;
import com.bortolan.iquadriv2.Interfaces.Libri.Announcement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistroDB extends SQLiteOpenHelper {
    private static int VERSION = 5;
    private static RegistroDB instance = null;
    private SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private RegistroDB(Context c) {
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
        onUpgrade(db, 1, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3)
            db.execSQL("CREATE TABLE schedules(`name` TEXT NOT NULL,`url` TEXT NOT NULL,`group` TEXT NOT NULL)");
        if (oldVersion < 4)
            db.execSQL("CREATE TABLE announcements(uuid TEXT PRIMARY KEY, title TEXT, isbn TEXT, subject TEXT, edition TEXT, grade TEXT, notes TEXT, price INTEGER, createdAt INTEGER, updatedAt INTEGER)");
        if (oldVersion < 5)
            db.execSQL("ALTER TABLE announcements ADD COLUMN phone TEXT");
    }

    /*public void addMarks(List<MarkSubject> markSubjects) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String name;
        db.delete("marks", null, null);
        for (MarkSubject subject : markSubjects) {
            name = subject.getName();
            for (Mark mark : subject.getMarks()) {
                db.execSQL("INSERT OR IGNORE INTO marks VALUES(?,?,?,?,?,?,?,?)", new Object[]{mark.getHash(), name, mark.getMark(), mark.getDesc(), mark.getDate().getTime(), mark.getType(), mark.getQ(), mark.isNs() ? 1 : 0});
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }*/

    public void addMarks(List<Grade> grades) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete("marks", null, null);
        for (Grade mark : grades) {
            try {
                db.execSQL("INSERT OR IGNORE INTO marks VALUES(?,?,?,?,?,?,?,?)", new Object[]{mark.getHash(), mark.getSubjectDesc(), mark.getDecimalValue(), mark.getNotesForFamily(), YMD.parse(mark.getEvtDate()).getTime(), mark.getComponentDesc(), mark.getPeriodPos(), mark.getColor().equals("blue") ? 1 : 0});
            } catch (ParseException e) {
                e.printStackTrace();
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
        boolean second;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM marks WHERE period != '1'", null);
        second = c.moveToFirst();
        c.close();
        return second;
    }

    public void addSchedules(GitHubResponse gitHubResponse) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete("schedules", null, null);
        for (GitHubItem item : gitHubResponse.getClassi()) {
            db.execSQL("INSERT INTO schedules VALUES(?,?,'classe')", new Object[]{item.getName(), item.getUrl()});
        }
        for (GitHubItem item : gitHubResponse.getAule()) {
            db.execSQL("INSERT INTO schedules VALUES(?,?,'aula')", new Object[]{item.getName(), item.getUrl()});
        }
        for (GitHubItem item : gitHubResponse.getProf()) {
            db.execSQL("INSERT INTO schedules VALUES(?,?,'prof')", new Object[]{item.getName(), item.getUrl()});
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public GitHubResponse getSchedules() {
        SQLiteDatabase db = getReadableDatabase();
        GitHubResponse schedules = new GitHubResponse();
        List<GitHubItem> temp = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT name, url FROM schedules WHERE `group`='classe'", null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            temp.add(new GitHubItem(c.getString(0), c.getString(1)));
        schedules.setClassi(new ArrayList<>(temp));
        temp.clear();
        c.close();

        c = db.rawQuery("SELECT name, url FROM schedules WHERE `group`='prof'", null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            temp.add(new GitHubItem(c.getString(0), c.getString(1)));
        schedules.setProf(new ArrayList<>(temp));
        temp.clear();
        c.close();

        c = db.rawQuery("SELECT name, url FROM schedules WHERE `group`='aula'", null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            temp.add(new GitHubItem(c.getString(0), c.getString(1)));
        schedules.setAule(new ArrayList<>(temp));
        temp.clear();
        c.close();

        return schedules;
    }

    public void addAnnouncements(List<Announcement> announcements) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete("announcements", null, null);
        for (Announcement a : announcements) {
            db.execSQL("INSERT OR IGNORE INTO announcements(uuid,title,isbn,subject,edition,grade,notes,price,createdAt,updatedAt,phone) VALUES(?,?,?,?,?,?,?,?,?,?,?)", new Object[]{a.getUnique_id(), a.getTitle(), a.getIsbn(), a.getSubject(), a.getEdition(), a.getGrade(), a.getNotes(), a.getPrice(), a.getCreatedAt().getTime(), a.getUpdatedAt().getTime(), a.getPhone()});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Announcement> getAnnouncements() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT uuid,title,isbn,subject,edition,grade,notes,price,createdAt,updatedAt,phone FROM announcements", null);
        List<Announcement> announcements = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            announcements.add(new Announcement(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(10), c.getInt(7), new Date(c.getLong(8))));
        }

        c.close();
        return announcements;
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
