package gui;

import data.Stats;

import javax.swing.*;

public final class GUI
{
    private static final GUI INSTANCE = new GUI();

    public static GUI getInstance()
    {
        return INSTANCE;
    }

    public void showColorPatternsSinceYearGui(String yearFilter, Stats stats)
    {
        SwingUtilities.invokeLater(() -> new ColorPatternsView(yearFilter, stats));
    }

    public void showTopColorPatternsGui(Stats stats, int targetSize)
    {
        SwingUtilities.invokeLater(() -> new ColorPatternsView(stats, targetSize));
    }

    public void showColorPatternTemplateGui()
    {
        // Run the GUI construction in the Event-Dispatching thread for thread-safety
        SwingUtilities.invokeLater(ColorPatternTemplateView::new);
    }

}
