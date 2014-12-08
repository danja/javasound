package com.dannyayers.sound.bang;
/**
 *  Bang.java
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import javax.swing.border.*; 

import	javax.sound.sampled.*;


public class Bang extends MouseInputAdapter implements  ActionListener, ChangeListener
{
	static AppWindow window; 
	JButton startbutton;
	JLabel scorelabel;
	JCheckBoxMenuItem menumusic;
	JCheckBoxMenuItem menusounds;
	JCheckBoxMenuItem menuwhine;
	MidiPlay midi;
	boolean musicon=false;

	ImagePanel panel;
	Image target;
	boolean hit;
	int count;
	int score;

	int twidth;
	int theight;
	int Xpos;
	int Ypos;
	int hitx;
	int hity;
	int xdistance;
	int ydistance;
	double targetscale;
	Tone tonex;
	Tone toney;
	int freqx;
	int freqy;
	Cues cues;
	Game game;
	Thread gamethread;
	boolean gameon;

    public static void main(String[] args)
    {
		window = new AppWindow("Bang", new Dimension(300,300));
		Bang bang = new Bang();
		window.setVisible(true);
	}

	public Bang() 
	{
	    Container content = window.getContentPane();

		JMenuBar menuBar = new JMenuBar();

		startbutton = new JButton("Start");
		startbutton.setBorderPainted(false);
		JMenu prefmenu = new JMenu("Preferences");

		startbutton.addActionListener(this);
		menumusic = new JCheckBoxMenuItem("Music",false);
		menusounds = new JCheckBoxMenuItem("Sounds",true);
		menuwhine = new JCheckBoxMenuItem("Whine",false);
		menumusic.addChangeListener(this);
		menusounds.addChangeListener(this);
		menuwhine.addChangeListener(this);
		prefmenu.add(menumusic);
		prefmenu.add(menusounds);
		prefmenu.add(menuwhine);
		scorelabel = new JLabel();

        menuBar.add(startbutton);
        menuBar.add(prefmenu);
		menuBar.add(scorelabel);
		window.setJMenuBar(menuBar);

		panel = new ImagePanel();
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); 
		panel.setBackground(Color.white);		
		panel.addMouseMotionListener(this);
		panel.addMouseListener(this);
		content.add(panel);


		target = Toolkit.getDefaultToolkit().getImage("critter.gif");
		twidth = target.getWidth(panel);
		theight = target.getHeight(panel);

		tonex = new Tone("Sine", 50, 0.8);
		toney = new Tone("Square", 50, 0.1);
		tonex.start();
		toney.start();

		midi  = new MidiPlay("test.mid");

		cues = new Cues();
		cues.addCue(startbutton);
		cues.addCue(prefmenu);
		cues.addCue(menumusic);
		cues.addCue(menusounds);
		cues.addCue(menuwhine);
		game = new Game();
	}

	public void actionPerformed(ActionEvent e) 
	{
		if(!gameon)	
		{
			count = 0;
			score = 0;
			scorelabel.setText("");
 			gamethread = new Thread(game); 
			gamethread.start();
		}
    }

	public void stateChanged(ChangeEvent e) 
	{
		if(menusounds.getState()&&!cues.enabled) cues.enabled = true;

		if(!menusounds.getState()&&cues.enabled) cues.enabled = false;

		if(menumusic.getState()&&!musicon)
		{
			if(!midi.isAlive()) midi.start();
			musicon = true;
			midi.Mute(false);
		} 
		if(!menumusic.getState()&&musicon)
		{
			midi.Mute(true);
			musicon = false;
		}

		if(menuwhine.getState())
		{
			tonex.On(true);
			toney.On(true);
		} else
			{
				tonex.On(false);
				toney.On(false);
			}
    }

    public void mouseMoved(MouseEvent e) 
	{
		xdistance = Math.abs(hitx-e.getX());
		ydistance = Math.abs(hity-e.getY());
		freqx = (xdistance!=0) ? 100+(int)(5000/xdistance) : 2000;
		freqy = (ydistance!=0) ? 100+(int)(5000/ydistance) : 2000;
		tonex.setFrequency(freqx);
		toney.setFrequency(freqy);
    }

    public void mousePressed(MouseEvent e) 
	{
		cues.cue("shot");
	}

    public void mouseClicked(MouseEvent e)
	{
		if(!hit && xdistance<theight*targetscale && ydistance<theight*targetscale)
		{
			hit = true;
			cues.cue("hit");
			score++;
			scorelabel.setText("     Score = "+score);
			panel.repaint();
		} else
			{
				cues.cue("miss");
			}
    }

	public void PaintTarget()
	{	
		targetscale = Math.random();
		Xpos = (int)( Math.random()*(panel.getWidth()-twidth));
		Ypos = (int)(Math.random()*(panel.getHeight()-theight));
		hitx = Xpos+(int)(targetscale*twidth/2);
		hity = Ypos+(int)(targetscale*theight/2);
		panel.repaint();
	}

	class Game extends Thread
	{
		public Game()
		{
			this.setDaemon(true);
		}

		public void run()
		{
			gameon = true;
			while(count<20)
			{
				if(hit)
				{
					try{
						this.sleep(500);
					} catch(InterruptedException ie){}
				}
				hit = false;
				cues.cue("target");
				PaintTarget();	
				count++;
				try{
					this.sleep((int)(800+Math.random()*2000));
				} catch(InterruptedException ie){}
			}
			scorelabel.setText("     Score = "+score+"   Game Over");
			gameon = false;
		}
	}

	class ImagePanel extends JPanel 
	{
		int height;

		public ImagePanel(){}

    	public void paint(Graphics g)
		{
			targetscale = targetscale > 0.2 ? targetscale : 0.2;
			super.paint(g);
			height = hit ? theight/2 : theight;
	       	g.drawImage(target, Xpos, Ypos, (int)(targetscale*twidth), (int)(targetscale*height), this);
	   }

		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) 
		{
			twidth = width;
			theight = height;
			return true;
		}
	}
}
