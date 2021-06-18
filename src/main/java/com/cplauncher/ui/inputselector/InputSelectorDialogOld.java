package com.cplauncher.ui.inputselector;

import com.cplauncher.items.AbstractItem;
import com.cplauncher.items.matchers.AbstractItemMatcher;
import com.cplauncher.items.matchers.BookmarksItemMatcher;
import com.cplauncher.items.matchers.FilesItemMatcher;
import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;
import com.cplauncher.items.matchers.ShellCommandsMatcher;
import com.cplauncher.platform.OsUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.StringUtils;

public class InputSelectorDialogOld extends JFrame
{
    public static JFrame instance;
    private JCpList<DirectoryItem> itemOptions = new JCpList<>();
    private JCpList<DirectoryItem> selectedItemsStack = new JCpList<>();

    private JTextField textField = new JTextField();

    private List<AbstractItemMatcher> allMatchers = new ArrayList<>();
    private Map<String, List<AbstractItemMatcher>> tagToItemMatchers = new HashMap<>();
    private List<AbstractItemMatcher> activeMatchersCache = new ArrayList<>();
    private boolean activeMatchersCacheDirty = true;
    private boolean enableDocumentListener = true;

    public InputSelectorDialogOld()
    {
        instance = this;
        setUndecorated(true);
        setTitle("CPLauncher");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        textField.setPreferredSize(new Dimension(400, 25));
        initMainTextField();
        JScrollPane itemsOptionsScroll = new JScrollPane(itemOptions);
        itemsOptionsScroll.setPreferredSize(new Dimension(400, 200));

        itemOptions.setBackground(Color.LIGHT_GRAY);
        itemOptions.setFocusable(false);
        itemOptions.setCellRenderer(new ListItemRenderer());

        JScrollPane selectedItemsScroll = new JScrollPane(selectedItemsStack);
        selectedItemsScroll.setPreferredSize(new Dimension(400, 50));
        selectedItemsStack.setBackground(Color.LIGHT_GRAY);
        selectedItemsStack.setFocusable(false);
        selectedItemsStack.setCellRenderer(new ListItemRenderer());

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(selectedItemsScroll);
        getContentPane().add(textField);
        getContentPane().add(itemsOptionsScroll);
        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }

            @Override
            public void windowActivated(WindowEvent e)
            {
                textField.requestFocus();
            }
        });

        System.out.println("Configure matchers");
        configureMatchers();
    }

    public void toggleShowMainWindow()
    {
        if (isVisible())
        {
            hideMainWindow();
        }
        else
        {
            showMainWindow();
        }
    }

    public void hideMainWindow()
    {
        setVisible(false);
    }

    public void showMainWindow()
    {
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            System.out.println("Bring to front");
            OsUtils.get().bringToFront(this);
        });
    }

    private void configureMatchers()
    {
        addMatcher(new FilesItemMatcher(new File("/Applications/"), new File("/Users/dmitry/Documents/books/")));
        addMatcher(new ShellCommandsMatcher());
        addMatcher(new BookmarksItemMatcher());
    }

    private void addMatcher(AbstractItemMatcher matcher)
    {
        for (String tag : matcher.getMatchableTags())
        {
            List<AbstractItemMatcher> tags = tagToItemMatchers.computeIfAbsent(tag, (t) -> new ArrayList<>());
            if (!tags.contains(matcher))
            {
                tags.add(matcher);
            }
        }
        allMatchers.add(matcher);
    }

    private void initMainTextField()
    {
        textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        textField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                onTextTyped(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                onTextTyped(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                onTextTyped(textField.getText());
            }
        });
        textField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    nextSelectedItem();
                    e.consume();
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP)
                {
                    previousSelectedItem();
                    e.consume();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    activate(false);
                    e.consume();
                }
                else if (e.getKeyCode() == KeyEvent.VK_TAB)
                {
                    activate(true);
                    e.consume();
                }
            }
        });
    }

    private void setSelectedItem(int index)
    {
        itemOptions.setSelectedIndex(index);
        itemOptions.ensureIndexIsVisible(index);
    }

    private void nextSelectedItem()
    {
        setSelectedItem(Math.min(itemOptions.getLastElementIndex(), itemOptions.getSelectedIndex() + 1));
    }

    private void previousSelectedItem()
    {
        setSelectedItem(Math.max(0, itemOptions.getSelectedIndex() - 1));
    }

    private List<AbstractItemMatcher> getActiveMatchers()
    {
        if (activeMatchersCacheDirty)
        {
            activeMatchersCacheDirty = false;
            List<AbstractItemMatcher> collectedMatchers = calculateActiveMatchers();
            Set<AbstractItemMatcher> intersection = new HashSet<>(collectedMatchers);
            intersection.addAll(collectedMatchers);
            intersection.retainAll(activeMatchersCache);
            for (AbstractItemMatcher currentMatcher : activeMatchersCache)
            {
                if (!intersection.contains(currentMatcher))
                {
                    currentMatcher.onMatcherDeactivated();
                    System.out.println("Deactivated matcher " + currentMatcher.getClass().getSimpleName());
                }
            }
            for (AbstractItemMatcher newMatcher : collectedMatchers)
            {
                if (!intersection.contains(newMatcher))
                {
                    newMatcher.onMatcherActivated();
                    System.out.println("Activated matcher " + newMatcher.getClass().getSimpleName());
                }
            }

            activeMatchersCache = collectedMatchers;
        }
        return activeMatchersCache;
    }

    private void resetActiveMatchersCache()
    {
        activeMatchersCacheDirty = true;
    }

    private List<AbstractItemMatcher> calculateActiveMatchers()
    {
        Set<String> activeTags = new HashSet<>();
        if (selectedItemsStack.isEmpty())
        {
            activeTags.add("root");
        }
        else
        {
            DirectoryItem lastItem = selectedItemsStack.getLastItem();
            if (lastItem.getTags() != null)
            {
                Collections.addAll(activeTags, lastItem.getTags());
            }
        }

        Set<AbstractItemMatcher> matchers = new HashSet<>();
        for (String tag : activeTags)
        {
            List<AbstractItemMatcher> matchersForTag = tagToItemMatchers.get(tag);
            if (matchersForTag != null)
            {
                matchers.addAll(matchersForTag);
            }
        }

        return Arrays.asList(matchers.toArray(new AbstractItemMatcher[0]));
    }

    private DirectoryItem getLastSelectedItem()
    {
        return selectedItemsStack.isEmpty() ? null : selectedItemsStack.getLastItem();
    }

    private void onTextTyped(String text)
    {
        if (enableDocumentListener)
        {
            List<DirectoryItem> result = matchItems(text, getActiveMatchers());
            setResultItemsToList(result);
        }
    }

    private List<DirectoryItem> matchItems(String text, List<AbstractItemMatcher> activeMatchers)
    {
        text = StringUtils.stripStart(text, " \t").toLowerCase();

        ResultItemsList result = new ResultItemsList(text);
        for (AbstractItemMatcher matcher : activeMatchers)
        {
            matcher.match(getLastSelectedItem(), text, result);
        }

        List<AbstractItem> matchedItemsList = result.getItems();
        //sortItemsList(matchedItemsList, text);
        //return matchedItemsList;
        return null;
    }

    private void sortItemsList(List<DirectoryItem> items, String text)
    {
        items.sort((o1, o2) -> {
            int d1 = StringUtils.getLevenshteinDistance(o1.getText(), text);
            int d2 = StringUtils.getLevenshteinDistance(o2.getText(), text);
            if (d1 == d2)
            {
                return o1.getText().compareToIgnoreCase(o2.getText());
            }
            return d1 - d2;
        });
    }

    private void setResultItemsToList(List<DirectoryItem> result)
    {
        itemOptions.clear();
        int maxCount = Math.min(50, result.size());
        for (int i = 0; i < maxCount; i++)
        {
            DirectoryItem item = result.get(i);
            itemOptions.addItem(item);
        }
        setSelectedItem(0);
    }

    private void activate(boolean forceList)
    {
        if (itemOptions.getSelectedIndex() == -1)
        {
            return;
        }

        DirectoryItem parentItem = itemOptions.getSelectedValue();
        selectedItemsStack.addItem(parentItem);
        resetActiveMatchersCache();
        List<DirectoryItem> items = matchItems("", getActiveMatchers());
        DirectoryItem executeItemCandidate = getExecuteCandidate(parentItem, items);
        if (forceList || executeItemCandidate == null)
        {
            setResultItemsToList(items);
        }
        else
        {
            executeItem(executeItemCandidate);
            selectedItemsStack.clear();
            resetActiveMatchersCache();
            setResultItemsToList(Collections.emptyList());
        }

        enableDocumentListener = false;
        textField.setText("");
        enableDocumentListener = true;
    }

    private DirectoryItem getExecuteCandidate(DirectoryItem parentItem, List<DirectoryItem> items)
    {
        /*
        if (parentItem != null && parentItem.getExecutor() != null)
        {
            return parentItem;
        }
        List<DirectoryItem> candidates = searchBestItemToExecute(items);
        if (candidates.size() != 1)
        {
            return null;
        }
        return candidates.get(0);

         */
        return null;
    }

    private List<DirectoryItem> searchBestItemToExecute(List<DirectoryItem> items)
    {
        /*
        List<DirectoryItem> result = items.stream()
                .filter(i -> i.getExecutor() != null)
                .sorted(Comparator.comparingInt(DirectoryItem::getPriority).reversed())
                .collect(Collectors.toList());

        if (result.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }

        int priority = result.get(0).getPriority();
        return items.stream().filter(i -> i.getPriority() == priority).collect(Collectors.toList());

         */
        return null;
    }

    private void executeItem(DirectoryItem item)
    {
        /*
        hideMainWindow();
        SwingUtilities.invokeLater(() -> {
            item.getExecutor().execute(item);
        });

         */
    }
}
