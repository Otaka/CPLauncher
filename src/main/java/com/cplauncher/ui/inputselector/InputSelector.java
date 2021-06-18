package com.cplauncher.ui.inputselector;

import com.cplauncher.items.AbstractItem;
import com.cplauncher.items.ActionItem;
import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;
import com.cplauncher.items.matchers.AbstractItemMatcher;
import com.cplauncher.items.matchers.MatchersManager;
import com.cplauncher.platform.OsUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.StringUtils;

public class InputSelector extends JFrame
{
    public static InputSelector instance;
    private JTextField inputField;
    private ItemsListPanel itemsListPanel;
    private JPanel stackedTermsHolderPanel;
    private List<AbstractItemMatcher> activeMatchers = new ArrayList<>();
    private boolean enableDocumentListener = true;
    private List<DirectoryItem> stackedItems = new ArrayList<>();
    private MatchersManager matchersManager;

    public InputSelector(MatchersManager matchersManager) throws HeadlessException
    {
        this.matchersManager = matchersManager;
        instance = this;
        setUndecorated(false);
        setTitle("CPLauncher");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new VerticalLayoutFillWidth().setPadding(5, 5));
        stackedTermsHolderPanel = new JPanel();
        stackedTermsHolderPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        stackedTermsHolderPanel.setMinimumSize(new Dimension(0, 0));
        stackedTermsHolderPanel.setLayout(new VerticalLayoutFillWidth());
        stackedTermsHolderPanel.setOpaque(false);
        getContentPane().add(stackedTermsHolderPanel);

        inputField = createInputField();
        getContentPane().add(inputField);

        itemsListPanel = new ItemsListPanel();
        getContentPane().add(itemsListPanel);
        setBackground(Style.INSTANCE.mainWindowBackground);
        getContentPane().setBackground(Style.INSTANCE.mainWindowBackground);
        getRootPane().setBackground(Color.red);
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
                inputField.requestFocus();
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    private JTextField createInputField()
    {
        JTextField inputField = new JTextField();
        inputField.setBorder(BorderFactory.createEmptyBorder());
        inputField.setBackground(Style.INSTANCE.inputFieldBackground);
        inputField.setForeground(Style.INSTANCE.inputFieldTextColor);
        inputField.setCaretColor(Color.WHITE);
        inputField.setFocusTraversalKeysEnabled(false);
        inputField.setPreferredSize(new Dimension(500, 40));
        inputField.setFont(inputField.getFont().deriveFont(20.f));
        inputField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    activate(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_TAB)
                {
                    activate(true);
                }
                else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    itemsListPanel.selectNext(false);
                    e.consume();
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP)
                {
                    itemsListPanel.selectPrevious(false);
                    e.consume();
                }
            }
        });
        inputField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                onTextTyped(inputField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                onTextTyped(inputField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                onTextTyped(inputField.getText());
            }
        });

        return inputField;
    }

    private void activate(boolean forceList)
    {
        if (itemsListPanel.getSelected() == -1)
        {
            return;
        }

        AbstractItem itemToExecute = itemsListPanel.getSelectedItem();

        if (itemToExecute instanceof ActionItem)
        {
            if(forceList){
                return;// do nothing if it is action item
            }

            setActionItems(Collections.emptyList());
            setText("", false);
            executeItem((ActionItem)itemToExecute);
            return;
        }

        DirectoryItem parentItem = (DirectoryItem)itemToExecute;
        stackedItems.add(parentItem);

        setActiveMatchers(matchersManager.getMatchersByTags(parentItem.getTags()));
        List<AbstractItem> items = matchItems("", getActiveMatchers());
        ActionItem executeItemCandidate = getExecuteCandidate(parentItem, items);
        if (forceList || executeItemCandidate == null)
        {
            setResultItemsToList(items);
        }
        else
        {
            executeItem(executeItemCandidate);
            stackedItems.clear();
            setActiveMatchers(matchersManager.getMatchersByTags(parentItem.getTags()));
            setResultItemsToList(Collections.emptyList());
        }

        setText("", true);
    }

    private ActionItem getExecuteCandidate(DirectoryItem parentItem, List<AbstractItem> items)
    {
        List<ActionItem> candidates = searchBestItemToExecute(items);
        if (candidates.size() != 1)
        {
            return null;
        }
        return candidates.get(0);
    }

    private List<ActionItem> searchBestItemToExecute(List<AbstractItem> items)
    {
        List<ActionItem> result = items.stream()
                .filter(item -> item instanceof ActionItem)
                .map(item -> (ActionItem)item)
                .filter(ActionItem::isDefaultItem)
                .sorted(Comparator.comparingInt(ActionItem::getPriority).reversed())
                .collect(Collectors.toList());

        if (result.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }

        int priority = result.get(0).getPriority();
        return result.stream()
                .filter(i -> i.getPriority() == priority)
                .collect(Collectors.toList());
    }

    private void executeItem(ActionItem item)
    {
        hideDialog();
        SwingUtilities.invokeLater(() -> {
            item.getExecutor().execute(item);
        });
    }

    private void onTextTyped(String text)
    {
        if (enableDocumentListener)
        {
            List<AbstractItem> result = matchItems(text, getActiveMatchers());
            setResultItemsToList(result);
        }
    }

    private List<AbstractItemMatcher> getActiveMatchers()
    {
        return activeMatchers;
    }

    public void setActiveMatchers(List<AbstractItemMatcher> activeMatchers)
    {
        this.activeMatchers = activeMatchers;
    }

    private void setResultItemsToList(List<AbstractItem> result)
    {
        setActionItems(result);
    }

    public void setActionItems(List<? extends AbstractItem> items)
    {
        int actionItemsListPanelHeightBefore = itemsListPanel.getVisibleElementsCount() * itemsListPanel.getElementHeight();
        itemsListPanel.setItems(items);
        int actionItemsListPanelHeightAfter = itemsListPanel.getVisibleElementsCount() * itemsListPanel.getElementHeight();
        int totalHeightClientHeight = inputField.getHeight() + actionItemsListPanelHeightAfter;
        int totalHeight = totalHeightClientHeight + getInsets().top + getInsets().bottom;
        System.out.println(
                "totalH=" + totalHeight + " inputFieldH=" + inputField.getHeight() + " itemsCount=" + itemsListPanel.getVisibleElementsCount() + " itemsListPanelHBef="
                        + actionItemsListPanelHeightBefore
                        + " after=" + actionItemsListPanelHeightAfter + " elHeight=" + itemsListPanel.getElementHeight());
        //setSize(getWidth(), totalHeight);
        pack();
    }

    private List<AbstractItem> matchItems(String text, List<AbstractItemMatcher> activeMatchers)
    {
        text = StringUtils.stripStart(text, " \t").toLowerCase();

        ResultItemsList result = new ResultItemsList(text);
        for (AbstractItemMatcher matcher : activeMatchers)
        {
            matcher.match(getLastStackedItem(), text, result);
        }

        List<AbstractItem> matchedItemsList = result.getItems();
        sortItemsList(matchedItemsList, text);
        return matchedItemsList;
    }

    private void sortItemsList(List<AbstractItem> items, String text)
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

    private DirectoryItem getLastStackedItem()
    {
        return stackedItems.isEmpty() ? null : stackedItems.get(stackedItems.size() - 1);
    }

    public void toggleShowDialog()
    {
        if (isVisible())
        {
            hideDialog();
        }
        else
        {
            showDialog();
        }
    }

    public void hideDialog()
    {
        setVisible(false);
    }

    public void showDialog()
    {
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            System.out.println("Bring InputSelector to front");
            OsUtils.get().bringToFront(this);
        });
    }

    public void setText(String text, boolean disableMatching)
    {
        if (disableMatching)
        {
            enableDocumentListener = false;
        }
        inputField.setText(text);
        enableDocumentListener = true;
    }

    public void setMatcher(AbstractItemMatcher matcher)
    {
        activeMatchers.clear();
        activeMatchers.add(matcher);
    }

    public void setMatcher(List<AbstractItemMatcher> matchers)
    {
        activeMatchers.clear();
        activeMatchers.addAll(matchers);
    }
}
