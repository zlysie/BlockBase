package net.oikmo.toolbox;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PTextField extends JTextField {

    public PTextField(final String proptText) {
        super(proptText);
        addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                if(getText().isEmpty()) {
                    setText(proptText);
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if(getText().equals(proptText)) {
                    setText("");
                }
            }
        });

    }

}