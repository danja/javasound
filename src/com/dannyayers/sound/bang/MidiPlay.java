package com.dannyayers.sound.bang;
/*
 * MidiPlay.java
 */

import	java.io.*;
import	javax.sound.midi.*;

public class MidiPlay extends Thread implements MetaEventListener
{
	Sequencer	sequencer;
	int ntracks;
	boolean playing;	
	
	public MidiPlay(String filename)
	{
		this.setDaemon(true);
		this.setPriority(MIN_PRIORITY);
		File	midiFile = new File(filename);
		Sequence	sequence = null;
		try
		{
			sequence = MidiSystem.getSequence(midiFile);
			ntracks = sequence.getTracks().length;
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

		try
		{
			this.sequencer = MidiSystem.getSequencer();
			this.sequencer.addMetaEventListener(this);
			sequencer.open();
			sequencer.setSequence(sequence);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		Mute(true);
	}

	public void Mute(boolean mute)
	{
		for(int i =0;i<ntracks;i++)
		{
			sequencer.setTrackMute(i,mute);
		}
	}

	public void run()
	{
		playing = true;
		sequencer.start();

		while(playing)
		{
			try
			{
				sleep(100);
			}
			catch(InterruptedException e)
				{
					System.out.println(e);
				}	
		}
		sequencer.stop();
		sequencer.close();
	}

	public void meta(MetaMessage event)
	{
		if (event.getType() == 47)
		{
			sequencer.stop();
			sequencer.start();
		}
	}

	public void Stop()
	{
		playing = false;
	}
}
