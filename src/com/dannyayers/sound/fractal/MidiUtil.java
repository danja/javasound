package com.dannyayers.sound.fractal;
public class MidiUtil
{
	private static String scale[] = {"C ","C#","D ","D#","E ","F ","F#","G ","G#","A ","A#","B "};
	public static String notes[]= new String[128];

  // Static initialization
  static
  {
		String octave;
		for(int i = 0 ; i<notes.length ; i++)
		{
			octave = (new Integer(i/12)).toString();
			if(octave.length() ==1) octave += " ";
			notes[i] = scale[i%12]+octave;
		}
  }
	public static int volume =  7;
	public static int pan	  = 10;
}

