package com.acelych.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class SliderUI extends BasicSliderUI
{
    private static final Color
            GrayType00 = new Color(170, 170, 170),
            GrayType01 = new Color(220, 220, 220),
            GrayType03 = new Color(250, 250, 250);
    private boolean isPointUp;

    public SliderUI(JSlider slider, boolean isPointUp)
    {
        super(slider);
        this.isPointUp = isPointUp;
    }

    @Override
    public void paintTrack(Graphics g)
    {
        int height = 4;
        Rectangle trackBounds = trackRect;
        g.setColor(GrayType03);
        g.fillRect(trackBounds.x, (trackBounds.height - height) / 2, trackBounds.width, height);
        g.setColor(GrayType01);
        g.drawLine(trackBounds.x, (trackBounds.height - height) / 2, trackBounds.x + trackBounds.width, (trackBounds.height - height) / 2);
        g.drawLine(trackBounds.x, (trackBounds.height + height) / 2, trackBounds.x + trackBounds.width, (trackBounds.height + height) / 2);
        g.drawLine(trackBounds.x, (trackBounds.height - height) / 2, trackBounds.x, (trackBounds.height + height) / 2);
        g.drawLine(trackBounds.x + trackBounds.width, (trackBounds.height - height) / 2, trackBounds.x + trackBounds.width, (trackBounds.height + height) / 2);
    }

    @Override
    public void paintThumb(Graphics g)
    {
        Rectangle knobBounds = thumbRect;

        g.setColor(GrayType00);

        if (isPointUp)
        {
            g.fillRect(knobBounds.x, knobBounds.y, knobBounds.width, knobBounds.height / 3 * 2);
            int[] px = {knobBounds.x, knobBounds.x + (knobBounds.width / 2), knobBounds.x + knobBounds.width, knobBounds.x};
            int[] py = {knobBounds.y + (knobBounds.height / 3 * 2), knobBounds.y + knobBounds.height, knobBounds.y + (knobBounds.height / 3 * 2), knobBounds.y + (knobBounds.height / 3 * 2)};
            g.fillPolygon(px, py, 4);
        }
        else
        {
            g.fillRect(knobBounds.x, knobBounds.y + knobBounds.height / 3, knobBounds.width, knobBounds.height / 3 * 2);
            int[] px = {knobBounds.x, knobBounds.x + (knobBounds.width / 2), knobBounds.x + knobBounds.width, knobBounds.x};
            int[] py = {knobBounds.y + knobBounds.height / 3, knobBounds.y, knobBounds.y + knobBounds.height / 3, knobBounds.y + knobBounds.height / 3};
            g.fillPolygon(px, py, 4);
        }
    }

    @Override
    public void paintFocus(Graphics g)
    {

    }
}
