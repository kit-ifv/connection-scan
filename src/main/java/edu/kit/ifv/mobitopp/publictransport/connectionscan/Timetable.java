package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public interface Timetable {

	Stop stopFor(int id);

	Connection connectionFor(int id);

}
