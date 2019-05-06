package ru.ezhov.jclsvis.gui.utils.distance;

import javax.swing.*;
import java.awt.*;

public class MinimalDistanceIT {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }

            Component componentOne = new JLabel("one");
            componentOne.setBounds(10, 10, 50, 50);

            Component componentTwo = new JLabel("two");
            componentTwo.setBounds(150, 150, 50, 50);

            JPanel panel = new JPanel(null);
            panel.add(componentOne);
            panel.add(componentTwo);

            JFrame frame = new JFrame("_________");
            frame.add(panel);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);


            CenterPoints one = CenterPoints.from(componentOne);
            CenterPoints two = CenterPoints.from(componentTwo);
            MinimalDistance minimalDistance1 = new MinimalDistance();
            System.out.println(minimalDistance1.find(one, two));
            MinimalDistance minimalDistance2 = new MinimalDistance();
            System.out.println(minimalDistance2.find(two, one));

            double current = Math.sqrt(Math.pow(297 - 310, 2D) + Math.pow(157 - 85, 2D));
            System.out.println(current);

            current = Math.sqrt(Math.pow(422 -435, 2D) + Math.pow(132 - 110, 2D));
            System.out.println(current);
        });
    }
}