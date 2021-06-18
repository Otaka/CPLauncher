package com.cplauncher.platform;

import com.cplauncher.utils.Utils;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JFrame;

class OsxOsUtils extends OsUtils
{
    private static ScriptEngine engine = new apple.applescript.AppleScriptEngineFactory().getScriptEngine();

    private static String executeExternalAppleScript(String script)
    {
        try
        {
            Process process = Runtime.getRuntime().exec(new String[]{"/usr/bin/osascript", "-l", "AppleScript"});
            OutputStream stream = process.getOutputStream();
            stream.write(script.getBytes(StandardCharsets.UTF_8));
            stream.flush();
            stream.close();
            int result = process.waitFor();
            return "";
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
/*
        try
        {
            return (String)engine.eval(script);
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }
        */

        return null;
    }

    private static String executeAppleScript(String script)
    {

        try
        {
            return (String)engine.eval(script);
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void bringToFront(JFrame frame)
    {
        executeAppleScript("tell me to activate");
    }

    @Override
    public void executeShellCommand(String command)
    {
        executeExternalAppleScript("tell application \"iTerm\"\n"
                + "\ttell current session of window 1 to write text \"" + command + "\"\n"
                + "end tell");
    }

    @Override
    public void openFileInFileManager(String path)
    {
        Utils.executeExternalApplication("open", "-R", path);
    }

    public String keyModifiersToString(boolean shift, boolean control, boolean meta, boolean alt)
    {
        StringBuilder sb = new StringBuilder();
        if (control)
        {
            sb.append("⌃");
        }
        if (alt)
        {
            if (sb.length() > 0)
            {
                sb.append("+");
            }
            sb.append("⌥");
        }
        if (shift)
        {
            if (sb.length() > 0)
            {
                sb.append("+");
            }
            sb.append("⇧");
        }
        if (meta)
        {
            if (sb.length() > 0)
            {
                sb.append("+");
            }
            sb.append("⌘");
        }
        return sb.toString();
    }
}
