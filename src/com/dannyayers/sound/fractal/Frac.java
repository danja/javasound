package com.dannyayers.sound.fractal;
/*
 *		Frac.java - a fractal Midi sequence generator 
 */

import java.util.*;


public class Frac extends Thread
{
	private static PlinkSynth synth = new PlinkSynth();

	private static int notes1[] = {36, 38, 39, 41, 43, 46, 48, 50, 51, 53, 55, 58, 60}; 
	private static int notes2[] = {24, 27, 29, 31, 34, 36};

	private double k;
	private double v;
	private double x;
	private double t;
	private double tscale;
	private int notes[];
	private int instrument;

	public static void main(String[] args)
	{

		Frac one =	new Frac(92, 3.7, 0.4, 0.5, 0.6, 2, notes1); //3.7
		Frac two =new Frac(100, 3.7, 0.5, 0.7, 0.6, 4, notes2);
		(new Thread(one)).start();
		(new Thread(two)).start();
	}

	public Frac(int inst, double c, double seedpitch, double seedvelocity, double seedtempo, double temposcale, int[] selection)
	{
		this.instrument = inst;
		this.k = c;
		this.v = seedvelocity;
		this.x = seedpitch;
		this.t = seedtempo;
		this.tscale = temposcale;
		this.notes = selection;
	}

	public void run()
	{
		int note;

		while(true)
		{
			x = Logistic(k,x); 
			t = Logistic(k,t); 
			v = Logistic(k,v);
			note = notes[(int)(x * notes.length)];
			synth.programChange(instrument); 
			synth.noteOn(note, (int)(v*v*v*128));

			try
			{
				this.sleep((int)(t*tscale*500));
			}
			catch(InterruptedException ie)
				{
					System.out.println(ie);
				}
			synth.noteOff(note);
		}
	}

	static double Logistic(double k, double x)
	{	
		return k*x*(1-x);
	}
}


