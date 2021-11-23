/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;



import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.PointsOfInterest;
import core.Settings;
import java.util.List;

import core.Coord;
/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class ShortestPathMapBasedMovement extends MapBasedMovement implements
	SwitchableMovement {
	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;

	/** Points Of Interest handler */
	private PointsOfInterest pois;



	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param settings The Settings object where the settings are read from
	 */
	public ShortestPathMapBasedMovement(Settings settings) {
		super(settings);
		this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
		this.pois = new PointsOfInterest(getMap(), getOkMapNodeTypes(),
				settings, rng);
	}

	/**
	 * Copyconstructor.
	 * @param mbm The ShortestPathMapBasedMovement prototype to base
	 * the new object to
	 */
	protected ShortestPathMapBasedMovement(ShortestPathMapBasedMovement mbm) {
		super(mbm);
		this.pathFinder = mbm.pathFinder;
		this.pois = mbm.pois;
	}


	//新しい行き先を決めるメソッド
	@Override
	public Path getPath() {
		Path p = new Path(generateSpeed());
		MapNode to = pois.selectDestination();

		//List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);
		List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);

		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
			to + ". The simulation map isn't fully connected";

		for (MapNode node : nodePath) { // create a Path from the shortest path
			//System.out.println(nodePath);

			//今向かっているマップノード→これをNodePathから削除したい
			p.addWaypoint(node.getLocation());
		}

		//lastMapNode = to;
		//System.out.println(p);
		return p;


	}




	//現在進行中のマップノードリストを作成する
	public List<MapNode> getPathNodeList() {
		MapNode to = pois.selectDestination();

		List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to,host);

		return nodePath;
	}




/*	public Path getPathBranchPoint(MapNode BranchNode) {

		Path p = new Path(generateSpeed());
		//MapNode to = pois.selectDestinationBranchPoint(BranchNode);


		List<MapNode> nodePath = pathFinder.getShortestPath(host.PathNodeList.get(host.PathCount), BranchNode,host);

		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
		BranchNode+ ". The simulation map isn't fully connected";

		for (MapNode node : nodePath) { // create a Path from the shortest path
			//System.out.println(nodePath);

			//今向かっているマップノード→これをNodePathから削除したい
			p.addWaypoint(node.getLocation());
		}

		lastMapNode =BranchNode;
		System.out.println(p);
		return p;
	}
	*/


	public Path getPathAnotherRout(MapNode BranchNode) {

		Path p = new Path(generateSpeed());
		MapNode to = pois.selectDestination();

		List<MapNode> nodePath = pathFinder.getShortestPath(BranchNode,to,host);

		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
		BranchNode+ ". The simulation map isn't fully connected";

		for (MapNode node : nodePath) { // create a Path from the shortest path
			//System.out.println(nodePath);

			//今向かっているマップノード→これをNodePathから削除したい
			p.addWaypoint(node.getLocation());
		}

		lastMapNode =to;
		//System.out.println(p);
		return p;
	}


	//災害地を避けたパスを返す
	@Override
	public List<MapNode> getAnotherPathNodeList(MapNode BranchNode) {
			MapNode to = pois.selectDestination();

			List<MapNode> nodePath = pathFinder.getShortestPath(BranchNode,to,host);

			return nodePath;
	}


	//宛先ノードを返す
	public MapNode getlastNode() {
			MapNode to = pois.selectDestination();
			return to;
	}

	@Override
	public ShortestPathMapBasedMovement replicate() {
		return new ShortestPathMapBasedMovement(this);
	}

}
