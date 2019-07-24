package gui;

import utils.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Gui class for showing the lotto color pattern table.
 */
class ColorPatternTemplateView extends JFrame
{
    private Dimension jLblDims = new Dimension(30, 30);

    public ColorPatternTemplateView()
    {
        this.setTitle("Pattern Table");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(jLblDims.width * 11, jLblDims.height * 7));

        // add the components
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        JPanel row0 = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        for (int i = 0; i <= 49; i++)
        {
            switch (i / 10)
            {
                case 0:
                    row0.add(getJLabel(i));
                    break;
                case 1:
                    row1.add(getJLabel(i));
                    break;
                case 2:
                    row2.add(getJLabel(i));
                    break;
                case 3:
                    row3.add(getJLabel(i));
                    break;
                case 4:
                    row4.add(getJLabel(i));
                    break;
            }
        }
        this.getContentPane().add(row0);
        this.getContentPane().add(row1);
        this.getContentPane().add(row2);
        this.getContentPane().add(row3);
        this.getContentPane().add(row4);

        this.pack();
        this.setVisible(true);
    }

    private JLabel getJLabel(int num)
    {
        JLabel label;
        if (num == 0)
        {
            label = new JLabel();
            label.setBackground(Color.LIGHT_GRAY);
        } else
        {
            label = new JLabel(num + "", SwingConstants.CENTER);
            label.setBackground(Utils.getColor(num / 10));
        }

        label.setMinimumSize(jLblDims);
        label.setPreferredSize(jLblDims);
        label.setMaximumSize(jLblDims);
        label.setOpaque(true);

        return label;
    }

}
