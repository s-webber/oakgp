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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.oakgp.Assignments;
import org.oakgp.node.FunctionNode;

public class ArtCanvas extends Canvas {
   private static final int WIDTH = 500;

   private FunctionNode logic;

   public ArtCanvas(FunctionNode logic) {
      this.logic = logic;
      setSize(WIDTH, WIDTH);
   }

   @Override
   public void paint(Graphics g) {
      BufferedImage img = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_INT_RGB);
      for (int x = 0; x < WIDTH; x++) {
         for (int y = 0; y < WIDTH; y++) {
            img.setRGB(x, y, getColor(x, y).getRGB());
         }
      }
      g.drawImage(img, 0, 0, null);
   }

   FunctionNode getLogic() {
      return logic;
   }

   void setLogic(FunctionNode logic) {
      this.logic = logic;
   }

   private Color getColor(int x, int y) {
      return logic.evaluate(Assignments.createAssignments(x, y), null);
   }

   void display() {
      JFrame f = new JFrame();
      f.add(this);
      f.setVisible(true);
   }
}
