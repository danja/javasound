package com.dannyayers.sound.fractal;
import	javax.sound.midi.*;

public class PlinkSynth
{
	Synthesizer	synth;

    Instrument instruments[];
	MidiChannel[]	channels;

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
		synth.loadInstrument(instruments[0]);
		channels = synth.getChannels();
	}

	public void noteOn(int note, int velocity)
	{
		channels[0].noteOn(note, velocity); //MIDI channel 1
	}

	public void noteOff(int note)
	{
		channels[0].noteOff(note);
	}	

	public void programChange(int program) 
	{
		synth.loadInstrument(instruments[program]);
		channels[0].programChange(program);
	}

	public void setPan(int pan) 
	{
		channels[0].controlChange(MidiUtil.pan, pan);
	}

	public void setVolume(int volume) 
	{
		channels[0].controlChange(MidiUtil.volume, volume);
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
