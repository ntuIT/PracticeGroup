/*
 * $Id: JXTaskPaneContainerAddon.java,v 1.6 2005/10/10 18:02:08 rbair Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.plaf;

import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.OS;

/**
 * Addon for <code>JXTaskPaneContainer</code>. <br>
 *  
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class JXTaskPaneContainerAddon extends AbstractComponentAddon {

  public JXTaskPaneContainerAddon() {
    super("JXTaskPaneContainer");
  }

  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addBasicDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      JXTaskPaneContainer.uiClassID,
      "org.jdesktop.swingx.plaf.basic.BasicTaskPaneContainerUI",
      "TaskPaneContainer.useGradient",
      Boolean.FALSE,
      "TaskPaneContainer.background",
      UIManager.getColor("Desktop.background")
    }));
  }

  @Override
  protected void addMetalDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMetalDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      "TaskPaneContainer.background",
      MetalLookAndFeel.getDesktopColor()
    }));
  }
  
  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPaneContainer.background",
        UIManager.getColor("List.background")
      }));      
    } else if (addon instanceof WindowsLookAndFeelAddons) {     
      String xpStyle = OS.getWindowsVisualStyle();
      ColorUIResource background;
      ColorUIResource backgroundGradientStart;
      ColorUIResource backgroundGradientEnd;
      
      if (WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {        
        background = new ColorUIResource(201, 215, 170);
        backgroundGradientStart = new ColorUIResource(204, 217, 173);
        backgroundGradientEnd = new ColorUIResource(165, 189, 132);
      } else if (WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {
        background = new ColorUIResource(192, 195, 209);
        backgroundGradientStart = new ColorUIResource(196, 200, 212);
        backgroundGradientEnd = new ColorUIResource(177, 179, 200);
      } else {        
        background = new ColorUIResource(117, 150, 227);
        backgroundGradientStart = new ColorUIResource(123, 162, 231);
        backgroundGradientEnd = new ColorUIResource(99, 117, 214);
      }      
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPaneContainer.useGradient",
        Boolean.TRUE,
        "TaskPaneContainer.background",
        background,
        "TaskPaneContainer.backgroundGradientStart",
        backgroundGradientStart,
        "TaskPaneContainer.backgroundGradientEnd",
        backgroundGradientEnd,
      }));
    }
  }

  @Override
  protected void addMacDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMacDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      "TaskPaneContainer.background",
      new ColorUIResource(238, 238, 238),
    }));            
  }

}
