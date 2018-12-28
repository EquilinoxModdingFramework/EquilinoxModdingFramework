package kd.equilinox.events.sessions;

import kd.equilinox.events.Event;

/**
 * Event which is fired after the game loads next session.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class PostLoadSessionEvent extends Event {
	private Object session;

	public PostLoadSessionEvent(Object session) {
		this.session = session;
	}

	/**
	 * @return Returns session which is currently being load.
	 */
	public Object getSession() {
		return this.session;
	}
}
