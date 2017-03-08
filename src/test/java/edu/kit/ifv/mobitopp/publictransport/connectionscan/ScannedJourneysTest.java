package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.junit.Assert.assertThat;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;

public class ScannedJourneysTest {

	@Test
	public void returnsTrueIfJourneyWasUsed() throws Exception {
		Journey journey = journey().build();

		ScannedJourneys scannedJourneys = scannedJourneys();
		scannedJourneys.use(journey);

		assertThat(scannedJourneys, used(journey));
	}

	@Test
	public void returnsFalseWhenJourneyWasNotUsed() throws Exception {
		Journey journey =  journey().build();

		ScannedJourneys scannedJourneys = scannedJourneys();

		assertThat(scannedJourneys, notUsed(journey));
	}

	private ScannedJourneys scannedJourneys() {
		return new ScannedJourneys();
	}

	private static Matcher<ScannedJourneys> used(Journey journey) {
		return new TypeSafeMatcher<ScannedJourneys>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("used");
				description.appendValue(journey);
			}

			@Override
			protected boolean matchesSafely(ScannedJourneys journeys) {
				return journeys.used(journey);
			}

			@Override
			protected void describeMismatchSafely(ScannedJourneys item, Description mismatchDescription) {
				mismatchDescription.appendText("not used");
				mismatchDescription.appendValue(journey);
			}
		};
	}

	private static Matcher<ScannedJourneys> notUsed(Journey journey) {
		return new TypeSafeMatcher<ScannedJourneys>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("not used");
				description.appendValue(journey);
			}

			@Override
			protected boolean matchesSafely(ScannedJourneys journeys) {
				return !journeys.used(journey);
			}

			@Override
			protected void describeMismatchSafely(ScannedJourneys item, Description mismatchDescription) {
				mismatchDescription.appendText("used");
				mismatchDescription.appendValue(journey);
			}
		};
	}
}
