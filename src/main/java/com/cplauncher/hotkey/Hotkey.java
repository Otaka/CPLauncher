package com.cplauncher.hotkey;

import com.cplauncher.platform.OsUtils;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class Hotkey
{
    //HOT KEYS MODIFIERS
    //1 - Shift ⇧
    //2 - Control ⌃
    //4 - META(Command) ⌘
    //8 - Alt(Option) ⌥
    private int modifiers;
    private int keyCode;

    public Hotkey(int keyCode, boolean shift, boolean control, boolean meta, boolean alt)
    {
        this.keyCode = keyCode;
        modifiers = createModifier(shift, control, meta, alt);
    }

    public Hotkey(int keyCode, int modifiers)
    {
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    public int getKeyCode()
    {
        return keyCode;
    }

    public int getModifiers()
    {
        return modifiers;
    }

    @Override
    public String toString()
    {
        if (keyCode == 0)
        {
            return "";
        }
        String modifiersString = OsUtils.get().keyModifiersToString(isShiftPressed(modifiers), isControlPressed(modifiers), isMetaPressed(modifiers), isAltPressed(modifiers));
        if (modifiersString.isEmpty())
        {
            return KeyEvent.getKeyText(keyCode);
        }
        else
        {
            return modifiersString + "+" + KeyEvent.getKeyText(keyCode);
        }
    }

    public static int createModifier(boolean shift, boolean control, boolean meta, boolean alt)
    {
        return ((shift ? 0xFF : 0) & Event.SHIFT_MASK) +
                ((control ? 0xFF : 0) & Event.CTRL_MASK) +
                ((meta ? 0xFF : 0) & Event.META_MASK) +
                ((alt ? 0xFF : 0) & Event.ALT_MASK);
    }

    public static boolean isShiftPressed(int modifier)
    {
        return (modifier & Event.SHIFT_MASK) > 0;
    }

    public static boolean isControlPressed(int modifier)
    {
        return (modifier & Event.CTRL_MASK) > 0;
    }

    public static boolean isMetaPressed(int modifier)
    {
        return (modifier & Event.META_MASK) > 0;
    }

    public static boolean isAltPressed(int modifier)
    {
        return (modifier & Event.ALT_MASK) > 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Hotkey hotkey = (Hotkey)o;

        if (modifiers != hotkey.modifiers)
            return false;
        return keyCode == hotkey.keyCode;
    }

    @Override
    public int hashCode()
    {
        int result = modifiers;
        result = 31 * result + keyCode;
        return result;
    }
}
