package com.dannyayers.sound.bang;
/*
 *	Tone.java
 */

import	javax.sound.sampled.*;
import	javax.sound.sampled.AudioFormat.*;

public class Tone extends Thread
{
	static Encoding encoding = Encoding.PCM_SIGNED;
	static int samplerate = 44100;
	static int resolution = 16;
	static int nchannels = 1;
	static int framesize = 2;
	static boolean bigendian = false;
	static int buffer = 4096;

	static AudioFormat	format = new AudioFormat(encoding, samplerate, resolution, nchannels, framesize, samplerate, bigendian);

	boolean on;
	boolean running;
	double amplitude;
	double timeScale;
	String wavetype;
	byte[] byteData;
	SourceDataLine	sdline = null;

	public Tone(String waveform, int frequency, double amplitude)
	{
		setDaemon(true);
		on = false;
		this.wavetype = waveform;
		this.amplitude = amplitude;
		setFrequency(frequency);
	}

	public void setType(String type) 
	{
		this.wavetype = type;
	}

	public void setFrequency(int frequency)
	{
		int period = samplerate / frequency; 
		this.timeScale = (double)Waves.points/(double)period;
	}

	public void setAmplitude(double amplitude)
	{
		this.amplitude = amplitude;
	}

	public void run()
	{
		running = true;
		byteData = new byte[buffer];

		double position = 0;

		DataLine.Info	info = new DataLine.Info(SourceDataLine.class, format);
		try 
		{
			sdline = (SourceDataLine) AudioSystem.getLine(info);
			sdline.open(format, sdline.getBufferSize());
		}
		catch (LineUnavailableException e)
		{
			System.out.println(e);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		sdline.start();
		while (running)
		{
			if(on)
			{	
				this.yield();
				for (int step = 0; step < buffer; step+=framesize)
				{
					position+=timeScale;
					if(position>Waves.points) position -= Waves.points;
					int value = (int)(amplitude*Waves.wave(wavetype,(int)position));
					byteData[step] = (byte) (value & 0xFF);
					byteData[step+1] = (byte) ((value >>> 8) & 0xFF);
				}
				sdline.write(byteData, 0, byteData.length / framesize);
			} else 
				try
				{
						sleep(100);
				}
				catch(InterruptedException e) {}
		}
		sdline.stop();
		sdline.drain();
		sdline.close();
	}

	public void On(boolean on)
	{
		this.on = on;
	}
}
