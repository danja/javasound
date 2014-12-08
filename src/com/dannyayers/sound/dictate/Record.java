package com.dannyayers.sound.dictate;
/*
 *	Record.java
 */

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.*;

public class Record extends Thread
{
	TargetDataLine tdline;
	AudioInputStream audioInputStream;
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	int peak;
	static boolean recording;
	
	static Encoding encoding = Encoding.PCM_SIGNED;
	static float samplerate = 11025.0F;
	static int resolution = 16;
	static int nchannels = 1;
	static int framesize = 2;
	static float framerate = 11025.0F;
	static boolean bigendian = false;
	static AudioFormat	format = new AudioFormat(encoding, samplerate, resolution, nchannels, framesize, framerate, bigendian);
	static int buffersize = 16384;
	static int threshold = 10;
	
	public Record()
	{
		DataLine.Info	info = new DataLine.Info(TargetDataLine.class, format);
		tdline = null;
		try
		{
			tdline = (TargetDataLine) AudioSystem.getLine(info);
			tdline.open(format,buffersize); 
		}
		catch (LineUnavailableException e)
		{
			System.out.println(e);
		}
	}

	public void run()
	{
		byte[] buffer = new byte[buffersize]; 
		int	nBufferFrames = buffersize / framesize;
		recording = true;
		while (recording)
		{
			int	nFramesRead = tdline.read(buffer, 0, nBufferFrames);

			int	nBytesToWrite = nFramesRead * framesize;

			peak = 0;
			for(int i =1;i<buffersize-1;i+=framesize)
			{
				peak = buffer[i]>peak ? buffer[i] : peak; 

			}
			if(peak>threshold)	byteArrayOutputStream.write(buffer, 0, nBytesToWrite);
		}
	}

	public void startRec()
	{
		tdline.start();
		super.start();
	}

	public void stopRec()
	{
		tdline.drain();
		tdline.stop();
		tdline.close();
		recording = false;
		try
		{
			byteArrayOutputStream.close();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		byte[]	byteData = byteArrayOutputStream.toByteArray();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteData);
		audioInputStream = 
			new AudioInputStream(byteArrayInputStream, format, byteData.length/framesize);
        try 
		{
               	if (AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File("dictate.wav")) == -1) 
				{
					System.out.println("Problems writing to file");
				} 
		} 
		catch (Exception e) 
		{ 
			System.out.println(e); 
		}
	}
	
	public int getLevel()
	{
		return peak;
	}
}

