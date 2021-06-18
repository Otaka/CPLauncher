package com.cplauncher.platform;

import java.util.Locale;
import javax.swing.JFrame;

public abstract class OsUtils
{
    private static OsUtils instance;

    public static OsUtils get()
    {
        if (instance == null)
        {
            instance = createOsUtils();
        }
        return instance;

    }

    private static OsUtils createOsUtils()
    {
        switch (_getOS())
        {
            case WINDOWS:
                return new WindowsOsUtils();
            case LINUX:
                break;
            case MAC:
                return new OsxOsUtils();
            case OTHER:
                break;
        }
        throw new RuntimeException("Error while creating osUtils for " + _getOS());
    }

    public enum OS
    {
        WINDOWS, LINUX, MAC, OTHER
    }

    private static OS os = null;

    public OS getOS()
    {
        return OsUtils._getOS();
    }

    public static OS _getOS()
    {
        if (os == null)
        {
            String operationSystem = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            if (operationSystem.contains("win"))
            {
                os = OS.WINDOWS;
            }
            else if (operationSystem.contains("nix") || operationSystem.contains("nux") || operationSystem.contains("aix"))
            {
                os = OS.LINUX;
            }
            else if (operationSystem.contains("mac"))
            {
                os = OS.MAC;
            }
            else
            {
                os = OS.OTHER;
            }
        }
        return os;
    }

    abstract public void bringToFront(JFrame frame);

    abstract public void executeShellCommand(String command);

    abstract public void openFileInFileManager(String path);

    abstract public String keyModifiersToString(boolean shift, boolean control, boolean meta, boolean alt);
}
