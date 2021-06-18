package com.cplauncher.utils;

import java.io.IOException;

public class Utils
{

    public static void executeExternalApplication(String... args)
    {
        try
        {
            Runtime.getRuntime().exec(args);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
