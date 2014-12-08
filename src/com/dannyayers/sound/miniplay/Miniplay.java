package com.dannyayers.sound.miniplay;
/*
 * 	Miniplay.java - a minimal JMF media player
 */

import java.net.*;
import java.io.*;
import javax.media.*;

public class Miniplay
{
	Player player = null;
	MediaLocator locator = null;

	public static void main(String args[])
	{
		String mysound = args[0];
		Miniplay mp = new Miniplay(mysound);	
	}

	public Miniplay(String mysound)
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
		player.start(); 
	}	
}

