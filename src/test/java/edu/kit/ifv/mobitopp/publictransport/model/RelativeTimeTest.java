package edu.kit.ifv.mobitopp.publictransport.model;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.temporal.ChronoUnit;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.RelativeTime;
import nl.jqno.equalsverifier.EqualsVerifier;

public class RelativeTimeTest {

	@Test
	public void madeUpOfSeconds() throws Exception {
		RelativeTime time = RelativeTime.of(1l, SECONDS);

		assertThat(time.seconds(), is(1l));
	}

	@Test
	public void oneMinuteContainsSixtySeconds() throws Exception {
		RelativeTime time = RelativeTime.of(1l, ChronoUnit.MINUTES);

		assertThat(time.seconds(), is(60l));
	}

	@Test
	public void oneHourContains3600Seconds() throws Exception {
		RelativeTime time = RelativeTime.of(1l, ChronoUnit.HOURS);

		assertThat(time.seconds(), is(3600l));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(RelativeTime.class).usingGetClass().verify();
	}
}
