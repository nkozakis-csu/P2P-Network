package cs455.overlay.wireformats;

public class EventFactory {
	private static EventFactory eventFactory = new EventFactory();
	
	private EventFactory(){
	}
	
	public static EventFactory getInstance(){
		return eventFactory;
	}
}
