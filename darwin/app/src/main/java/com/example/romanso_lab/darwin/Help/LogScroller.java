package com.example.romanso_lab.darwin.Help;

import android.os.AsyncTask;
import android.widget.ListView;

/**
 * Created by romanso_lab on 05.04.17.
 */

public class LogScroller extends AsyncTask<Void, Void, Void> {

    private ListView lv;
    private static boolean toScroll;

    public LogScroller(ListView lv) {
        this.lv = lv;
        toScroll = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        while(!isCancelled()) {
            if (toScroll) {
                publishProgress();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        toScroll = false;
        if (Logger.getAdapter().getCount() > 2) {
            this.lv.setSelection(Logger.getAdapter().getCount() - 1);
        }
        super.onProgressUpdate(values);
    }

    public static void scroll() {
        toScroll = true;
    }
}
