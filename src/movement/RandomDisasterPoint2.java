/*
* Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import movement.map.DijkstraPathFinder;
import movement.map.PointsOfInterest;
import core.Settings;
import java.util.List;
/**
 * 被災地点ノードを複数個、マップのランダムな位置に生成するMovemetModel 
 */
public class RandomDisasterPoint2 extends MapBasedMovement implements 
	SwitchableMovement {
	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;

	/** Points Of Interest handler */
	private PointsOfInterest pois;
	
	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param settings The Settings object where the settings are read from
	 */
	public RandomDisasterPoint2(Settings settings) {
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
	protected RandomDisasterPoint2(RandomDisasterPoint2 mbm) {
		super(mbm);
		this.pathFinder = mbm.pathFinder;
		this.pois = mbm.pois;
	}
	
	/**新しい行き先を決めるメソッド
	 * 初期位置から動かない
	 */
	@Override
	public Path getPath() {
		Path p = new Path(0);
		return p;
	}	
	
	@Override
	public RandomDisasterPoint2 replicate() {
		return new RandomDisasterPoint2(this);
	}

}
