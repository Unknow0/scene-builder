package unknow.scene.builder;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/**
 * onLoad event listener
 * 
 * @author unknow
 */
public abstract class LoadListener implements EventListener {
	@Override
	public boolean handle(Event event) {
		if (!(event instanceof LoadEvent))
			return false;
		loaded((LoadEvent) event);
		return false;
	}

	/**
	 * called when the object finished loading
	 * 
	 * @param e the event
	 */
	public abstract void loaded(LoadEvent e);

	/**
	 * the load event
	 * 
	 * @author unknow
	 */
	public static class LoadEvent extends Event {
	}
}
