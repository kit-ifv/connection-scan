package edu.kit.ifv.mobitopp.publictransport.model;

public interface ModifiableJourney extends Journey {

	void add(Connection connection);

	public static final ModifiableJourney noJourney = new NoJourney();

}