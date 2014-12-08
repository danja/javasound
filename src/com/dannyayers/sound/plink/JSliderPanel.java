package com.dannyayers.sound.plink;
/**
 *  JSliderPanel.java
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border; 

public class JSliderPanel extends JPanel 
{
	JSliderPlus[] sliderplus;

	public JSliderPanel(int nsliders)
	{
		sliderplus = new JSliderPlus[nsliders];

		for(int i=0;i<nsliders;i++)
		{
			sliderplus[i] = new JSliderPlus((new Integer(i+1)).toString());
			super.add(sliderplus[i]);
		}
	}
	
	public void setSliderFocus(int current)
	{
		sliderplus[current].requestFocus();
	}

	public boolean isSelected(int current)
	{
		return sliderplus[current].isSelected();
	}

	public int getNote(int current)
	{
		return sliderplus[current].getValue();
	}
}