package com.dannyayers.sound.jmfdictate;
/*
 * 	Play.java
 */

import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.media.*;


public class Play extends Thread 
{
	Player player = null;
	StateMonitor statemon;
	double duration;

	public void run()
	{
		MediaLocator locator = null;
		File soundFile = new File("dictate.mp3");
		try
		{
			locator = new MediaLocator(soundFile.toURL());
		} 
		catch(Exception e)
			{
				System.out.println(e);
			}
		try
		{
			player = Manager.createPlayer(locator); 
		} 
		catch (Exception e)
			{
				System.out.println(e);
			}
		statemon = new StateMonitor(player);
		statemon.configure();
		statemon.realize();
		duration = statemon.getDurationSeconds();
		System.out.println("just before statemon.start in Play run()");
		statemon.start(); 
System.out.println("1111");
		statemon.close();
	}	
	
	public void startPlay()
	{
		this.start();
	}

	public void stopPlay()
	{
	System.out.println("just before statemon.close() in Play stopPlay()");
		statemon.close();
		System.out.println("just after statemon.close() in Play stopPlay()");
	}

	public boolean isEnd()
	{
		if(statemon != null)
		{
			return statemon.isEnd();
		} else return false;
	}
	
	public int getLevel()
	{
		if(statemon != null)
		{		
			return (int)(128*(statemon.getMediaTimeSeconds()/duration));
		} else return 0;
	}
}

