package com.dannyayers.sound.pump;
import javax.media.*;

public class StateMonitor implements ControllerListener 
	{
		final static long TIMEOUT = 10000; // 5000 good ,test with too short - include error
		long starttime;
		Player player = null;
		boolean configured = false;
		boolean realized = false;
		boolean prefetched = false;
		boolean end = false;
		boolean error = false;
		boolean closed = false;
   		double duration;
		double currenttime;   
		boolean capture = false;
	    
	public StateMonitor(Player p) 
	{
		player = p;
		p.addControllerListener(this);
	}
	
	public boolean isStarted() 
	{
			System.out.println("player.getState() = "+player.getState());
		return (player.getState() == Player.Started);
	}
	
	public synchronized boolean configure() 
	{
		starttime = System.currentTimeMillis();
		if (player instanceof Processor) ((Processor)player).configure();
			else return false;
		while (!configured && !error) 
		{
			try 
			{
				wait(TIMEOUT);
			}catch (Exception e){}
		if (System.currentTimeMillis()-starttime > TIMEOUT)break;
		}
		return configured&&!error;
    }
     
	public synchronized boolean realize() 
	{
		starttime = System.currentTimeMillis();
		player.realize();
		while(!realized && !error) 
		{
			try 
			{
				wait(TIMEOUT);
			}catch(Exception e){}
			if (System.currentTimeMillis()-starttime > TIMEOUT) break;
		}
		return realized && !error;
   }

	public synchronized boolean prefetch() 
	{
		starttime = System.currentTimeMillis();
		player.prefetch();
		while(!prefetched && !error) 
		{
			try 
			{
				wait(TIMEOUT);
			}catch(Exception e){}
		if(System.currentTimeMillis()-starttime > TIMEOUT) break;
		}
		return prefetched && !error;
    }

	public void start()
	{
		this.start(false);
	}
 

	public void stop()
	{
	System.out.println("ssss");
		player.stop();
	}
 
	public synchronized boolean start(boolean cap) 
	{
		this.capture = cap;

		starttime = System.currentTimeMillis();
		player.start();

		while (!end && !error) 
		{
			try 
			{
				wait();
			}catch(Exception e) {}

			if (capture)break;
		}
		return end && !error;
	}

	public synchronized void close() 
	{
	System.out.println("just before player.close() in statemon close()");
		player.close();
		System.out.println("just after player.close() in statemon close()");
		while(!closed) 
		{
			try 
			{
				wait(TIMEOUT);
			}catch(Exception e){}
		}
		player.removeControllerListener(this);
	}
	
	public double getDurationSeconds()
	{ 
		double ds = 0;
		if(player!=null)
		{
			Time d = player.getDuration();
			if(d != Time.TIME_UNKNOWN) ds = d.getSeconds();
		}
		return ds;
	}
	
	public double getMediaTimeSeconds()
	{
		double mts = 0;
		if(player != null)
		{
			Time mt = player.getMediaTime();
			if(mt != Time.TIME_UNKNOWN) mts = mt.getSeconds();
		}
		return mts;		
	}
	
	public boolean isCapture()
	{
		return capture;
	}

	public boolean isEnd()
	{
		return end;
	}

	
	public synchronized void controllerUpdate(ControllerEvent cevent) 
	{
		end = false;
	// System.out.println(cevent.getClass());
		if (cevent instanceof RealizeCompleteEvent) 
		{
			realized = true;
		} 
		else if(cevent instanceof ConfigureCompleteEvent) 
		{
			configured = true;
		} 
		else if(cevent instanceof PrefetchCompleteEvent) 
		{
			prefetched = true;
		} 
		else if(cevent instanceof EndOfMediaEvent) 
		{
			end = true;
		//	active = false;
		} 
		else if(cevent instanceof ControllerErrorEvent) 
		{
			error = true;
			// active = false;
		} 
		else if(cevent instanceof ControllerClosedEvent) 
		{
			closed = true;
			// active = false;
		} 
		else return;
		notifyAll();
	}
}
