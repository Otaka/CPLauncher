package com.cplauncher.hotkey;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.KeyStroke;

public class HotkeyManager
{
    private Provider hotkeyProvider = Provider.getCurrentProvider(true);
    private Map<KeyStroke, List<HotkeyInfo>> keyStroke2hotkeys = new HashMap<>();
    private Map<String, HotkeyInfo> id2Hotkey = new HashMap<>();

    public void register(String id, Hotkey hotkey, HotKeyListener listener)
    {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(hotkey.getKeyCode(), hotkey.getModifiers());
        HotkeyInfo hotkeyInfo = new HotkeyInfo(keyStroke, hotkey, id, listener);
        unregister(id);
        id2Hotkey.put(id, hotkeyInfo);
        List<HotkeyInfo> hotkeys = keyStroke2hotkeys.computeIfAbsent(keyStroke, (x) -> new ArrayList<>());
        hotkeys.add(hotkeyInfo);
        enable(hotkeyInfo);
    }

    public void unregister(String id)
    {
        HotkeyInfo hotkeyInfo = id2Hotkey.get(id);
        if (hotkeyInfo != null)
        {
            disable(hotkeyInfo);
            List<HotkeyInfo> hotkeysList = keyStroke2hotkeys.get(hotkeyInfo.keyStroke);
            hotkeysList.remove(hotkeyInfo);
            if (hotkeysList.isEmpty())
            {
                keyStroke2hotkeys.remove(hotkeyInfo.keyStroke);
            }
        }
    }

    public void reassign(String id, Hotkey hotkey)
    {
        HotkeyInfo hotkeyInfo = id2Hotkey.get(id);
        if (hotkeyInfo == null)
        {
            System.out.println("Hotkey id " + id + " does not registered yet");
            return;
        }
        unregister(id);
        register(id, hotkey, hotkeyInfo.listener);
    }

    private void enable(HotkeyInfo hotkeyInfo)
    {
        hotkeyProvider.register(hotkeyInfo.keyStroke, hotkeyInfo.listener);
    }

    public void disable(String id)
    {
        HotkeyInfo hotkeyInfo = id2Hotkey.get(id);
        if (hotkeyInfo != null)
        {
            disable(hotkeyInfo);
        }
    }

    private void disable(HotkeyInfo hotkeyInfo)
    {
        if (hotkeyInfo != null)
        {
            hotkeyProvider.unregister(hotkeyInfo.keyStroke);
        }
    }

    public void disableAll()
    {
        System.out.println("Disable all hotkeys");
        for (HotkeyInfo hotkeyInfo : id2Hotkey.values())
        {
            hotkeyProvider.unregister(hotkeyInfo.keyStroke);
        }
    }

    public void enableAll()
    {
        System.out.println("Enable all hotkeys");
        for (HotkeyInfo hotkey : id2Hotkey.values())
        {
            enable(hotkey);
        }
    }

    public static class HotkeyInfo
    {
        String id;
        KeyStroke keyStroke;
        Hotkey hotkey;
        HotKeyListener listener;

        public HotkeyInfo(KeyStroke keyStroke, Hotkey hotkey, String id, HotKeyListener listener)
        {
            this.keyStroke = keyStroke;
            this.hotkey = hotkey;
            this.id = id;
            this.listener = listener;
        }
    }
}
