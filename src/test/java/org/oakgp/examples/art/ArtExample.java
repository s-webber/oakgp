/*
 * Copyright 2019 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.examples.art;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ArtExample {
   /**
    * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
    */
   private static void createAndShowGUI() {
      // Create and set up the window.
      JFrame frame = new JFrame("OakGP Art");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

      ArtFactory artExample = new ArtFactory();
      ArtCanvas canvas1 = new ArtCanvas(artExample.generate());
      ArtCanvas canvas2 = new ArtCanvas(artExample.generate());

      frame.getContentPane().add(createPanel(artExample, canvas1, canvas2));
      frame.getContentPane().add(createPanel(artExample, canvas2, canvas1));

      // Display the window.
      frame.pack();
      frame.setVisible(true);
   }

   private static JPanel createPanel(ArtFactory artExample, ArtCanvas example, ArtCanvas canvas2) {
      JPanel pane = new JPanel();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

      pane.add(example);

      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
      JButton generateBtn = new JButton("Generate");
      generateBtn.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            example.setLogic(artExample.generate());
            example.repaint();
         }
      });
      buttonPane.add(generateBtn);
      JButton mutateBtn = new JButton("Mutate");
      mutateBtn.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            example.setLogic(artExample.mutate(example.getLogic()));
            example.repaint();
         }
      });
      buttonPane.add(mutateBtn);
      JButton crossoverBtn = new JButton("Crossover");
      crossoverBtn.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            example.setLogic(artExample.crossover(example.getLogic(), canvas2.getLogic()));
            example.repaint();
         }
      });
      buttonPane.add(crossoverBtn);

      pane.add(buttonPane);
      return pane;
   }

   public static void main(String[] args) {
      // Schedule a job for the event-dispatching thread:
      // creating and showing this application's GUI.
      javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
   }
}
