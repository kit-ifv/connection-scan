package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.util.Arrays.asList;

import java.util.Collection;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.TransitNetwork;

public class TransitNetworkTest {

	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnWrongStopIds() {
		Stop first = stop().withId(0).build();
		Stop tooHighId = stop().withId(2).build();
		Collection<Stop> stops = asList(first, tooHighId);
		
		TransitNetwork.createOf(stops, noConnections());
	}

	private Connections noConnections() {
		return new Connections();
	}
}
