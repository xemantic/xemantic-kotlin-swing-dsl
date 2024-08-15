/*
 * This file is part of xemantic-kotlin-swing-dsl - Kotlin goodies for Java Swing.
 *
 * Copyright (C) 2024  Kazimierz Pogoda
 *
 * xemantic-kotlin-swing-dsl is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * xemantic-kotlin-swing-dsl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with xemantic-kotlin-swing-dsl. If not,
 * see <https://www.gnu.org/licenses/>.
 */
package com.xemantic.kotlin.swing.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MyBrowserJava {

  public static void main(
      String[] args
  ) throws InvocationTargetException, InterruptedException {
    SwingUtilities.invokeAndWait(MyBrowserJava::mainWindow);
  }

  private static void mainWindow() {

    JFrame frame = new JFrame("My Browser");

    final JTextArea content = new JTextArea();
    content.setPreferredSize(
        new Dimension(300, 300)
    );

    JPanel contentPanel = new JPanel(
        new BorderLayout()
    );

    JPanel northContent = new JPanel(
        new BorderLayout(4, 0)
    );
    northContent.add(
        new JLabel("URL"),
        BorderLayout.WEST
    );
    northContent.setBorder(
        BorderFactory.createEmptyBorder(4, 4, 4, 4)
    );

    JTextField urlBox = new JTextField(10);
    JButton goAction = new JButton("Go!");
    goAction.setEnabled(false);

    ActionListener action = e -> {
      goAction.setEnabled(false);

      new SwingWorker<String, String>() {
        @Override
        protected String doInBackground() {
          try {
            URL url = new URI(urlBox.getText()).toURL();
            try (InputStream in = url.openStream()) {
              byte[] data = in.readAllBytes();
              return new String(data);
            }
          } catch (Exception e) {
            return e.toString();
          }
        }

        @Override
        protected void done() {
          goAction.setEnabled(true);
          try {
            String text = get();
            content.setText(text);
          } catch (Exception e) {
            e.printStackTrace();
            // should never happen
          }
        }
      }.execute();
    };

    goAction.addActionListener(action);
    urlBox.addActionListener(action);
    northContent.add(goAction, BorderLayout.EAST);

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
                goAction.setEnabled(!text.isBlank());
              }
            });

    northContent.add(urlBox, BorderLayout.CENTER);
    contentPanel.add(northContent, BorderLayout.NORTH);
    contentPanel.add(content, BorderLayout.CENTER);

    frame.setContentPane(new JScrollPane(contentPanel));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
