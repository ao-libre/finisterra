package shared.network.time;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

import java.util.concurrent.atomic.AtomicInteger;

public class TimeSyncRequest implements IRequest {
	private static final AtomicInteger nextId = new AtomicInteger();

	public int requestId;

	public static TimeSyncRequest getNextRequest() {
		return new TimeSyncRequest(nextId.getAndIncrement());
	}

	public TimeSyncRequest() {
	}

	public TimeSyncRequest(int requestId) {
		this.requestId = requestId;
	}

	@Override public void accept(IRequestProcessor processor, int connectionId) {
		processor.processRequest(this, connectionId);
	}
}
