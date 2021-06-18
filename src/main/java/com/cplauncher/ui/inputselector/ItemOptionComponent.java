package com.cplauncher.ui.inputselector;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ItemOptionComponent extends JPanel
{
    private static Font textFont;
    private static Font subtextFont;

    private Icon icon;
    private JLabel textLabel;
    private JLabel subtextLabel;
    private static int cachedHeight = 0;
    private static BufferedImage tempCanvas = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);

    public ItemOptionComponent()
    {
        setLayout(new VerticalLayoutFillWidth().setPadding(50, 0));
        textLabel = new JLabel();
        textLabel.setFont(getTextFont());
        textLabel.setForeground(Style.INSTANCE.actionItemTextColor);
        add(textLabel);
        subtextLabel = new JLabel();
        subtextLabel.setFont(getSubtextFont());
        subtextLabel.setForeground(Style.INSTANCE.actionItemTextColor);
        add(subtextLabel);
        setSelected(false);
        setOpaque(true);
    }

    public static int getComponentHeight()
    {
        return cachedHeight;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if (cachedHeight <= 0)
        {
            cachedHeight = getHeight();
        }
    }

    public void setSelected(boolean selected)
    {
        if (selected)
        {
            setBackground(Style.INSTANCE.selectedActionItemBackground);
        }
        else
        {
            setBackground(Style.INSTANCE.notSelectedActionItemBackground);
        }
    }

    private Font getTextFont()
    {
        return textFont == null ? textFont = getFont().deriveFont(20.0f) : textFont;
    }

    private Font getSubtextFont()
    {
        return subtextFont == null ? subtextFont = getFont().deriveFont(10.0f) : subtextFont;
    }

    public void reset()
    {
        icon = null;
        textLabel.setText("");
        subtextLabel.setText("");
        setSelected(false);
    }

    public void setIcon(Icon icon)
    {
        this.icon = icon;
    }

    public void setText(String title)
    {
        textLabel.setText(title);
    }

    public void setSubtext(String subtext)
    {
        subtextLabel.setText(subtext);
    }

    private void drawIcon(Icon icon, Graphics g, int x, int y, int w, int h)
    {
        Graphics gr = tempCanvas.getGraphics();
        gr.clearRect(0, 0, tempCanvas.getWidth(), tempCanvas.getHeight());
        icon.paintIcon(this, gr, 0, 0);
        gr.dispose();
        g.drawImage(tempCanvas, x, y, w, h, 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (icon != null)
        {
            drawIcon(icon, g, 1, 1, 40, 40);
        }
    }
}
