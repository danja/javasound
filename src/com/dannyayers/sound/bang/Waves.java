package com.dannyayers.sound.bang;
public class Waves
{
	static int amplitude = 32767;
	static int points = 65536;
	static int ratio = points/amplitude;
	static int[] sine = new int[points];

	static
	{
		for(int i = 0;i<points;i++)
		{
			sine[i] = (int) (Math.sin((double)i * (double)2.0 * Math.PI/(double)points) * amplitude);
		}
	}

	static int wave(String type, int position)
	{
		int value = 0;
		if(type.equals("Sine"))	value = sine[position];
		if(type.equals("Square"))	value = position<points/2 ? -amplitude : amplitude;
		if(type.equals("Sawtooth"))	value = amplitude-2*position/ratio;
		if(type.equals("Triangle"))	value = position<points/2 ? 4*position/ratio-amplitude : 4*(amplitude-position)/ratio + amplitude+1;
		if(type.equals("Noise"))	value = (int) (Math.random()*2*amplitude-amplitude);
		return value;
	}
}