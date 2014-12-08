package com.dannyayers.sound.pump;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;

public class Knob extends JPanel 
{
	int angle;
	int centrex = 13;
	int centrey = 14;
	Image image;
	Image bgimage;
	AffineTransform at;
    
	public Knob() 
	{
		angle = 0;
		image =  Toolkit.getDefaultToolkit().getImage("images/knob.gif"); 
		bgimage =  Toolkit.getDefaultToolkit().getImage("images/knobbg.gif"); 
		at = new AffineTransform();
	}

	public void paint(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D)g;
		at.setToIdentity();
		g2d.transform(at);
		g2d.drawImage(bgimage,0,0,this);
		at.rotate(Math.toRadians(angle),centrex,centrey);
		g2d.transform(at);
		g.drawImage(image, 0, 0, this);
	}
    
    
	public void add(int move) 
	{
		angle += move;
		angle = angle>120? 120:angle; 
		angle = angle<-120? -120:angle;
		repaint();
    }
    
	public float getValue() 
	{
		return((float)(120+angle)/240);
	}
}