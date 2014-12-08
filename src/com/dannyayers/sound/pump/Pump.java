package com.dannyayers.sound.pump;
import javax.swing.*;


public class Pump extends JWindow {

    public Pump(String [] args) {
        new Gui(this);
    }

    public static void main(String[] args) {
        Pump pump = new Pump(args);
    }
} 
