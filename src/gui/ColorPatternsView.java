package gui;

import data.Stats;
import models.StringPattern;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Gui class for showing a list of color patterns.
 */
class ColorPatternsView extends JFrame
{
    private Dimension squareDims = new Dimension(30, 30);

    public ColorPatternsView(String yearFilter, Stats stats)
    {
        List<int[]> list = Utils.getColorPatternsSinceYear(yearFilter);
        this.setTitle(list.size() + " Color Patterns since " + yearFilter);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(squareDims.width * 10, squareDims.height * 30));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for (int[] arr : list)
        {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 2));
            for (int num : arr)
            {
                JLabel label = new JLabel();
                label.setMinimumSize(squareDims);
                label.setPreferredSize(squareDims);
                label.setMaximumSize(squareDims);
                label.setOpaque(true);
                label.setBackground(Utils.getColor(num / 10));
                row.add(label);
            }
            row.add(
                    new JLabel(
                            stats.
                                    getColorPatterns().
                                    get(Utils.getColorPatternAsString(arr)).
                                    getProbability() + ""));
            mainPanel.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane);
        this.pack();
        this.setVisible(true);
    }

    public ColorPatternsView(Stats stats, int targetSize)
    {
        this.setTitle("Top " + targetSize + " Color Patterns since " + stats.getYearFilter());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(squareDims.width * 10, squareDims.height * 30));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for (StringPattern pattern : stats.getTopColorPatterns(targetSize))
        {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 2));
            for (int num : Utils.convertStringToIntegerArray(pattern.getPattern()))
            {
                JLabel label = new JLabel();
                label.setMinimumSize(squareDims);
                label.setPreferredSize(squareDims);
                label.setMaximumSize(squareDims);
                label.setOpaque(true);
                label.setBackground(Utils.getColor(num));
                row.add(label);
            }
            row.add(new JLabel(pattern.getProbability() + ""));
            mainPanel.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane);
        this.pack();
        this.setVisible(true);
    }

}
