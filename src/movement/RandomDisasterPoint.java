/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import core.Coord;
import core.Settings;

import java.util.List;

import movement.map.MapNode;
import movement.map.SimMap;

/**
 * 被災地点ノードを複数個、マップのランダムな位置に生成するMovemetModel 
 */
public class RandomDisasterPoint extends MovementModel {
	/** Per node group setting for setting the location ({@value}) */
	//public static final String LOCATION_S = "nodeLocation";
	private Coord loc; /** The location of the nodes */
	private SimMap Map;
	/**
	 * Setting　Objectをもとにした新しいMovemetModelを生成する
	 * @param s The Settings object where the settings are read from
	 */
	public RandomDisasterPoint(Settings s) {
		super(s);
	}
	
	public RandomDisasterPoint(SimMap ParentMap) {
		this.Map = ParentMap;
		List<MapNode> allNodes = Map.getNodes();
		MapNode Node;
		
	
		Node = allNodes.get(rng.nextInt(allNodes.size()));
		double coords[]= Node.getLocation().Intgetcoords();
		this.loc = new Coord(coords[0],coords[1]);
	}
	
	
	/**
	 * Copy constructor. 
	 * @param sm The StationaryMovement prototype
	 */
	public RandomDisasterPoint (RandomDisasterPoint  sm) {
		super(sm);
		this.loc = sm.loc;
	}
	
	
	
	/**
	 * Returns the only location of this movement model
	 * @return the only location of this movement model
	 */
	@Override
	public Coord getInitialLocation() {
		return loc;
	}
	
	/**
	 * Returns a single coordinate path (using the only possible coordinate)
	 * @return a single coordinate path
	 */
	@Override
	public Path getPath() {
		Path p = new Path(0);
		p.addWaypoint(loc);
		return p;
	}
	
	@Override
	public double nextPathAvailable() {
		return Double.MAX_VALUE;	// no new paths available
	}
	
	@Override
	public RandomDisasterPoint  replicate() {
		return new RandomDisasterPoint (this);
	}
	
	
}
