package com.dannyayers.sound.miniplay;
/*
 * 	Miniplay2.java - a minimal JMF media player with ControllerListener
 */

import java.net.*;
import java.io.*;
import javax.media.*;

public class Miniplay2
{
	Player player = null;
	MediaLocator locator = null;

	public static void main(String args[])
	{
		String mysound = args[0];
		Miniplay2 mp = new Miniplay2(mysound);	
	}

	public Miniplay2(String mysound)
	{
		File soundFile = new File(mysound);
		try
		{
			locator = new MediaLocator(soundFile.toURL());
			player = Manager.createPlayer(locator); 
		} 
		catch (Exception e)
			{
				System.out.println(e);
			}
		player.addControllerListener(new eventListener());
		player.start(); 
	}

	public class eventListener implements ControllerListener
	{
		public void controllerUpdate(ControllerEvent cevent) 
		{
			System.out.println(cevent.getClass());
			if(cevent instanceof EndOfMediaEvent) 
			{
				player.stop();
				player.deallocate();
				player.close();
			}
			if(cevent instanceof ControllerClosedEvent) 
			{
				player = null;
				System.exit(0);
			}

		}		
	}
}

