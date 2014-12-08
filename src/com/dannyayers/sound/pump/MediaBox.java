package com.dannyayers.sound.pump;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.control.*;

import java.io.*;
import java.util.*;

public class MediaBox
{
	Processor proc = null;
	DataSink filewriter = null;
	private boolean recording;
	File mediafile;
				
	String encoding = AudioFormat.LINEAR; 
	double samplerate = 44100.0D;
	int resolution = 16;
	int nchannels = 2;
	int endian = AudioFormat.LITTLE_ENDIAN;
	int signed = AudioFormat.SIGNED;
	AudioFormat aformat = new AudioFormat(encoding, samplerate, resolution, nchannels, endian, signed);
 	VideoFormat vformat = new VideoFormat(VideoFormat.MPEG);
	boolean recvideo;
    
	private GainControl volume = null;
	// Player proc = null;
	StateMonitor statemon;
	CaptureDeviceInfo info;
	double duration;
	String filename;
	String fileext = new String();
	MediaLocator locator = null;
	private static boolean playing;
	private static boolean paused;
	private static boolean loop = true;
	PlayThread playthread;
	RecThread recthread;
	private String errmsg = new String();
   PauseThread pausethread;
	ShuttleThread shuttlethread;
	private double shuttlestep;
		    
	private boolean shuttlemode;
	private boolean shuttlestate;

	
	public MediaBox() 
	{
	}

   
   public void setLoop(boolean lp)
   {
   		this.loop = lp;
	}
	   
   public boolean isLoop()
   {
   	return loop;
	}
	   
   public void setShuttleMode(boolean sm)
   {
   		this.shuttlemode = sm;
	}
	
	public boolean getShuttleMode()
	{
		return shuttlemode;
	}
	
 	public synchronized void setShuttleState(boolean ss)
	{
		this.shuttlestate = ss;
		System.out.println("ss = "+ss);
		if(ss)
		{
				if (statemon != null) // proc ***
				{
				System.out.println(shuttlethread);
				shuttlethread = null;

						shuttlethread = new ShuttleThread();
						System.out.println("new shuttlethread");

					if(!shuttlethread.isAlive())
					{						
						shuttlethread.start();
		System.out.println("ss start ");						
					}	
				}
		} else shuttlethread = null;
	}   
  
	
	class ShuttleThread extends Thread
	{
		double now;
		public void run() 
		{
			shuttlestep = paused ? 0.1:1;
			while(shuttlestate)
			{
			System.out.println("shuttling!");
				if((proc.getState()==Player.Started)||shuttlestate)
				{
					now = proc.getMediaTime().getSeconds();
					if(shuttlemode)
					{
						now += shuttlestep;
						now = now>duration ? 0:now;
					} else
						{ 
							now -= shuttlestep; 
							now = now<0 ? 0:now;
						}
						
					proc.stop(); //statemon
					proc.setMediaTime(new Time(now));
					if(!paused) 	proc.start(); //proc
				}
					try
					{
						Thread.sleep(10);
					}catch (Exception e){}
			}
			System.out.println("end of loop");
		}
	}
	
	public String getErrMsg()
	{
		return errmsg;
	}
	
	public boolean setLocator(String mf)
	{
		try
		{
			mediafile = new File(mf);
			locator = new MediaLocator(mediafile.toURL());
		}catch(Exception e)
			{
				System.out.println(e);
				errmsg = "File location error.";
				return false;
			}
		return true;
	}
	
	public boolean loadFile(String filename)
	{
		if(!setLocator(filename)) return false;
				try
				{
					proc = Manager.createProcessor(locator); 
				}catch (Exception ee)
					{
						System.out.println(ee);
						errmsg = "File Error.";
						return false;
					}
				statemon = new StateMonitor(proc);
				statemon.configure();
				proc.setContentDescriptor(null); // to use processor as player
				statemon.realize();
				duration = proc.getDuration().getSeconds();
	    volume = (GainControl) proc.getControl("javax.media.GainControl");
	    return true;
			}

	public void rew()
	{
		setShuttleMode(false);
		setShuttleState(true);
	}

	public void stopplay()
	{
		playing = false;
		paused = false;
		recording = false;
		if(statemon != null)
		{
			statemon.stop();
		}
		playthread = null;
				//		timedisplay.stop();
			// dtext = "Ready.";
	}
	
	public synchronized void play()
	{
			paused = false;
				playing = true;
			if(playthread == null) playthread = new PlayThread();
			if(!playthread.isAlive())
			{
			System.out.println("start in play");
			 playthread.start();
			 }
	}
	
	public void ff()
	{			
		setShuttleMode(true);	
		setShuttleState(true);
	}

	public synchronized boolean capture(String filename)
	{
		try 
		{
			proc = Manager.createProcessor(info.getLocator());
		}catch(Exception e) 
			{
             System.out.println(e);
             errmsg = "Recorder Error.";
             return false;
         } 
		statemon = new StateMonitor(proc);
		statemon.configure();

		if(recvideo)
		{
			fileext = "*.avi";
		 	 proc.setContentDescriptor(
					new FileTypeDescriptor(FileTypeDescriptor.MSVIDEO));		
		}else
			{
			fileext = "*.mp3";
				proc.setContentDescriptor(
						new FileTypeDescriptor(FileTypeDescriptor.MPEG_AUDIO));
				TrackControl track[] = proc.getTrackControls(); 

				track[0].setFormat(new AudioFormat(AudioFormat.MPEGLAYER3)); 
			}

			statemon.realize();
	 	if(!setLocator(filename)) return false;
		DataSource source = proc.getDataOutput();
		
		try 
		{
			filewriter = Manager.createDataSink(source, locator);
			filewriter.open();
				filewriter.start();
		}catch(Exception e) 
			{
				System.out.println(e);
				errmsg = "File Error.";
				return false;
		} 
		recthread = new RecThread();
		recthread.start();
		return true;
	}	
	
	public boolean prepareCapture()
	{
		paused = false;
		recording = true;
	 	info = null;
		Vector deviceList = CaptureDeviceManager.getDeviceList(null);

		if (deviceList.size()==0)
		{
			errmsg = "No devices found.";
			return false;
		}
		
		for(int i=0;i<deviceList.size();i++)
		{
			recvideo = false;
			Format[] f = ((CaptureDeviceInfo)deviceList.elementAt(i)).getFormats();
			for(int j=0;j<f.length;j++)
			{
				if(f[j].equals(vformat)) 
				{
					info = (CaptureDeviceInfo)deviceList.elementAt(i);
					recvideo = true;
					break;
				}else
				if(f[j].equals(aformat))
					info = (CaptureDeviceInfo)deviceList.elementAt(i);
			}
		}
		if (info==null)
		{
			errmsg = "No suitable devices found.";
			return false;
		}

		fileext = recvideo ? "*.avi" : "*.mp3";       
		return true;
	}	
	
	public String getFileExt()
	{
		return fileext;
	}
	
	
	public void setPause(boolean p)
	{
	System.out.println(p);
		this.paused = p; //
		if(pausethread == null) pausethread = new PauseThread();
		if(p)
		{
			if(!pausethread.isAlive())
			{
				System.out.println("pstart"); 
				pausethread.start();
			}
		}
		  else 
		  {
		  		pausethread = null;
			}
	}
	
	public boolean isPause()
	{	
		return paused;
	}
						
	public Component getVisualComponent()
	{
		return proc.getVisualComponent();
	}
	
	public double getTime()
	{
		double tim = proc.getMediaTime().getSeconds();
		return Math.rint(tim*100)/100;
	}
	
	public void setVolume(float level)
	{
		if(volume != null) volume.setLevel(level);
	}
	
	class PlayThread extends Thread
	{
		public void run()
		{
			while(loop) //  
			{
				if(!paused) proc.setMediaTime(new Time(0));
	
												
				if(!statemon.isStarted())statemon.start();///

			}
				statemon.stop();
				System.out.println("threadend");
		}
	}
	
	class PauseThread extends Thread
	{
		public void run()
		{
			statemon.stop();
			while(paused)
			{
			System.out.println(paused);
				try
				{
					Thread.sleep(100);
				}catch (Exception e){System.out.println("interrupted");}
			}
				 statemon.start();
		}
	}
	
	class RecThread extends Thread
	{
		public void run()
		{
			statemon.start();
			while(recording)
			{
				try
				{
					Thread.sleep(100);
				}catch (Exception e){}
			}
		}
	}
} // End of MediaBox.java

// load  = 0
// loop  = 1
// rew   = 2
// stop  = 3
// play  = 4
// ff    = 5
// rec   = 6
// pause = 7 
// exit  = 8	