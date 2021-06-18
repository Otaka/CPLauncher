package com.cplauncher.hotkey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class HotKeyEditor extends JPanel
{
    private Hotkey hotkey = new Hotkey(0, 0);
    private JTextField hotkeyField = new JTextField();
    private HotkeyChangedListener hotkeyChangedListener;

    public HotKeyEditor()
    {
        setLayout(new BorderLayout());
        setFocusable(false);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        setBackground(Color.WHITE);
        hotkeyField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        hotkeyField.setEditable(false);
        hotkeyField.setFont(hotkeyField.getFont().deriveFont(20.0f));
        hotkeyField.setBorder(null);
        setPreferredSize(new Dimension((int)(calculateMaxHotkeyWidth().getWidth() + 10), 40));

        KeyAdapter keyListener = new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                e.consume();
                int keyCode = e.getKeyCode();
                if (keyCode == 0 || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_META || keyCode == KeyEvent.VK_ALT)
                {
                    return;
                }

                fireHotkeyChanged();
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                e.consume();
                int keyCode = e.getKeyCode();
                if (keyCode == 0 || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_META || keyCode == KeyEvent.VK_ALT)
                {
                    return;
                }
                /*
                System.out.println("\n");
                System.out.println("Code   :" + e.getKeyCode());
                System.out.println("ExtCode:" + e.getExtendedKeyCode());
                System.out.println("ModifEx:" + e.getModifiersEx());
                System.out.println("Modif  :" + e.getModifiers());
                System.out.println("Param  :" + e.paramString());
                System.out.println("KeyText:" + KeyEvent.getKeyText(e.getKeyCode()));
                System.out.println("ModifS :" + KeyEvent.getKeyModifiersText(e.getModifiers()));
                */
                setHotkey(new Hotkey(e.getKeyCode(), e.getModifiers()));
            }
        };
        hotkeyField.addKeyListener(keyListener);
        hotkeyField.setComponentPopupMenu(createPopupMenu());
        add(hotkeyField);
        //how to create padding from left?
        JPanel paddingPanel = new JPanel();
        paddingPanel.setBackground(Color.WHITE);
        paddingPanel.setPreferredSize(new Dimension(5, 0));
        add(paddingPanel, BorderLayout.WEST);

    }

    private JPopupMenu createPopupMenu()
    {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem removeHotkey = new JMenuItem("Remove hotkey");
        removeHotkey.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setHotkey(new Hotkey(0, 0));
                fireHotkeyChanged();
            }
        });
        popupMenu.add(removeHotkey);
        return popupMenu;
    }

    public void setHotkeyChangedListener(HotkeyChangedListener hotkeyChangedListener)
    {
        this.hotkeyChangedListener = hotkeyChangedListener;
    }

    private void fireHotkeyChanged()
    {
        if (hotkeyChangedListener != null)
        {
            hotkeyChangedListener.hotkeyChanged(hotkey);
        }
    }

    public void setHotkey(Hotkey hotkey)
    {
        this.hotkey = hotkey;
        hotkeyField.setText(hotkey.toString());
    }

    public Hotkey getHotkey()
    {
        return hotkey;
    }

    @Override
    public void requestFocus()
    {
        hotkeyField.requestFocus();
    }

    private Rectangle2D calculateMaxHotkeyWidth()
    {
        FontMetrics metrics = hotkeyField.getFontMetrics(hotkeyField.getFont());
        return metrics.getStringBounds("CTRL+SHIFT+META+ALT+F1", null);
    }
}
