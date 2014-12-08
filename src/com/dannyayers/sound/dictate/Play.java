package com.dannyayers.sound.dictate;
/*
 * 	Play.java
 */

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.*;

public class Play extends Thread  
{
	SourceDataLine sdline;
	static boolean playing;
	int position;
//	int	frames;
	AudioInputStream audioInputStream;
	int playlevel;
	boolean end;
	static Encoding encoding = Encoding.PCM_SIGNED;
	static float samplerate = 11025.0F;
	static int resolution = 16;
	static int nchannels = 1;
	static int framesize = 2;
	static float framerate = 11025.0F;
	static boolean bigendian = false;
	static AudioFormat	format = new AudioFormat(encoding, samplerate, resolution, nchannels, framesize, framerate, bigendian);
	static int buffersize = 16384; 
	
	public Play()
	{
		end = false;
		DataLine.Info	info = new DataLine.Info(SourceDataLine.class, format);
		sdline = null;
		try
		{
			sdline = (SourceDataLine) AudioSystem.getLine(info);
		}
		catch (LineUnavailableException e)
		{
			System.out.println(e);
		}
	}

	public void startPlay()
	{
		playing = true;
		File	soundFile = new File("dictate.wav");
		audioInputStream = null;
		try
		{
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			sdline.open(format, buffersize);
		}
		catch (LineUnavailableException e)
		{
			System.out.println(e);
		}
		sdline.start();
		super.start();
	}

	public void run()
	{
		end = false;
		playing = true;
		int bytes = 0;
		int length = (int)audioInputStream.getFrameLength()*framesize;
		byte[]	byteData = new byte[buffersize]; 
		position = 0;
		while (bytes != -1)
		{
			try
			{
				bytes = audioInputStream.read(byteData, 0, buffersize);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		if (bytes >= 0) 
			{
				position	+= sdline.write(byteData, 0, bytes);
				playlevel = (int)(128*(float)position/(float)length);
			}
		}

		stopPlay();
		end = true;
	}


	public void stopPlay()
	{
		sdline.drain();
		sdline.stop();
		sdline.close();
		playing = false;
	}
	
	public boolean isEnd()
	{
		return end;
	}
	
	public int getLevel()
	{
		return playlevel;
	}
}

