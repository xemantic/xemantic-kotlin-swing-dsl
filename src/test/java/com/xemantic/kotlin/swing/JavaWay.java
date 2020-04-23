/*
 * xemantic-kotlin-swing-dsl - swing DSL for kotlin
 *
 * Copyright (C) 2020  Kazimierz Pogoda
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.xemantic.kotlin.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JavaWay {

  public static void main(String[] args) throws InvocationTargetException, InterruptedException {
    SwingUtilities.invokeAndWait(
        new Runnable() {
          private final JLabel displayLabel = new JLabel();

          private void onNewUrl(String url) {
            displayLabel.setText("Cannot load: " + url);
          }

          @Override
          public void run() {
            JFrame frame = new JFrame("My Browser");

            displayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            displayLabel.setVerticalAlignment(SwingConstants.CENTER);
            displayLabel.setPreferredSize(new Dimension(300, 300));

            JPanel contentPanel = new JPanel(new BorderLayout());

            JPanel northContent = new JPanel(new BorderLayout(4, 0));
            northContent.add(new JLabel(("URL")), BorderLayout.WEST);
            northContent.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            JTextField urlBox = new JTextField(10);
            JButton button = new JButton("Go!");
            button.setEnabled(false);

            button.addActionListener(e -> onNewUrl(urlBox.getText()));
            northContent.add(button, BorderLayout.EAST);

            urlBox.addActionListener(e -> onNewUrl(urlBox.getText()));
            urlBox
                .getDocument()
                .addDocumentListener(
                    new DocumentListener() {
                      @Override
                      public void insertUpdate(DocumentEvent e) {
                        fireChange(e);
                      }

                      @Override
                      public void removeUpdate(DocumentEvent e) {
                        fireChange(e);
                      }

                      @Override
                      public void changedUpdate(DocumentEvent e) {
                        fireChange(e);
                      }

                      private void fireChange(DocumentEvent e) {
                        String text = urlBox.getText();
                        displayLabel.setText("Will try: " + text);
                        button.setEnabled(!text.trim().isEmpty());
                      }
                    });
            northContent.add(urlBox, BorderLayout.CENTER);



            contentPanel.add(northContent, BorderLayout.NORTH);
            contentPanel.add(displayLabel, BorderLayout.CENTER);
            frame.setContentPane(contentPanel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.pack();
            frame.setVisible(true);
          }
        });
  }
}
