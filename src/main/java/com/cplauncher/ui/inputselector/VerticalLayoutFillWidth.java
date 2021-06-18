package com.cplauncher.ui.inputselector;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.function.Function;

public class VerticalLayoutFillWidth implements LayoutManager
{
    private int leftPadding;
    private int rightPadding;

    public VerticalLayoutFillWidth setPadding(int left, int right)
    {
        leftPadding = left;
        rightPadding = right;
        return this;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent)
    {
        return calculateDimension(parent, Component::getPreferredSize);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent)
    {
        return calculateDimension(parent, Component::getMinimumSize);
    }

    private Dimension calculateDimension(Container parent, Function<Component, Dimension> dimensionExtractor)
    {
        int width = 0, height = 0;
        for (Component component : parent.getComponents())
        {
            Dimension dimension = dimensionExtractor.apply(component);
            int tw = (int)dimension.getWidth();
            int th = (int)dimension.getHeight();
            width = Math.max(tw, width + leftPadding + rightPadding);
            height = th + height;
        }
        return new Dimension(width, height);
    }

    @Override
    public void layoutContainer(Container parent)
    {
        int containerWidth = parent.getWidth();
        int currentHeight = 0;
        for (Component component : parent.getComponents())
        {

            int preferredHeight = (int)component.getPreferredSize().getHeight();
            component.setBounds(leftPadding, currentHeight, containerWidth, preferredHeight);
            currentHeight += preferredHeight;
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp)
    {
    }

    @Override
    public void removeLayoutComponent(Component comp)
    {
    }
}
