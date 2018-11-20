package com.example.romanso_lab.darwin.Task;

import com.example.romanso_lab.darwin.Help.Connection;
import com.example.romanso_lab.darwin.Help.Logger;
import com.example.romanso_lab.darwin.Help.ViewToEnDis;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by romanso_lab on 05.04.17.
 */

public class CMDShellSSH extends ShellSSH {

    public CMDShellSSH(String shellName, ArrayList<ViewToEnDis> views) {
        super(shellName, views);
    }

    private ArrayList<ViewToEnDis> views;

    private Queue<String> cmds;

    private DataOutputStream out;
    private InputStream in;

    private JSch jsch;
    private Session session;
    private ChannelShell channel;
    private String shellName;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Logger.log(this.shellName + " :: Task has finished.");
        this.enableDisableViews(true);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (values.length > 1) {
            this.enableDisableViews(false);
        } else {
            Logger.log(values[0]);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            publishProgress(this.shellName + " :: JSch init.");
            this.jsch = new JSch();
            publishProgress(this.shellName + " :: JSch created.");

            publishProgress(this.shellName + " :: Trying to get session.");
            this.session = this.jsch.getSession(Connection.user, Connection.host);
            publishProgress(this.shellName + " :: Got session.");

            this.session.setPassword(Connection.password);
            this.session.setConfig("StrictHostKeyChecking", "no");

            publishProgress(this.shellName + " :: Starting session.");
            this.session.connect();
            publishProgress(this.shellName + " :: Session has been started.");

            publishProgress(this.shellName + " :: Opening shell channel.");
            this.channel = (ChannelShell) this.session.openChannel("shell");
            publishProgress(this.shellName + " :: Channel has been opened.");

            publishProgress(this.shellName + " :: Connecting to channel.");
            this.channel.connect();
            publishProgress(this.shellName + " :: Connected to channel.");
            publishProgress(this.shellName + " :: Waiting for a command.");

            in = channel.getInputStream();
            out = new DataOutputStream(this.channel.getOutputStream());
        }
        catch (JSchException e) {
            publishProgress(this.shellName + " :: <<< JSCH PROBLEMS >>>");
            publishProgress(this.shellName + " :: EXCEPTION :: " + e.getMessage());
            e.printStackTrace();
            this.cancel(true);
        } catch (IOException e) {
            publishProgress(this.shellName + " :: <<< IO INIT PROBLEMS >>>");
            publishProgress(this.shellName + " :: EXCEPTION :: " + e.getMessage());
            e.printStackTrace();
        }

        publishProgress("enable", "disable");

        Thread commander = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!ShellSSH.this.isCancelled()) {
                        if (!ShellSSH.this.cmds.isEmpty()) {
                            out.writeBytes(ShellSSH.this.cmds.peek());
                            out.flush();
                            ShellSSH.this.cmds.remove();
                        }
                        Thread.sleep(50);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        commander.start();

        while (!isCancelled()) {
            byte[] tmp = new byte[1024];
            try {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    publishProgress(this.shellName + " :: " + new String(tmp, 0, i));
                }
                Thread.sleep(100);
            } catch (IOException e) {
                publishProgress(this.shellName + " :: <<< IO RUN PROBLEMS >>>");
                publishProgress(this.shellName + " :: EXCEPTION :: " + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void execCMD(String cmd) {
        this.cmds.add(cmd);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Logger.log(this.shellName + " :: Task has been canceled.");
        this.enableDisableViews(true);
    }

    private void enableDisableViews(boolean enable) {
        if (enable) {
            for (ViewToEnDis v : this.views) {
                v.view.setEnabled(v.enable);
            }
        } else {
            for (ViewToEnDis v : this.views) {
                v.view.setEnabled(!v.enable);
            }
        }
    }
}
