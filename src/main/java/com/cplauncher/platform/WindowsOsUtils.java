package com.cplauncher.platform;

import com.cplauncher.utils.Utils;
import javax.swing.JFrame;

public class WindowsOsUtils extends OsUtils
{
    @Override
    public void bringToFront(JFrame frame)
    {
        frame.toFront();
        frame.repaint();
    }

    @Override
    public void executeShellCommand(String command)
    {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public void openFileInFileManager(String path)
    {
        Utils.executeExternalApplication("explorer.exe", "/select", path);
    }

    @Override
    public String keyModifiersToString(boolean shift, boolean control, boolean meta, boolean alt)
    {
        StringBuilder sb = new StringBuilder();
        if (control)
        {
            sb.append("CTRL");
        }
        if (alt)
        {
            if (sb.length() > 0)
            {
                sb.append("+");
            }
            sb.append("ALT");
        }
        if (shift)
        {
            if (sb.length() > 0)
            {
                sb.append("+");
            }
            sb.append("SHIFT");
        }
        if (meta)
        {
            if (sb.length() > 0)
            {
                sb.append("+");
            }
            sb.append("META");
        }
        return sb.toString();
    }
}
