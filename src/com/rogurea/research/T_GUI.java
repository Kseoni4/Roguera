package com.rogurea.research;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class T_GUI {

    public static void InitGUI(){
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();

        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            final Window window = new BasicWindow("My root window");
            Panel contentPanel = new Panel(new GridLayout(2));
            GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
            gridLayout.setHorizontalSpacing(3);

            Label title = new Label("This is a label that spans two columns");
            title.setLayoutData(GridLayout.createLayoutData(
                    GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                    GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                    true,       // Give the component extra horizontal space if available
                    false,        // Give the component extra vertical space if available
                    2,                  // Horizontal span
                    1));                  // Vertical span
            contentPanel.addComponent(title);

            contentPanel.addComponent(new Label("Text Box (aligned)"));

            contentPanel.addComponent(
                    new TextBox()
                            .setLayoutData(GridLayout.createLayoutData(
                                    GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)
                            ));
            contentPanel.addComponent(new Label("Password Box (right aligned)"));

            contentPanel.addComponent(
                    new TextBox()
                            .setMask('*')
                            .setLayoutData(GridLayout.createLayoutData(
                                    GridLayout.Alignment.END, GridLayout.Alignment.CENTER)
                            ));

            contentPanel.addComponent(new Label("Read-only Combo Box (forced size)"));
            List<String> timezonesAsStrings = new ArrayList<String>();
            for(String id: TimeZone.getAvailableIDs()) {
                timezonesAsStrings.add(id);
            }

            ComboBox<String> readOnlyComboBox = new ComboBox<String>(timezonesAsStrings);
            readOnlyComboBox.setReadOnly(true);
            readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
            contentPanel.addComponent(readOnlyComboBox);

            contentPanel.addComponent(new Label("Editable Combo Box (filled)"));
            contentPanel.addComponent(
                    new ComboBox<String>("Item #1", "Item #2", "Item #3", "Item #4")
                            .setReadOnly(false)
                            .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));
            contentPanel.addComponent(new Label("Button (centered)"));
            contentPanel.addComponent(new Button("Button", new Runnable() {
                @Override
                public void run() {
                    MessageDialog.showMessageDialog(textGUI, "MessageBox", "This is a message box", MessageDialogButton.OK);
                }
            }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));
            contentPanel.addComponent(
                    new EmptySpace()
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Separator(Direction.HORIZONTAL)
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Button("Close", new Runnable() {
                        @Override
                        public void run() {
                            window.close();
                        }
                    }).setLayoutData(
                            GridLayout.createHorizontallyEndAlignedLayoutData(2)));

            window.setComponent(contentPanel);
            textGUI.addWindowAndWait(window);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {

        }
    }

}
