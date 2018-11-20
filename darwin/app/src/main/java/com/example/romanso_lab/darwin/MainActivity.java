package com.example.romanso_lab.darwin;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.romanso_lab.darwin.Help.Connection;
import com.example.romanso_lab.darwin.Help.LogScroller;
import com.example.romanso_lab.darwin.Help.Logger;
import com.example.romanso_lab.darwin.Help.ViewToEnDis;
import com.example.romanso_lab.darwin.Task.ShellSSH;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Button> controlButtons;

    private ListView logsLV;
    private LogScroller logScroller;

    private EditText ipET;
    private EditText portET;
    private EditText userET;
    private EditText passwordET;

    private Button psAuxDemoB;
    private Button killDemoB;

    private Button roscoreB;
    private Button rosControlB;

    private Button connectCMDB;
    private Button executeCMDB;
    private EditText executeCMDET;

    private Button puppetB;

    private ShellSSH cmdShell;
    private ShellSSH rosControl;

    private void initialize() {
        this.logsLV         = (ListView) findViewById(R.id.logs_lv);

        this.logScroller = new LogScroller(this.logsLV);
        this.logScroller.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Logger.init(this);
        this.logsLV.setAdapter(Logger.getAdapter());

        this.ipET       = (EditText) findViewById(R.id.ip_et);
        Connection.host = this.ipET.getText().toString();
        this.portET     = (EditText) findViewById(R.id.port_et);
        Connection.port = Integer.parseInt(this.portET.getText().toString());
        this.userET     = (EditText) findViewById(R.id.user_et);
        Connection.user = this.userET.getText().toString();
        this.passwordET = (EditText) findViewById(R.id.password_et);
        Connection.password = this.passwordET.getText().toString();

        this.psAuxDemoB     = (Button) findViewById(R.id.ps_aux_kill_demo_b);
        this.killDemoB      = (Button) findViewById(R.id.kill_demo_b);

        this.roscoreB       = (Button) findViewById(R.id.roscore_b);
        this.rosControlB    = (Button) findViewById(R.id.ros_control_b);

        this.connectCMDB    = (Button) findViewById(R.id.connect_cmd_b);
        this.executeCMDB    = (Button) findViewById(R.id.execute_cmd_b);
        this.executeCMDET   = (EditText) findViewById(R.id.execute_cmd_et);

        this.puppetB        = (Button) findViewById(R.id.puppet_b);

        this.controlButtons = new ArrayList<>();

        this.cmdShell = new ShellSSH("CMD", this.viewsCMD());
//        this.control = new ShellSSH("CONTROL", this.rosControlB);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialize();
    }

    public void connectCMDButtonOnClick(View v) {
        this.connectCMDB.setEnabled(false);
        if (this.cmdShell.getStatus() == AsyncTask.Status.FINISHED) {
            this.cmdShell = new ShellSSH("CMD", this.viewsCMD());
        }
        this.cmdShell.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void executeCMDButtonOnClick(View v) {
        String cmd = this.executeCMDET.getText().toString().concat("\n");
        this.cmdShell.execCMD(cmd);
    }

    public void roscoreButtonOnClick(View v) {
    }

    public void clearCMDButtonOnClick(View v) {
        this.executeCMDET.setText("");
    }

    public void psAuxGrepDemoButtonOnClick(View v) {
        this.executeCMDET.setText("ps aux | grep demo");
    }

    public void killDemoButtonOnClick(View v) {
        this.executeCMDET.setText("sudo killall demo");
    }

    public void puppetButtonOnClick(View v) {

    }

    public void ctrlCButtonOnClick(View v) {
        if (this.cmdShell != null) {
            if (this.cmdShell.getStatus() != AsyncTask.Status.FINISHED) {
                this.cmdShell.cancel(true);
            }
        }
    }

    public void robotControlButtonOnClick(View v) {
//        this.rosControlB.setEnabled(false);
//        if (this.control.getStatus() == AsyncTask.Status.FINISHED) {
//            this.control = new ShellSSH("CONTROL", this.rosControlB);
//        }
//        this.control.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void clearLogsButtonOnClick(View v) {
        Logger.clearLog();
    }

    private ArrayList<ViewToEnDis> viewsCMD() {
        ArrayList<ViewToEnDis> views = new ArrayList<>();
        views.add(new ViewToEnDis(this.executeCMDET, false));
        views.add(new ViewToEnDis(this.executeCMDB, false));
        views.add(new ViewToEnDis(this.psAuxDemoB, false));
        views.add(new ViewToEnDis(this.killDemoB, false));
        views.add(new ViewToEnDis(this.connectCMDB, true));
        return views;
    }
}
