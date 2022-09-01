package com.moreno;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

import static java.awt.Frame.MAXIMIZED_BOTH;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        FlatDarkLaf.setup();
        Principal principal=new Principal();
        principal.setVisible(true);
    }
}
