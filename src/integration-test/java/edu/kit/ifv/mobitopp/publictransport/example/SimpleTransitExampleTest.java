package edu.kit.ifv.mobitopp.publictransport.example;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.example.SimpleNetwork.fourOClock;
import static edu.kit.ifv.mobitopp.publictransport.example.SimpleNetwork.oneOClock;
import static edu.kit.ifv.mobitopp.publictransport.example.SimpleNetwork.threeOClock;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.Time;

public class SimpleTransitExampleTest {

	private SimpleNetwork network;

	@Before
	public void initialise() {
		network = new SimpleNetwork();
	}
	
	@Test
	public void routeFromStopToStop() {
		Stop fromAmsterdam = network.amsterdam();
		Stop toBerlin = network.berlin();
		Time atNoon = network.noon();
		Connection amsterdamToDortmund = network.amsterdamToDortmund();
		Connection dortmundToBerlin = network.dortmundToBerlin();
		
		RouteSearch connectionScan = network.connectionScan();
		
		Optional<PublicTransportRoute> potentialRoute = connectionScan.findRoute(fromAmsterdam, toBerlin, atNoon);
		
		assertThat(potentialRoute, isPresent());
		PublicTransportRoute route = potentialRoute.get();
		assertThat(route.arrival(), is(equalTo(threeOClock)));
		assertThat(route.start(), is(equalTo(fromAmsterdam)));
		assertThat(route.end(), is(equalTo(toBerlin)));
		assertThat(route.connections(), contains(amsterdamToDortmund, dortmundToBerlin));
	}
	
	@Test
	public void anotherRouteFromStopToStop() {
		Stop fromChemnitz = network.chemnitz();
		Stop toBerlin = network.berlin();
		Time atOneOClock = oneOClock;
		Connection chemnitzToBerlin = network.chemnitzToBerlin();
		
		RouteSearch connectionScan = network.connectionScan();
		
		Optional<PublicTransportRoute> potentialRoute = connectionScan.findRoute(fromChemnitz, toBerlin, atOneOClock);
		
		assertThat(potentialRoute, isPresent());
		PublicTransportRoute route = potentialRoute.get();
		assertThat(route.arrival(), is(equalTo(fourOClock)));
		assertThat(route.start(), is(equalTo(fromChemnitz)));
		assertThat(route.end(), is(equalTo(toBerlin)));
		assertThat(route.connections(), contains(chemnitzToBerlin));
	}
}
