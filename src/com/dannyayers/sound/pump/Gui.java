package com.dannyayers.sound.pump;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;

public class Gui extends JWindow implements MouseListener,MouseMotionListener 
{
	private int knoby;
	private Image bgimage;

	private JButton[] buttons;
    
	private Knob volknob;
	private Knob pressedKnob;
	private boolean onknob;
	private Point startPoint;
	private Point startLocation;
	private ImageIcon icon;
	static JLabel display;
	static JWindow window;
	private String lastdir;

	String[] bname = {"load","loop","rew","stop","play","ff","rec","pause","exit"};

    static JWindow vidscreen = null;
    static int WIDTH = 291;
    static int HEIGHT = 76;
		
	String filename;

	private int vidshow=0;
	Dimension[] vidsizes = new Dimension[3];
	Component vid;
	Dimension screenSize;
	private String dtext = new String();
	static boolean mousedown;

	static boolean forward;
	javax.swing.Timer displaytimer;
	static String displaytext = new String("The Pump");
	boolean displaywarning = false;
	static boolean showtime = false;
	static boolean recording;
	MediaBox mediabox = new MediaBox();
		    
	public Gui(JWindow win) 
	{
		window = win;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-WIDTH)*2/3,(screenSize.height-HEIGHT)*2/3);
		setVisible(false);
	setSize(WIDTH,HEIGHT);

		try 
		{
			UIManager.setLookAndFeel(
			UIManager.getCrossPlatformLookAndFeelClassName());
 		}catch(Exception e){}
        
		Container content = getContentPane();
		content.setLayout(null);
   
		bgimage = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/background.gif"));

		display = new JLabel();

		volknob = new Knob();
		volknob.addMouseMotionListener(this);
		volknob.addMouseListener(this);

		display.setBackground(Color.black);
		display.setOpaque(true); 
		buttons = new JButton[9];
		for(int i = 0;i < 9;i++) 
		{
			icon = new ImageIcon(getClass().getClassLoader().getResource("images/"+bname[i]+".gif"));
			buttons[i] = new JButton(icon);
			icon = new ImageIcon(getClass().getClassLoader().getResource("images/"+bname[i]+"d.gif"));
			buttons[i].setDisabledIcon(icon);
			icon = new ImageIcon(getClass().getClassLoader().getResource("images/"+bname[i]+"p.gif"));
			buttons[i].setPressedIcon(icon) ;
			buttons[i].setActionCommand(bname[i]);
			buttons[i].addMouseListener(this);
			buttons[i].setBorder(null);
			content.add(buttons[i]);
		}
		content.add(display);
		content.add(volknob);
		content.addMouseMotionListener(this);
		content.addMouseListener(this);
		InitialPositionSet();
		setVisible(true);
		
		displaytimer = 
			new javax.swing.Timer(100, new ActionListener() 
			{ 
				public void actionPerformed(ActionEvent evt) 
				{
					if(displaywarning)
					{ 
						display.setForeground(Color.red); 
					} else display.setForeground(Color.green);
					if(recording)
					{ 
						display.setBackground(Color.red); 
					} else display.setBackground(Color.black);					
					if(showtime)
					{
 		 				display.setText("     "+mediabox.getTime());
					} else display.setText("     "+displaytext);
					display.repaint();
		 		} 
			}); 
		displaytimer.start();
	}


	 
	public void paint(Graphics g) 
	{
		g.drawImage(bgimage,super.getInsets().left,super.getInsets().top,this);
		for(int i = 0;i < 9;i++) 
		{
			buttons[i].repaint();
		}
		volknob.repaint();
	}
    
	public void InitialPositionSet()
	{
		display.setBounds(90,8,190,22);//180
		volknob.setBounds(8,6,26,28);
		buttons[0].setBounds(30,44,46,26); 	//load
		buttons[1].setBounds(42,6,27,26); 	//loop
		buttons[2].setBounds(91,46,30,22); 	//rew
		buttons[3].setBounds(123,46,30,22); 	//stop
		buttons[4].setBounds(155,46,30,22); 	// play 
		buttons[5].setBounds(187,46,30,22); 	// ff
		buttons[6].setBounds(219,46,30,22); 	// rec 
		buttons[7].setBounds(251,46,30,22);	//pause 
		buttons[8].setBounds(4,60,14,13);		//exit

		EnableButtons("yynnnnyyy");
	}
    
	public void mousePressed(MouseEvent e) 
	{
		if(displaywarning)
		{
			displaywarning = false;
			stop();
			return;
		}
		mousedown = true;
		toFront();
		startPoint = e.getPoint();
		onknob = false;
		if((startPoint.x<30)&&(startPoint.y<30)) onknob = true;
		if(e.getComponent() instanceof Knob) 
		{
			knoby = e.getY();
		} else
		if(e.getComponent().getName()=="vid")
		{
			resize();
		}  else
		if(e.getComponent() instanceof JButton) 
		{
			displaywarning = false;
			boolean buttonok = ((JButton)(e.getComponent())).isEnabled();
			{
				String command = ((JButton)(e.getComponent())).getActionCommand();

				if(command.equals("load") && buttonok) 	load();
				if(command.equals("loop")) 					loop();
				if(command.equals("rew") && buttonok) 	rew(); 
				if(command.equals("stop") && buttonok) 	stop();	
				if(command.equals("play") && buttonok) 	play();
				if(command.equals("ff") && buttonok) 		ff();
				if(command.equals("rec") && buttonok) 	rec();
				if(command.equals("pause")) 				pause();
				if (command.equals("exit")) System.exit(0);
			}
		}
	}
	
	public void load()
	{
		showtime = false;
		displaytext = "Select file...";
		String filename = fileDialog(false, "*.*");
		displaytext = "Initialising"+filename;
		if(!mediabox.loadFile(filename)) 
		{
			displaywarning = true;
			displaytext = mediabox.getErrMsg();
			return;
		}
		addVideo();
		play();
	}

	public String fileDialog(boolean save_load, String ext)
	{
		FileDialog fd;
		if(save_load)
		{
			fd = new FileDialog(new Frame(), "Save recording as...", FileDialog.SAVE);


		}else	fd = new FileDialog(new Frame(), "Open File", FileDialog.LOAD);
		if (lastdir != null) fd.setDirectory(lastdir);
			fd.setFile(ext);
				fd.show();
				lastdir = fd.getDirectory();
				System.out.println(lastdir);
				filename = fd.getFile();
				if (filename == null) return null;
					else 
					{
						dtext = filename;
					}
				fd.dispose();
				EnableButtons("n*yynynyy");
				repaint(); //?
		return lastdir + filename;

	}
	
	public void loop()
	{
		if(mediabox.isLoop())
		{
			mediabox.setLoop(false);
			EnableButtons("*n*******");
			displaytext = "Off";
		}
		else
			{
			mediabox.setLoop(true);
				EnableButtons("*y*******");
				displaytext = "On";
			}
		displaytext = "Loop "+displaytext;
	}

	public void rew()
	{
		mediabox.setShuttleMode(false);
		mediabox.setShuttleState(true);
	}

	public void stop()
	{
		if(vidscreen != null) vidscreen.setVisible(false);
		showtime = false;
		recording = false;
		EnableButtons("y*nnynyyy");
		mediabox.stopplay();	
		displaytext = "Ready.";
	}
	
	public void play()
	{
		EnableButtons("n*yynynyy");
		mediabox.play();
		showtime = true;
	}
	
	public void ff()
	{			
		mediabox.setShuttleMode(true);	
		mediabox.setShuttleState(true);
	}

	public void rec()
	{
		EnableButtons("nnnynnnyy");
		displaytext = "Save recording as...";
		
		if(!mediabox.prepareCapture())
		{
			displaywarning = true;
			displaytext = mediabox.getErrMsg();
			return;
		}
		String filename = fileDialog(true,mediabox.getFileExt());
		if(!mediabox.capture(filename))
		{
			displaywarning = true;
			displaytext = mediabox.getErrMsg();
			return;
		}

		showtime = true;
		recording = true;
	}	
	
	public void pause()
	{
		if(!mediabox.isPause())
		{
			mediabox.setPause(true);
			showtime = false;
			displaytext = "Pause On";
			EnableButtons("y*yyyynny");
		} else
		{
			mediabox.setPause(false);
			showtime = true;
			displaytext = "Pause Off";
			EnableButtons("y*yyyynyy");
		}
	}
						
	public void mouseDragged(MouseEvent e)
	{
		if(onknob)
		{
			int y = e.getY();
			int change = knoby - y;
			if(change != 0) volknob.add(change);
			mediabox.setVolume(volknob.getValue()); 
		}
		else
		{
			startLocation = getLocation();
			setLocation(startLocation.x - startPoint.x + e.getPoint().x,
			startLocation.y - startPoint.y + e.getPoint().y);
			Toolkit.getDefaultToolkit().sync();
			repaint();
		}		
	}		

	public void mouseEntered(MouseEvent e){}   
	public void mouseExited(MouseEvent e){}   
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	public void mouseReleased(MouseEvent e)
	{
		mousedown = false;
		if(mediabox!=null)mediabox.setShuttleState(false);
		if(vidscreen!=null)vidscreen.repaint();
	}
	    
	public void EnableButtons(String on)
	{
		for(int i = 0;i < 9;i++) 
		{  
			if(on.charAt(i) == 'y')buttons[i].setEnabled(true);
			if(on.charAt(i) == 'n')buttons[i].setEnabled(false);
		}
		repaint();
	}

	public void addVideo()
	{
		vidscreen = new JWindow(window);
		vid = mediabox.getVisualComponent();
		if (vid != null) 
		{
			vid.setName("vid");
			vid.addMouseListener(this);
			vidscreen.getContentPane().add(vid);
			vidsizes[1] = vid.getPreferredSize();
			vidsizes[0] = new Dimension(WIDTH,(int)(WIDTH*vidsizes[1].getHeight()/vidsizes[1].getWidth()));
			vidsizes[2] = new Dimension(screenSize);
			vidshow = 2;
			resize();
		}
	}
	
	private void resize() 
	{
		int vidy = 0;
		Point location = getLocation();
		
		vidscreen.setVisible(false);
		vidshow = (vidshow+1)%3;
		vidscreen.setSize(vidsizes[vidshow]); 
		if(vidshow==2)
		{
			location.setLocation(0,0);
		} else vidy = -vidscreen.getSize().height;
		location.translate(0,vidy);
		vidscreen.setLocation(location);
		vidscreen.setVisible(true);
	}
/*	
	class MediaFileFilter implements FilenameFilter
	{
		public boolean accept(File file, String filename)
		{
			// return filename.endsWith(".avi") || filename.endsWith(".mp3");
			return false;
		}
	}
*/
} 
		
// End of GUI.java
// load  = 0
// loop  = 1
// rew   = 2
// stop  = 3
// play  = 4
// ff    = 5
// rec   = 6
// pause = 7 
// exit  = 8	