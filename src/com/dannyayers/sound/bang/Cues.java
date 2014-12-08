package com.dannyayers.sound.bang;
/*
 * Cues.java
 */

import javax.sound.sampled.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Cues implements ActionListener, ChangeListener
{
	private String[] clipFiles  = {"button.wav","click.wav","miss.wav","shot.wav","hit.wav","target.wav"};
	private String[] clipNames = {"button", "click", "miss", "shot", "hit", "target"};
	private int nClips = clipFiles.length;
	private Clip[] clip = new Clip[nClips];
	static boolean enabled = true;

	public Cues()
	{
		for(int i=0;i<nClips;i++)
		{
			File	clipFile = new File(clipFiles[i]);
			AudioInputStream	audioInputStream = null;
			try
			{
				audioInputStream = AudioSystem.getAudioInputStream(clipFile);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			AudioFormat	format = audioInputStream.getFormat();
			DataLine.Info	info = new DataLine.Info(Clip.class, format);
			try
			{
				clip[i] = (Clip) AudioSystem.getLine(info);
				clip[i].open(audioInputStream);
			}
			catch (LineUnavailableException e)
			{
				System.out.println(e);
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}

	public void cue(String name)
	{
		if(!enabled) return;
		for(int i=0;i<clipNames.length;i++)
		{

			if(name.equals(clipNames[i])) 
			{
				clip[i].setFramePosition(0);
				clip[i].start();
				return;
			}
		}	
	}

	public void addCue(JComponent component)
	{
		String compo = component.getClass().toString();

		if(compo.equals("class javax.swing.JButton")) 
		{
			((JButton)component).addActionListener(this);
		}

		if(compo.equals("class javax.swing.JMenu")) 
		{
			((JMenu)component).addChangeListener(this);
		}

		if(compo.equals("class javax.swing.JMenuItem")) 
		{
			((JMenuItem)component).addChangeListener(this);
		}

		if(compo.equals("class javax.swing.JCheckBoxMenuItem")) 
		{
			((JCheckBoxMenuItem)component).addChangeListener(this);
		}
	}

	public void actionPerformed(ActionEvent e) 
	{
		clip[0].setFramePosition(0);
		if(enabled) clip[0].start();
    }

	public void stateChanged(ChangeEvent e) 
	{
		if(!enabled) return;
		String compo = e.getSource().getClass().toString();
		if(compo.equals("class javax.swing.JMenu")) 
		{
			if( ((JMenu)e.getSource() ).isSelected() )
			{
				clip[0].setFramePosition(0);
				clip[0].start();
			}			
		} else 
			{
				clip[1].setFramePosition(0);
				clip[1].start();
			}
	}
}