package com.dannyayers.sound.jmfdictate;
/*
 *	Record.java
 */

import java.io.*;
import java.util.*;
import javax.media.*; 
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;


public class Record extends Thread 
{ 
	Processor proc = null;
	StateMonitor statemon;
	DataSink filewriter = null;
	static boolean recording;
	
	static String encoding = AudioFormat.LINEAR; 
	static double samplerate = 44100.0D;
	static int resolution = 16;
	static int nchannels = 2;
	static int endian = AudioFormat.LITTLE_ENDIAN;
	static int signed = AudioFormat.SIGNED;
	static AudioFormat format = new AudioFormat(encoding, samplerate, resolution, nchannels, endian, signed);
	int level = 0;
	
	public void run()
	{
		recording = true;
	 	CaptureDeviceInfo info = null;
		Vector deviceList = CaptureDeviceManager.getDeviceList(format);//null

		if (deviceList.size()==0)
			System.out.println("No devices found.");
		for(int i=0;i<deviceList.size();i++)
		{
			Format[] f = ((CaptureDeviceInfo)deviceList.elementAt(i)).getFormats();
			for(int j=0;j<f.length;j++)
			{
				if(f[j].equals(format)) 
					info = (CaptureDeviceInfo)deviceList.elementAt(i);
			}
		}
		if (info==null)
				System.out.println("No suitable device found.");
		try 
		{
			proc = Manager.createProcessor(info.getLocator());
		}catch(Exception e) 
			{
             System.out.println(e);
         } 
         
		statemon = new StateMonitor(proc);
		statemon.configure();

		proc.setContentDescriptor(new
                 FileTypeDescriptor(FileTypeDescriptor.MPEG_AUDIO));
		TrackControl track[] = proc.getTrackControls(); 
		track[0].setFormat(new AudioFormat(AudioFormat.MPEGLAYER3)); 

		statemon.realize();
		DataSource source = proc.getDataOutput();

 		MediaLocator locator = null;
		File soundFile = new File("dictate.mp3");
		try
		{
			locator = new MediaLocator(soundFile.toURL());
		}catch(Exception e)
			{
				System.out.println(e);
			}

		try 
		{
			filewriter = Manager.createDataSink(source, locator);
			filewriter.open();
		}catch(Exception e) 
			{
				System.out.println(e);
		} 
		try 
		{
				filewriter.start();
		}catch(Exception e) 
			{
				System.out.println(e);
			}
					 
		statemon.start(true);/////////////
		long starttime = System.currentTimeMillis();
		while(recording)
		{
			try
			{
				level = (int)(128*(System.currentTimeMillis()-starttime)/10000);
				if(level>127) starttime = System.currentTimeMillis();
				sleep(100);
			}catch(Exception e){}
		}

	}

	public void startRec()
	{
		this.start();
	}

	public void stopRec()
	{
		recording = false;
		statemon.close();
		filewriter.close();
	}
	
	public int getLevel()
	{
		return level;
	}
}
