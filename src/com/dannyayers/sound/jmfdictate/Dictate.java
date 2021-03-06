package com.dannyayers.sound.jmfdictate;
/**
 *		Dictate.java
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class Dictate implements ActionListener
{
	static AppWindow window; 
	Record rec;
	Play play;

	static JProgressBar meterbar;
	static JButton recordButton;
	static JButton stopButton;
	static JButton playButton;
	Color meterback = SystemColor.control;

	static int level=0;
	static boolean showlevel;
//	static int threshold = 10;
//	static int buffersize = 16384; 
	static long starttime;
	double duration;
	boolean capturemode;
	LevelUpdate levelupdate;
		
    public static void main(String[] args)
    {
		window = new AppWindow("Dictate", new Dimension(250,75));
		Dictate  dictate = new Dictate();
		window.setVisible(true);
	}

	public Dictate()
	{
		JPanel buttonpanel = new JPanel();

		recordButton = new JButton("Record");
		recordButton.addActionListener(this);
		buttonpanel.add(recordButton);

		stopButton = new JButton("Stop");
		stopButton.addActionListener(this);
		buttonpanel.add(stopButton);

		playButton = new JButton("Play");
		playButton.addActionListener(this);
		buttonpanel.add(playButton);

		Container content = window.getContentPane();
		content.setLayout(new BorderLayout());
		content.add(buttonpanel, BorderLayout.SOUTH);

		meterbar = new Meterbar(0,128);
		meterbar.setBorderPainted(true);
		meterbar.setForeground(Color.green);
		content.add(meterbar, BorderLayout.CENTER);
		rec = new Record();
		play = new Play();
		EnableButtons(true, false, false);
	}

	static void EnableButtons(boolean start, boolean stop, boolean play)
	{
		recordButton.setEnabled(start);
		stopButton.setEnabled(stop);
		playButton.setEnabled(play);
	}		

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		
		if(command.equals("Record")) 
		{
			capturemode = true;
			startMeter();
			rec = new Record();
			rec.startRec();

			EnableButtons(false, true, false);	
		}

		else if(command.equals("Stop")) stopActivity();

		else if(command.equals("Play")) 
		{
			System.out.println("------------Play clicked--------");
			capturemode = false;
			play = new Play();
			play.startPlay();	
			EnableButtons(false, true, false);
			startMeter();
		}
	}

	public void stopActivity()
	{
			showlevel = false;
			if(capturemode) 
			{
				rec.stopRec();
			} else play.stopPlay();
	
			EnableButtons(true, false, true);
	}
	
	public void startMeter()
	{	
	System.out.println("start meter");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
		showlevel = true;
		levelupdate = new LevelUpdate();
		levelupdate.start();	
	}
			
			
	class LevelUpdate extends Thread
	{
		float oldlevel = 0;
		
		public void run() 
		{
			meterback = Color.black;
			level = 0;
			meterbar.repaint();
			System.out.println("showlevel = "+showlevel); 
 			while(showlevel)
			{		
				level = capturemode ? rec.getLevel() : play.getLevel();
				// problem is with is end
				if(!capturemode && play.isEnd())
				{
				System.out.println("capturemode = "+capturemode+"   play.isEnd() = "+play.isEnd());
			//	stopActivity();
			showlevel = false;
				 System.out.println("just after stopActivity() in LevelUpdate");
				}
				if(level != oldlevel)	meterbar.repaint();
				oldlevel = level;
				try
				{
				Thread.sleep(10);
				} 
				catch (InterruptedException ie)
				{
					System.out.println(ie);
				}
			}
			EnableButtons(true, false, true);
			level = 0;
			meterback = SystemColor.control;
			meterbar.repaint();
		}
	}

	
	class Meterbar extends JProgressBar
	{
		public Meterbar(int i, int j)
		{
			super(i,j);
		}
		
		    public void paint(Graphics g) 
	    {
    			setBackground(meterback);
				setValue((int)level);
				super.paint(g);
		}
		
		public void update(Graphics g)
		{
			paint(g);
		}
	}
}