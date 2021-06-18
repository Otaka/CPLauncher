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
import org.apache.commons.lang3.StringUtils;

public class ItemsListPanel extends JPanel
{
    private final static int MAX_ITEMS_TO_SHOW = 9;
    private Queue<ItemOptionComponent> itemOptionComponentsPool;
    private List<AbstractItem> items = new ArrayList<>();
    private int selectedItemIndex = 0;
    private int topComponent = 0;
    private int visibleElementsCount = 0;
    private int elementHeight;

    public ItemsListPanel()
    {
        setMinimumSize(new Dimension(0, 0));
        setLayout(new VerticalLayoutFillWidth().setPadding(0, 5));
        setOpaque(false);

        initItemOptionsComponentsPool();

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

    private void initItemOptionsComponentsPool()
    {
        itemOptionComponentsPool = new ArrayDeque<>(9);
        for (int i = 0; i < MAX_ITEMS_TO_SHOW; i++)
        {
            itemOptionComponentsPool.add(new ItemOptionComponent());
        }
    }

    private void onMouseMoved(int x, int y)
    {
        int selectedVisibleItem = y / ItemOptionComponent.getComponentHeight();
        if (getSelected() != topComponent + selectedVisibleItem)
        {
            setSelected(topComponent + selectedVisibleItem);
            refreshItemOptionsComponents();
        }
    }

    public void setSelected(int index)
    {
        if (selectedItemIndex == index)
        {
            return;
        }

        if (items.isEmpty())
        {
            selectedItemIndex = -1;
        }
        selectedItemIndex = index;
        if (selectedItemIndex < 0)
        {
            selectedItemIndex = 0;
        }
        if (selectedItemIndex >= items.size())
        {
            selectedItemIndex = items.size() - 1;
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
        if (items.isEmpty() || selectedItemIndex == -1)
        {
            return null;
        }
        return items.get(selectedItemIndex);
    }

    public void selectNext(boolean wrapAround)
    {
        if (wrapAround && getSelected() == items.size() - 1)
        {
            setSelected(0);
        }
        else
        {
            setSelected(getSelected() + 1);
        }
        refreshItemOptionsComponents();
    }

    public void selectPrevious(boolean wrapAround)
    {
        if (wrapAround && getSelected() == 0)
        {
            setSelected(items.size() - 1);
        }
        else
        {
            setSelected(getSelected() - 1);
        }
        refreshItemOptionsComponents();
    }

    public void setItems(List<? extends AbstractItem> items)
    {
        this.items.clear();
        this.items.addAll(items);
        selectedItemIndex = (items.size() > 0) ? 0 : -1;
        topComponent = 0;
        refreshItemOptionsComponents();
        if (items.size() > 0)
        {
            elementHeight = getComponent(0).getHeight();
        }
        else
        {
            elementHeight = 0;
        }
    }

    private ItemOptionComponent getItemOptionComponentFromPool()
    {
        ItemOptionComponent itemOptionComponent = itemOptionComponentsPool.poll();
        return itemOptionComponent;
    }

    private void returnItemOptionComponentToPool(ItemOptionComponent itemOptionComponent)
    {
        itemOptionComponentsPool.add(itemOptionComponent);
    }

    private void refreshItemOptionsComponents()
    {
        removeActionItemsComponentsFromWindow();
        visibleElementsCount = Math.min(MAX_ITEMS_TO_SHOW, items.size());
        for (int i = 0; i < visibleElementsCount; i++)
        {
            AbstractItem item = items.get(i + topComponent);
            ItemOptionComponent itemOptionComponent = getItemOptionComponentFromPool();
            updateItemOptionComponent(itemOptionComponent, item, topComponent + i);
            add(itemOptionComponent);
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
            ItemOptionComponent itemOptionComponent = (ItemOptionComponent)component;
            itemOptionComponent.reset();
            returnItemOptionComponentToPool(itemOptionComponent);
        }
        removeAll();
    }

    private void updateItemOptionComponent(ItemOptionComponent itemComponent, AbstractItem item, int index)
    {
        itemComponent.setIcon(item.getIcon());
        itemComponent.setSubtext(StringUtils.isEmpty(item.getSubtext()) ? " " : item.getSubtext());
        itemComponent.setText(item.getText());
        itemComponent.setSelected(index == selectedItemIndex);
    }

    public List<AbstractItem> getItems()
    {
        return items;
    }
}
