package edu.kit.ifv.mobitopp.publictransport.model;

class NoJourney implements ModifiableJourney {

	private static final int defaultId = -2;

	NoJourney() {
		super();
	}

	@Override
	public int id() {
		return defaultId;
	}

	@Override
	public Time day() {
		return Time.infinite;
	}

	@Override
	public Connections connections() {
		return new Connections();
	}

	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public TransportSystem transportSystem() {
		return new TransportSystem("no system");
	}

	@Override
	public void add(Connection connection) {
	}
}