package unknow.scene.builder;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class LoadListener implements EventListener
	{
	@Override
	public boolean handle(Event event)
		{
		if(!(event instanceof LoadEvent))
			return false;
		loaded((LoadEvent)event);
		return false;
		}

	public abstract void loaded(LoadEvent e);

	public static class LoadEvent extends Event
		{
		}
	}
