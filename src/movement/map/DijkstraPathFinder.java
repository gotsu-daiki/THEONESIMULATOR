/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement.map;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import core.Coord;
import core.DTNHost;

/**
 * Implementation of the Dijkstra's shortest path algorithm.
 */
public class DijkstraPathFinder {
	/** Value for infinite distance  */
	private static final Double INFINITY = Double.MAX_VALUE;
	/** 優先キューのサイズの初期値 */
	private static final int PQ_INIT_SIZE = 11;

	/** ソースノードからマップノードまでの距離*/
	private DistanceMap distances;
	/** 既に訪れたノードのセット(where the shortest path is known) */
	private Set<MapNode> visited;
	/** Priority queue of unvisited nodes discovered so far */
	private Queue<MapNode> unvisited;
	/** Map of previous nodes on the shortest path(s) */
	private Map<MapNode, MapNode> prevNodes;

	private int [] okMapNodes;
	
	/**
	 * Constructor.
	 * @param okMapNodes The map node types that are OK for paths or null if
	 * all nodes are OK
	 */
	public DijkstraPathFinder(int [] okMapNodes) {
		super();
		this.okMapNodes = okMapNodes;
	}

	/**
	 *
	 * Initializes a new search with a source node
	 * ソースノードを用いて新しい探索を初期化する
	 * @param node The path's source node（そのパスのソースノード）
	 */
	private void initWith(MapNode node) {
		assert (okMapNodes != null ? node.isType(okMapNodes) : true);
		
		// 新しいデータ構造を作成する
		this.unvisited = new PriorityQueue<MapNode>(PQ_INIT_SIZE, 
				new DistanceComparator());
		this.visited = new HashSet<MapNode>();
		this.prevNodes = new HashMap<MapNode, MapNode>();
		this.distances = new DistanceMap();
		
		//ソースを基準とした距離をセットし、unvisited キューを初期化する
		this.distances.put(node, 0);
		this.unvisited.add(node);
	}
	
	/**
	 * 2つのマップノード間の最短経路を見つけて、返す
	 * @param from The source of the path
	 * @param to The destination of the path
	 * @return a shortest path between the source and destination nodes in
	 * a list of MapNodes or an empty list if such path is not available
	 */
	public List<MapNode> getShortestPath(MapNode from, MapNode to) {
		List<MapNode> path = new LinkedList<MapNode>();
		
		if (from.compareTo(to) == 0) { // 出発地点と目的地店が同じであったらば
			path.add(from); // 出発地点のみを含んだリストを返却する
			return path;
		}
		
		initWith(from);
		MapNode node = null;
		
		// 毎回最短距離を持つノードを選択する
		while ((node = unvisited.poll()) != null) {
			if (node == to) {
				break; // we found the destination -> no need to search further
			}
			
			visited.add(node); //  visitedとしてノードをマークする
			relax(node); // add/update neighbor nodes' distances
		}
		
		// now we either have the path or such path wasn't available
		//パスをもっているもしくはそのパスは利用することが出来なかった場合
		//最終目的ノードから１つ前へ１つ前へとノードをたどりパスを作る
		if (node == to) { // found a path
			path.add(0,to); 
			MapNode prev = prevNodes.get(to); 
			while (prev != from) { 
				path.add(0, prev);	// always put previous node to beginning
				prev = prevNodes.get(prev);
			}
			
			path.add(0, from); // finally put the source node to first node
		}
		
		return path;
	}
	
	
	public List<MapNode> getShortestPath(MapNode from, MapNode to,DTNHost host) {
		List<MapNode> path = new LinkedList<MapNode>();
		
		if (from.compareTo(to) == 0) { // 出発地点と目的地店が同じであったらば
			path.add(from); // 出発地点のみを含んだリストを返却する
			return path;
		}
		
		initWith(from);
		MapNode node = null;
		
		// 毎回最短距離を持つノードを選択する
		while ((node = unvisited.poll()) != null) {
			if (node == to) {
				break; // we found the destination -> no need to search further
			}
			
			visited.add(node); //  visitedとしてノードをマークする
			relax(node,host); // add/update neighbor nodes' distances
		}
		
		// now we either have the path or such path wasn't available
		//パスをもっているもしくはそのパスは利用することが出来なかった場合
		//最終目的ノードから１つ前へ１つ前へとノードをたどりパスを作る
		if (node == to) { // found a path
			path.add(0,to); 
			MapNode prev = prevNodes.get(to); 
			while (prev != from) { 
				path.add(0, prev);	// always put previous node to beginning
				prev = prevNodes.get(prev);
			}
			
			path.add(0, from); // finally put the source node to first node
		}
		
		return path;
	}
	/**
	 * Relaxes the neighbors of a node (updates the shortest distances).
	 * @param node The node whose neighbors are relaxed
	 */
	private void relax(MapNode node) {
		double nodeDist = distances.get(node);	//nodeまでの距離を出力
		for (MapNode n : node.getNeighbors()) {
			if (visited.contains(n)) {
				continue; // 一度訪れたノードはスキップする
			}
			
			if (okMapNodes != null && !n.isType(okMapNodes)) {
				continue; // OKノードすなわち通ることのできないノードはスキップ
			}
			
			// パスのソースノードからnノードのまでの距離
			double nDist = nodeDist + getDistance(node, n);
			
			if (distances.get(n) > nDist) { // stored distance > found dist?　蓄積している距離より新しく見つけた距離のほうがみじかければ 
				prevNodes.put(n, node);		//nノードの前のマップノードにnodeを引っ付ける
				setDistance(n, nDist);
			}
		}
	}
	
	private void relax(MapNode node,DTNHost host) {
		double nodeDist = distances.get(node);	//nodeまでの距離を出力
		for (MapNode n : node.getNeighbors()) {
			if (visited.contains(n)) {
				continue; // 一度訪れたノードはスキップする
			}
			
			if (okMapNodes != null && !n.isType(okMapNodes)) {
				continue; // OKノードすなわち通ることのできないノードはスキップ
			}
			
			
			
			// パスのソースノードからnノードのまでの距離
			double nDist = nodeDist + getDistance(node, n);
			
			if (distances.get(n) > nDist) { // stored distance > found dist?　蓄積している距離より新しく見つけた距離のほうがみじかければ 
	
				//nが災害地を踏むエッジの目的地マップノードであったらば距離無限
				
				 if(host.AvoidanceNode!=null&&Coord.CompareEqual(n.getLocation(),host.AvoidanceNode.getLocation() )){
				    setDistance(n,INFINITY);
		
				    }
				 else {	
					 prevNodes.put(n, node);	//nノードの前のマップノードにnodeを引っ付ける
					 setDistance(n, nDist);
			   }
			}
		}
	}
	
	/**
	 * Sets the distance from source node to a node
	 * @param n The node whose distance is set
	 * @param distance The distance of the node from the source node
	 */
	private void setDistance(MapNode n, double distance) {
		unvisited.remove(n); // remove node from old place in the queue
		distances.put(n, distance); // update distance
		unvisited.add(n); // insert node to the new place in the queue
	}
	

	
	
	/**
	 * Returns the (euclidean) distance between the two map nodes
	 * @param from The first node
	 * @param to The second node
	 * @return Euclidean distance between the two map nodes
	 */
	private double getDistance(MapNode from, MapNode to) {
		return from.getLocation().distance(to.getLocation());
	}
	
	/**
	 * Comparator that compares two map nodes by their distance from
	 * the source node.
	 */
	private class DistanceComparator implements Comparator<MapNode> {
		
		/**
		 * Compares two map nodes by their distance from the source node
		 * @return -1, 0 or 1 if node1's distance is smaller, equal to, or
		 * bigger than node2's distance
		 */
		public int compare(MapNode node1, MapNode node2) {
			double dist1 = distances.get(node1);
			double dist2 = distances.get(node2);
			
			if (dist1 > dist2) {
				return 1;
			}
			else if (dist1 < dist2) {
				return -1;
			}
			else {
				return node1.compareTo(node2);
			}
		}
	}
	
	/**
	 * Simple Map implementation for storing distances. 
	 */
	private class DistanceMap {
		private HashMap<MapNode, Double> map;
		
		/**
		 * Constructor. Creates an empty distance map
		 */
		public DistanceMap() {
			this.map = new HashMap<MapNode, Double>(); 
		}
		
		/**
		 * Returns the distance to a node. If no distance value
		 * is found, returns {@link DijkstraPathFinder#INFINITY} as the value.
		 * @param node The node whose distance is requested
		 * @return The distance to that node
		 */
		public double get(MapNode node) {
			Double value = map.get(node);
			if (value != null) {
				return value;
			}
			else {
				return INFINITY;
			}
		}
		
		/**
		 * Puts a new distance value for a map node
		 * @param node The node
		 * @param distance Distance to that node
		 */
		public void  put(MapNode node, double distance) {
			map.put(node, distance);
		}
		
		/**
		 * Returns a string representation of the map's contents
		 * @return a string representation of the map's contents
		 */
		public String toString() {
			return map.toString();
		}
	}
}