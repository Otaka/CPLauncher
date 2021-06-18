package com.cplauncher.ui.inputselector;

import com.cplauncher.items.AbstractItem;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.swing.JPanel;

public class ItemsListPanel extends JPanel
{
    private final static int MAX_ITEMS_TO_SHOW = 9;
    private Queue<ActionItemComponent> actionItemComponentsPool;
    private List<AbstractItem> actionItems = new ArrayList<>();
    private int selectedItemIndex = 0;
    private int topComponent = 0;
    private int visibleElementsCount = 0;
    private int elementHeight;

    public ItemsListPanel()
    {
        setMinimumSize(new Dimension(0, 0));
        setLayout(new VerticalLayoutFillWidth().setPadding(0, 5));
        setOpaque(false);

        initActionItemsComponentsPool();

        elementHeight = 0;
        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                onMouseMoved(e.getX(), e.getY());
            }
        });
        addMouseWheelListener(new MouseWheelListener()
        {
            long lastAccessTime = 0;
            boolean lastWasNegative = false;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                int speed = e.getUnitsToScroll();
                if (Math.abs(speed) > 1)
                {
                    speed = speed / 3;
                }
                //on mac os when I scroll in one direction, I periodically receive on event in opposite direction, that is why I am filtering it out
                long currentTime = System.currentTimeMillis();
                boolean currentIsNegative = speed < 0;
                if (currentTime - lastAccessTime < 20)
                {
                    if (lastWasNegative != currentIsNegative)
                    {
                        lastWasNegative = currentIsNegative;
                        return;
                    }
                }
                lastWasNegative = currentIsNegative;
                lastAccessTime = currentTime;

                for (int i = 0; i < Math.abs(speed); i++)
                {
                    if (e.getUnitsToScroll() >= 0)
                    {
                        ItemsListPanel.this.selectNext(false);
                    }
                    else
                    {
                        ItemsListPanel.this.selectPrevious(false);
                    }
                }
            }
        });
    }

    public int getElementHeight()
    {
        return elementHeight;
    }

    private void initActionItemsComponentsPool()
    {
        actionItemComponentsPool = new ArrayDeque<>(9);
        for (int i = 0; i < MAX_ITEMS_TO_SHOW; i++)
        {
            actionItemComponentsPool.add(new ActionItemComponent());
        }
    }

    private void onMouseMoved(int x, int y)
    {
        int selectedVisibleItem = y / ActionItemComponent.getComponentHeight();
        if (getSelected() != topComponent + selectedVisibleItem)
        {
            setSelected(topComponent + selectedVisibleItem);
            refreshActionComponents();
        }
    }

    public void setSelected(int index)
    {
        if (selectedItemIndex == index)
        {
            return;
        }

        if (actionItems.isEmpty())
        {
            selectedItemIndex = -1;
        }
        selectedItemIndex = index;
        if (selectedItemIndex < 0)
        {
            selectedItemIndex = 0;
        }
        if (selectedItemIndex >= actionItems.size())
        {
            selectedItemIndex = actionItems.size() - 1;
        }
        if (selectedItemIndex < topComponent)
        {
            topComponent = selectedItemIndex;
        }
        if (selectedItemIndex >= (topComponent + visibleElementsCount))
        {
            topComponent = selectedItemIndex - visibleElementsCount + 1;
        }
    }

    public int getSelected()
    {
        return selectedItemIndex;
    }

    public AbstractItem getSelectedItem()
    {
        if (actionItems.isEmpty() || selectedItemIndex == -1)
        {
            return null;
        }
        return actionItems.get(selectedItemIndex);
    }

    public void selectNext(boolean wrapAround)
    {
        if (wrapAround && getSelected() == actionItems.size() - 1)
        {
            setSelected(0);
        }
        else
        {
            setSelected(getSelected() + 1);
        }
        refreshActionComponents();
    }

    public void selectPrevious(boolean wrapAround)
    {
        if (wrapAround && getSelected() == 0)
        {
            setSelected(actionItems.size() - 1);
        }
        else
        {
            setSelected(getSelected() - 1);
        }
        refreshActionComponents();
    }

    public void setActionItems(List<? extends AbstractItem> items)
    {
        actionItems.clear();
        actionItems.addAll(items);
        selectedItemIndex = (items.size() > 0) ? 0 : -1;
        topComponent = 0;
        refreshActionComponents();
        if (items.size() > 0)
        {
            elementHeight = getComponent(0).getHeight();
        }
        else
        {
            elementHeight = 0;
        }
    }

    private ActionItemComponent getActionItemComponentFromPool()
    {
        ActionItemComponent actionItemComponent = actionItemComponentsPool.poll();
        return actionItemComponent;
    }

    private void returnActionItemComponentToPool(ActionItemComponent actionItemComponent)
    {
        actionItemComponentsPool.add(actionItemComponent);
    }

    private void refreshActionComponents()
    {
        removeActionItemsComponentsFromWindow();
        visibleElementsCount = Math.min(MAX_ITEMS_TO_SHOW, actionItems.size());
        for (int i = 0; i < visibleElementsCount; i++)
        {
            AbstractItem item = actionItems.get(i + topComponent);
            ActionItemComponent actionItemComponent = getActionItemComponentFromPool();
            beforeUpdateItemToPanel(actionItemComponent, item, topComponent + i);
            add(actionItemComponent);
        }

        validate();
        repaint();

    }

    public int getVisibleElementsCount()
    {
        return visibleElementsCount;
    }

    private void removeActionItemsComponentsFromWindow()
    {
        //reset components and return them to pool
        for (Component component : getComponents())
        {
            ActionItemComponent actionItemComponent = (ActionItemComponent)component;
            actionItemComponent.reset();
            returnActionItemComponentToPool(actionItemComponent);
        }
        removeAll();
    }

    private void beforeUpdateItemToPanel(ActionItemComponent itemComponent, AbstractItem item, int index)
    {
        itemComponent.setIcon(item.getIcon());
        itemComponent.setSubtext(item.getSubtext());
        itemComponent.setText(item.getText());
        itemComponent.setSelected(index == selectedItemIndex);
    }

    public List<AbstractItem> getActionItems()
    {
        return actionItems;
    }
}
