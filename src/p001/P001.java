/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p001;

import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
import java.text.ParseException;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * 
 */
public class P001 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        try {
            LookAndFeel dlf = new SyntheticaAluOxideLookAndFeel();
            UIManager.setLookAndFeel(dlf);
            new login().setVisible(true);
        } catch (UnsupportedLookAndFeelException e) {
        }
    }

}
