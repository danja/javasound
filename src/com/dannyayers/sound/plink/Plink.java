package com.dannyayers.sound.plink;
/**
 *  
 *		Plink - A Simple MIDI Machine
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import javax.swing.border.*; 

public class Plink implements ActionListener, ChangeListener
{
	static int seqlength = 16;
	int duration = 600;
	int pan = 63;
	int volume = 100;
	Thread looper;
	int current; // Slider selector
	JComboBox instrnames;
	JSlider temposlider;
	JSlider panslider;
	JSlider volumeslider;

	static PlinkSynth plinksynth = new PlinkSynth();

	boolean playing;
	JSliderPanel sliderpanel; 
	static AppWindow window; 
	static Plink plink;
	Looper loop = new Looper();

    public static void main(String[] args)
    {
		window = new AppWindow("Plink", new Dimension(650,400));
		plink = new Plink();
		window.setVisible(true);
	}

	public Plink()
	{
	    Container content = window.getContentPane();

		GridBagLayout gridbag = new GridBagLayout();
		content.setLayout(gridbag);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE; 
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 0;

		JPanel globalpanel = new JPanel();
		globalpanel.setBorder(BorderFactory.createLoweredBevelBorder());

		temposlider = new JSlider(JSlider.HORIZONTAL, 30, 2000, duration);
		temposlider.setInverted(true); 

		Dictionary labeltable = new Hashtable();
		labeltable.put(new Integer(30), new JLabel("Fast"));
		labeltable.put(new Integer(2000), new JLabel("Slow"));
		temposlider.setLabelTable(labeltable);
		temposlider.setPaintLabels(true);
		temposlider.addChangeListener (this);

		TitledBorder titleborder = new TitledBorder(new EtchedBorder());
		titleborder.setTitle("Tempo");
		temposlider.setBorder(titleborder);

		panslider = new JSlider(JSlider.HORIZONTAL, 0, 127, pan);
		labeltable = new Hashtable();
		labeltable.put(new Integer(0 ), new JLabel("<"));
		labeltable.put(new Integer(127), new JLabel(">"));
		panslider.setLabelTable(labeltable);
		panslider.setPaintLabels(true);
		panslider.addChangeListener(this);

		titleborder = new TitledBorder(new EtchedBorder());
		titleborder.setTitle("Pan");
		panslider.setBorder(titleborder);

		volumeslider = new JSlider(JSlider.HORIZONTAL, 0, 127, volume);
		labeltable = new Hashtable();
		labeltable.put(new Integer(0 ), new JLabel("Soft"));
		labeltable.put(new Integer(127), new JLabel("Loud"));
		volumeslider.setLabelTable(labeltable);
		volumeslider.setPaintLabels(true);
		volumeslider.addChangeListener (this);

		titleborder = new TitledBorder(new EtchedBorder());
		titleborder.setTitle("Volume");
		volumeslider.setBorder(titleborder);

		globalpanel.add(temposlider);
		globalpanel.add(panslider);
		globalpanel.add(volumeslider);
	
		gridbag.setConstraints(globalpanel, gbc);
		content.add(globalpanel);

		sliderpanel = new JSliderPanel(seqlength);
		sliderpanel.setBorder(BorderFactory.createEtchedBorder());
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(sliderpanel, gbc);
		content.add(sliderpanel);

		JPanel buttonpanel = new JPanel();

		JButton playButton = new JButton("Play");
		playButton.addActionListener(this);

		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(this);

		instrnames = new JComboBox(plinksynth.getNames());
		instrnames.setLightWeightPopupEnabled(false);
		instrnames.addActionListener(this);

		buttonpanel.add(playButton);
		buttonpanel.add(stopButton);
		buttonpanel.add(instrnames);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gridbag.setConstraints(buttonpanel, gbc);
		content.add(buttonpanel);
    }


	public class Looper implements Runnable
	{
		public void run() 
		{
			int note;
			current = -1;
			while(playing)
			{
				if(seqlength == ++current) current = 0; // ++current
				EventQueue.invokeLater(new Runnable()
				{
					public void run()
					{
						sliderpanel.setSliderFocus(current);
					}
				});
				note = sliderpanel.getNote(current);
				boolean ticked = false;
				if(sliderpanel.isSelected(current)) ticked = true;
				if(ticked) plinksynth.noteOn(note,80);
				try
				{
					Thread.sleep(duration);
				} 
				catch (InterruptedException e)
					{
						System.out.println(e);
					}
				if(ticked) plinksynth.noteOff(note);
			}
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if(command.equals("Play"))
		{
				if(!playing)	
				{
					looper = new Thread(loop);
					looper.setDaemon(true);
					looper.start();
					playing = true;
				}
		}

		if(command.equals("Stop"))
		{
			playing = false;
		}

		if(command.equals("comboBoxChanged"));
			plinksynth.programChange(instrnames.getSelectedIndex());
	}	

       	public void stateChanged(ChangeEvent e) 
		{
				duration = temposlider.getValue();
				plinksynth.setPan(panslider.getValue());
				plinksynth.setVolume(volumeslider.getValue());
       	}
}
