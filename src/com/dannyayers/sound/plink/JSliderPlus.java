package com.dannyayers.sound.plink;
/**
 *	JSliderPlus.java
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border; 


public class JSliderPlus extends JPanel
{
	JLabel sliderlabel;
	JSlider slider; 
	JLabel notelabel;
	JCheckBox checkbox;
	int value = 60;

	public JSliderPlus(String label)
	{
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		super.setPreferredSize(new Dimension(30,200));
		sliderlabel = new JLabel(label);
		slider = new JSlider(JSlider.VERTICAL, 0, 127, value);
		slider.setAlignmentX(LEFT_ALIGNMENT);
		checkbox = new JCheckBox("",true);
		notelabel = new JLabel(MidiUtil.notes[value]);

		SPListener listener = new SPListener();
		slider.addChangeListener(listener);

		super.add(sliderlabel);	
		super.add(slider);
		super.add(notelabel);
		super.add(checkbox);
	}

    public class SPListener implements ChangeListener 
	{
        public void stateChanged(ChangeEvent e) 
		{
			JSlider source = (JSlider)e.getSource();
			value = source.getValue(); 
			notelabel.setText(MidiUtil.notes[value]);
        }
    }    

	public int getValue()
	{
		return value;
	}

	public boolean isSelected()
	{
		return checkbox.isSelected();
	}

	public void requestFocus()
	{
		slider.requestFocus();
	}
}