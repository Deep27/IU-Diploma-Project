package com.example.romanso_lab.darwin.Help;

import android.widget.ArrayAdapter;

import com.example.romanso_lab.darwin.MainActivity;
import com.example.romanso_lab.darwin.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by romanso_lab on 05.04.17.
 */

public class Logger {

    private static ArrayList<String> logs;
    private static ArrayAdapter<String> logsAdapter;

    private static DateFormat df = new SimpleDateFormat("HH:mm:ss");

    public static void log(String log) {
        Date date = new Date();
        logs.add(df.format(date) + " :: " + log);
        logsAdapter.notifyDataSetChanged();
        LogScroller.scroll();
    }

    public static ArrayAdapter<String> getAdapter() {
        return logsAdapter;
    }

    public static void clearLog() {
        logs.clear();
        logsAdapter.notifyDataSetChanged();
    }

    public static void init(MainActivity ma) {
        logs = new ArrayList<>();
        logsAdapter = new ArrayAdapter<>(ma, R.layout.log_list_item, logs);
    }
}
