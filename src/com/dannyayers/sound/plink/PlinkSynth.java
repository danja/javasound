package com.dannyayers.sound.plink;
import	javax.sound.midi.*;

public class PlinkSynth
{
	Synthesizer	synth;
    Instrument instruments[];
	MidiChannel	channel;

	public PlinkSynth()
	{
		try
		{
			synth = MidiSystem.getSynthesizer();
			synth.open();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

		instruments = synth.getDefaultSoundbank().getInstruments();
		channel = synth.getChannels()[0];
	}

	public void noteOn(int note, int velocity)
	{
		channel.noteOn(note, velocity); //MIDI channel 1
	}

	public void noteOff(int note)
	{
		channel.noteOff(note);
	}	

	public void programChange(int program) 
	{
		channel.programChange(program);
	}

	public void setPan(int pan) 
	{
		channel.controlChange(MidiUtil.pan, pan);
	}

	public void setVolume(int volume) 
	{
		channel.controlChange(MidiUtil.volume, volume);
	}

	public String[] getNames()
	{
		String[] names = new String[128];
		for(int i=0;i<128;i++)
		{
			names[i] = instruments[i].getName();
		}
		return names;
	}
}
