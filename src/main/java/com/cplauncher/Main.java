package com.cplauncher;

import com.cplauncher.hotkey.Hotkey;
import com.cplauncher.hotkey.HotkeyManager;
import com.cplauncher.items.matchers.MatchersManager;
import com.cplauncher.ui.inputselector.InputSelector;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main
{
    private MatchersManager matchersManager;
    private InputSelector inputSelectorDialog;

    public static void main(String[] args) throws IOException, AWTException
    {
        new Main().main();
    }

    private HotkeyManager hotkeyManager = new HotkeyManager();

    private void main() throws IOException, AWTException
    {
        System.out.println("Started CPLauncher");
        matchersManager = new MatchersManager();
        inputSelectorDialog = new InputSelector(matchersManager);
        createTray();
        matchersManager.initMatchers();
        registerShowLauncherHotkey();
    }

    private void registerShowLauncherHotkey()
    {
        hotkeyManager.register("hotkey.show.launcher", new Hotkey(KeyEvent.VK_SPACE, false, false, false, true), new HotKeyListener()
        {
            String[] rootTag = new String[]{"root"};

            @Override
            public void onHotKey(HotKey hotKey)
            {
                inputSelectorDialog.setMatcher(matchersManager.getMatchersByTags(rootTag));
                inputSelectorDialog.toggleShowDialog();
            }
        });
    }

    private void createTray() throws IOException, AWTException
    {
        BufferedImage icon = ImageIO.read(Main.class.getResourceAsStream("/com/cplauncher/resources/icons/keyboard.png"));

        final TrayIcon trayIcon = new TrayIcon(icon, "CPLauncher");
        PopupMenu popupMenu = new PopupMenu("");
        MenuItem showPreferencesMenu = new MenuItem("Show preferences...");
        showPreferencesMenu.addActionListener(e -> {
            System.out.println("Show preferences");
            trayIcon.displayMessage("Preferences", "Message text", TrayIcon.MessageType.INFO);
        });

        popupMenu.add(showPreferencesMenu);

        MenuItem exitMenu = new MenuItem("Exit");
        exitMenu.addActionListener(e -> {
            System.exit(0);
        });

        popupMenu.add(exitMenu);

        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addActionListener(e -> System.out.println("Tray action listener"));
        SystemTray.getSystemTray().add(trayIcon);
    }
/*
    private static JFrame createTestWindow()
    {
        System.out.println("Create test window");
        JFrame frame = new JFrame();
        frame.setTitle("Test window");
        frame.setSize(200, 200);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));

        HotKeyEditor hotKeyChooser = new HotKeyEditor();
        frame.add(hotKeyChooser);
        frame.pack();
        frame.setVisible(true);
        System.out.println("Test window created");
        return frame;
    }
    */
}
