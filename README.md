# Overview
The connection-scan is a route search algorithm for transit networks. The 
algorithm itself is described in various papers ([CSA](https://www.researchgate.net/publication/257361294_Intriguingly_Simple_and_Fast_Transit_Routing), [CSA Accelarated](https://www.researchgate.net/publication/286955308_Connection_Scan_Accelerated)).
The implementation is developed at the [Institute for Transport Studies]
(http://www.ifv.kit.edu) at the Karlsruhe Institute of Technology. The route search algorithm is part of our travel demand model [mobiTopp](http://www.ifv.kit.edu/359.php).

# Usage
To search routes via the connection scan, you have to initialise it with a transit network made up of stops and connections. During initialisation the algorithm will check the following prerequisites.
1. All stop ids must be in the range from `0` to `n`. This is needed for some performance improvements.
2. Connections are not allowed to start and end at the same stop. Those connections will be ignored.
3. Connections are not allowed to arrive before they depart. Due to this, travelling back in time is not possible.

[SimpleTransitExampleTest](https://github.com/mobitopp/connection-scan/blob/master/src/integration-test/java/edu/kit/ifv/mobitopp/publictransport/example/SimpleTransitExampleTest.java) shows how the connection scan can be used.
Route search requests can be made from single stops to single stops or from many to many stops. In case the request is from many to many, the access and egress paths have to be provided. Those paths will be considered during route search to find the earliest arrival at the target.