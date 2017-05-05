package com.bortolan.iquadriv2.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bortolan.iquadriv2.Interfaces.Mark;
import com.bortolan.iquadriv2.Interfaces.MarkSubject;
import com.bortolan.iquadriv2.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Methods {

    public final static String[] CATEGORY = new String[]{"genitori", "studenti", "ata", "docenti"};
    public final static String[] PERIOD = new String[]{"q1", "q3"};

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String beautifyName(String name) {
        if (!isEmptyOrNull(name))
            return name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1).toLowerCase();
        else return name;
    }

    private static boolean isEmptyOrNull(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isAppInstalled(Context c, String uri) {
        PackageManager pm = c.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    public static String MessaggioVoto(float Obb, float media, int voti) {
        // Calcolo
        if (Obb > 10 || media > 10)
            return "Errore"; // Quando l'obiettivo o la media sono > 10
        if (Obb >= 10 && media < Obb)
            return "Impossibile raggiungere la media del " + media; // Quando l'obiettivo è 10 (o più) e la media è < 10 (non si potrà mai raggiungere)
        double[] array = {0.75, 0.5, 0.25, 0};
        int index = 0;
        float sommaVotiDaPrendere;
        double[] votiMinimi = new double[5];
        double diff;
        double diff2;
        double resto = 0;
        double parteIntera;
        double parteDecimale;
        try {
            do {
                index = index + 1;
                sommaVotiDaPrendere = (Obb * (voti + index)) - (media * voti);
            } while ((sommaVotiDaPrendere / index) > 10);
            for (int i = 0; i < index; i = i + 1) {
                votiMinimi[i] = (sommaVotiDaPrendere / index) + resto;
                resto = 0;
                parteIntera = Math.floor(votiMinimi[i]);
                parteDecimale = (votiMinimi[i] - parteIntera) * 100;
                if (parteDecimale != 25 && parteDecimale != 50 && parteDecimale != 75) {
                    int k = 0;
                    do {
                        diff = votiMinimi[i] - (parteIntera + array[k]);
                        k++;
                    } while (diff < 0);
                    votiMinimi[i] = votiMinimi[i] - diff;
                    resto = diff;
                }
                if (votiMinimi[i] > 10) {
                    diff2 = votiMinimi[i] - 10;
                    votiMinimi[i] = 10;
                    resto = resto + diff2;
                }
            }
        } catch (Exception e) {
            return "Obiettivo non raggiungibile";
        }
        // Stampa
        String toReturn;
        if (votiMinimi[0] <= 2)
            return "Puoi stare tranquillo"; // Quando i voti da prendere sono negativi
        if (votiMinimi[0] <= Obb)
            toReturn = "Non prendere meno di " + votiMinimi[0];
        else {
            toReturn = "Devi prendere almeno ";
            for (double aVotiMinimi : votiMinimi) {
                if (aVotiMinimi != 0) {
                    toReturn = toReturn + aVotiMinimi + ", ";
                }
            }
            toReturn = toReturn.substring(0, toReturn.length() - 2);
        }
        return toReturn;
    }

    public static void setAlarm(AlarmManager alarmManager, SharedPreferences preferences, PendingIntent operation) {
        Log.d("ALARM", "SET NOTIFICATION INEXACT REPEATING");

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, Long.parseLong(preferences.getString("notify_frequency", String.valueOf(AlarmManager.INTERVAL_HOUR))), operation);

        //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10, 60000, operation);
    }

    private static int getMarkColor(float voto, float voto_obiettivo) {
        if (voto >= voto_obiettivo)
            return R.color.greenmaterial;
        else if (voto < 5)
            return R.color.redmaterial;
        else if (voto >= 5 && voto < 6)
            return R.color.orangematerial;
        else
            return R.color.lightgreenmaterial;
    }

    public static List<MarkSubject> getMarksOfThisPeriod(List<MarkSubject> markssubject, String p) {
        if (p == null) return markssubject;
        List<MarkSubject> marksSub = new ArrayList<>();
        for (MarkSubject s : markssubject) {
            List<Mark> marks = new ArrayList<>();
            for (Mark m : s.getMarks())
                if (m.getQ().equals(p))
                    marks.add(m);

            if (!marks.isEmpty())
                marksSub.add(new MarkSubject(s.getName(), marks));
        }

        return marksSub;
    }

    static boolean removeItemNotInCategory(JSONArray cat, SharedPreferences preferences) throws JSONException {
        Set<String> user_categories = getCategoriesSettings(preferences);
        boolean remove = true;
        for (int j = 0; j < cat.length() && remove; j++) {                            //PER OGNI CATEGORIA IN UN ITEM
            if (user_categories != null) {
                for (int k = 0; k < user_categories.size() && remove; k++) {              //PER OGNI CATEGORIA IN PREFERENCES
                    if (user_categories.contains((cat.getJSONObject(j)).getString("content"))) {
                        remove = false;
                    }
                }
            }
        }
        return remove;
    }

    private static Set<String> getCategoriesSettings(SharedPreferences preferences) {
        List<String> settings = new ArrayList<>();

        if (preferences.getBoolean("studenti", true)) settings.add("Studenti");
        if (preferences.getBoolean("ata", true)) settings.add("ATA");
        if (preferences.getBoolean("docenti", true)) settings.add("Docenti");
        if (preferences.getBoolean("genitori", true)) settings.add("Genitori");

        return new HashSet<>(settings);
    }

    public static String capitalize(String str) {
        return capitalize(str, (char[]) null);
    }

    public static String capitalize(String str, char... delimiters) {
        int delimLen = delimiters == null ? -1 : delimiters.length;
        if (!TextUtils.isEmpty(str) && delimLen != 0) {
            char[] buffer = str.toCharArray();
            boolean capitalizeNext = true;

            for (int i = 0; i < buffer.length; ++i) {
                char ch = buffer[i];
                if (isDelimiter(ch, delimiters)) {
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer[i] = Character.toTitleCase(ch);
                    capitalizeNext = false;
                }
            }

            return new String(buffer);
        } else {
            return str;
        }
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        } else {
            char[] arr$ = delimiters;
            int len$ = delimiters.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                char delimiter = arr$[i$];
                if (ch == delimiter) {
                    return true;
                }
            }

            return false;
        }
    }

    public static int getMediaColor(float media, String tipo, float voto_obiettivo) {
        return getMarkColor(media, voto_obiettivo);
    }


    public static int getMediaColor(float media, float voto_obiettivo) {
        return getMediaColor(media, "Generale", voto_obiettivo);
    }

    public static float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}
