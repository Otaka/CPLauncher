package com.cplauncher.ui.inputselector;

import com.cplauncher.items.AbstractItem;
import com.cplauncher.items.DirectoryItem;
import com.cplauncher.items.ResultItemsList;
import com.cplauncher.items.matchers.AbstractItemMatcher;
import com.cplauncher.platform.OsUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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

    public InputSelector() throws HeadlessException
    {
        instance = this;
        setUndecorated(true);
        setTitle("CPLauncher");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
       // getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().setLayout(new VerticalLayoutFillWidth().setPadding(5,5));
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
        inputField.setPreferredSize(new Dimension(400, 40));
        inputField.setFont(inputField.getFont().deriveFont(20.f));
        inputField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    System.out.println("Accept. Not implemented yet");
                }
                if (e.getKeyCode() == KeyEvent.VK_TAB)
                {
                    System.out.println("Expand. Not implemented yet");
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

    private void onTextTyped(String text)
    {
        if (enableDocumentListener)
        {
            List<DirectoryItem> result = matchItems(text, getActiveMatchers());
            setResultItemsToList(result);
        }
    }

    private List<AbstractItemMatcher> getActiveMatchers()
    {
        return activeMatchers;
    }

    private void setResultItemsToList(List<DirectoryItem> result)
    {
        setActionItems(result);
    }

    public void setActionItems(List<? extends AbstractItem> items)
    {
        int actionItemsListPanelHeightBefore = itemsListPanel.getVisibleElementsCount() * itemsListPanel.getElementHeight();
        itemsListPanel.setActionItems(items);
        int actionItemsListPanelHeightAfter = itemsListPanel.getVisibleElementsCount() * itemsListPanel.getElementHeight();
        int totalHeightClientHeight = inputField.getHeight() + actionItemsListPanelHeightAfter;
        int totalHeight=totalHeightClientHeight+getInsets().top+getInsets().bottom;
        System.out.println(
                "totalH=" + totalHeight + " inputFieldH=" + inputField.getHeight() + " itemsCount=" + itemsListPanel.getVisibleElementsCount() + " itemsListPanelHBef="
                        + actionItemsListPanelHeightBefore
                        + " after=" + actionItemsListPanelHeightAfter + " elHeight=" + itemsListPanel.getElementHeight());
        setSize(getWidth(), totalHeight);

    }

    private List<DirectoryItem> matchItems(String text, List<AbstractItemMatcher> activeMatchers)
    {
        text = StringUtils.stripStart(text, " \t").toLowerCase();

        ResultItemsList result = new ResultItemsList(text);
        for (AbstractItemMatcher matcher : activeMatchers)
        {
            matcher.match(getLastStackedItem(), text, result);
        }

        List<DirectoryItem> matchedItemsList = result.getItems();
        sortItemsList(matchedItemsList, text);
        return matchedItemsList;
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
