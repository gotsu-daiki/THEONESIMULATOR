/*
 /*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package core;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import movement.MovementModel;
import movement.Path;
import movement.map.MapNode;
import routing.MessageRouter;
import routing.util.RoutingInfo;
import applications.DataManager;
import core.Coord;
import java.util.Random;
import javax.net.ssl.HostnameVerifier;
import movement.map.SimMap;
import movement.MapBasedMovement;
import core.SimClock;
import gui.DTNSimGUI;
/**
 * A DTN capable host.
 */
public class DTNHost implements Comparable<DTNHost> {
	private static int nextAddress = 0;
	public int address;
//変更済み　private→public　
	
	public Coord initiallocation;//=movement.getInitialLocation(); //ホストの初期位置
	public Coord location; 	// ホストの現在地
	public Coord destination;	// 現在目指しているマップノードの座標（中継地点）
	public Coord LastMapNode;  //ホストの最終目的地
	public MapNode BeforeMapNode;//最後に訪れたマップノード
	public MapNode AnotherPathStartNode;//パス再計算時に向かう最寄りのマップノード
	
	public int PathCount=-1; //被災者が自身のパスの何番目のマップノードまで進んだか確認する
	
 	public List<Coord> DisasterPointlist=new ArrayList<Coord>();//ホストが持っている被災地の位置情報	
	public List<MapNode> AvoidanceNode=new ArrayList<>(); 
	public List<MapNode> PathNodeList=new ArrayList<>();//ホストのパス	
	public List<List<MapNode>> AvoidanceEdge=new ArrayList<>();  //ホストが持つ災害発生エッジ情報
	
	public boolean initiallocationOK=true;
	public boolean DateSendPermisstion=true;
	public boolean MoveActive=true;
	public boolean returning=false;
	public boolean returning2=false;
	public boolean returning3=false;
	
	
	
	private MessageRouter router;
	private MovementModel movement;
	
	private Path path;
	private double speed;
	private double nextTimeToMove;
	//private String name;
	public String name;
	private List<MessageListener> msgListeners;
	private List<MovementListener> movListeners;
	private List<NetworkInterface> net;
	private ModuleCommunicationBus comBus;
	private boolean koritu;
	static {
		DTNSim.registerForReset(DTNHost.class.getCanonicalName());
		reset();
	}
	/**
	 * Creates a new DTNHost.
	 * @param msgLs Message listeners
	 * @param movLs Movement listeners
	 * @param groupId GroupID of this host
	 * @param interf List of NetworkInterfaces for the class
	 * @param comBus Module communication bus object
	 * @param mmProto Prototype of the movement model of this host
	 * @param mRouterProto Prototype of the message router of this host
	 */
	public DTNHost(List<MessageListener> msgLs,
			List<MovementListener> movLs,
			String groupId, List<NetworkInterface> interf,
			ModuleCommunicationBus comBus,
			MovementModel mmProto, MessageRouter mRouterProto) {
		this.comBus = comBus;
		this.location = new Coord(0,0);
		this.address = getNextAddress();
		this.name = groupId+address;
		this.net = new ArrayList<NetworkInterface>();
		for (NetworkInterface i : interf) {
			NetworkInterface ni = i.replicate();
			ni.setHost(this);
			net.add(ni);
		}
		// TODO - think about the names of the interfaces and the nodes
		//this.name = groupId + ((NetworkInterface)net.get(1)).getAddress();
		this.msgListeners = msgLs;
		this.movListeners = movLs;
		// create instances by replicating the prototypes
		this.movement = mmProto.replicate();
		this.movement.setComBus(comBus);
		this.movement.setHost(this);
		setRouter(mRouterProto.replicate());
		this.location = movement.getInitialLocation();
		this.nextTimeToMove = movement.nextPathAvailable();
		this.path = null;
		//追加
		this.LastMapNode=movement.getlastNode().getLocation();
		if (movLs != null) { // inform movement listeners about the location
			for (MovementListener l : movLs) {
				l.initialLocation(this, this.location);
			}
		}
	}
	/**
	 * Returns a new network interface address and increments the address for
	 * subsequent calls.
	 * @return The next address.
	 */
	private synchronized static int getNextAddress() {
		return nextAddress++;
	}
	/**
	 * Reset the host and its interfaces
	 */
	public static void reset() {
		nextAddress = 0;
	}
	/**
	 * Returns true if this node is actively moving (false if not)
	 * @return true if this node is actively moving (false if not)
	 */
	public boolean isMovementActive() {
		return this.movement.isActive();
	}
	/**
	 * Returns true if このノードの無線が有効なら (false if not)
	 * @return true if this node's radio is active (false if not)
	 */
	public boolean isRadioActive() {
		/* TODO: make this work for multiple interfaces */
		return this.getInterface(1).isActive();
		//return false;
	}
	/**
	 * Set a router for this host
	 * @param router The router to set
	 */
	private void setRouter(MessageRouter router) {
		router.init(this, msgListeners);
		this.router = router;
	}
	/**
	 * Returns the router of this host
	 * @return the router of this host
	 */
	public MessageRouter getRouter() {
		return this.router;
	}
	/**
	 * Returns the network-layer address of this host.
	 */
	public int getAddress() {
		return this.address;
	}
	/**
	 * Returns this hosts's ModuleCommunicationBus
	 * @return this hosts's ModuleCommunicationBus
	 */
	public ModuleCommunicationBus getComBus() {
		return this.comBus;
	}
    /**
	 * Informs the router of this host about state change in a connection
	 * object.
	 * @param con  The connection object whose state changed
	 */
	public void connectionUp(Connection con) {
		this.router.changedConnection(con);
	}
	public void connectionDown(Connection con) {
		this.router.changedConnection(con);
	}
	/**
	 * Returns a copy of the list of connections this host has with other hosts
	 * @return a copy of the list of connections this host has with other hosts
	 */
	public List<Connection> getConnections() {
		List<Connection> lc = new ArrayList<Connection>();
		for (NetworkInterface i : net) {
			lc.addAll(i.getConnections());
		}
		return lc;
	}
	/**
	 * Returns the current location of this host.
	 * @return The location
	 */
	public Coord getLocation() {
		return this.location;
	}
	public Coord getLastMapNode() {
		return this.LastMapNode;
	}
	/**
	 * Returns the Path this node is currently traveling or null if no
	 * path is in use at the moment.
	 * @return The path this node is traveling
	 */
	public Path getPath() {
		return this.path;
	}
	/**
	 * Sets the Node's location overriding any location set by movement model
	 * @param location The location to set
	 */
	public void setLocation(Coord location) {
		this.location = location.clone();
	}
	/**
	 * Sets the Node's name overriding the default name (groupId + netAddress)
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns the messages in a collection.
	 * @return Messages in a collection
	 */
	public Collection<Message> getMessageCollection() {
		return this.router.getMessageCollection();
	}
	/**
	 * Returns the number of messages this node is carrying.
	 * @return How many messages the node is carrying currently.
	 */
	public int getNrofMessages() {
		return this.router.getNrofMessages();
	}
	/**
	 * Returns the buffer occupancy percentage. Occupancy is 0 for empty
	 * buffer but can be over 100 if a created message is bigger than buffer
	 * space that could be freed.
	 * @return Buffer occupancy percentage
	 */
	public double getBufferOccupancy() {
		double bSize = router.getBufferSize();
		double freeBuffer = router.getFreeBufferSize();
		return 100*((bSize-freeBuffer)/bSize);
	}
	/**
	 * Returns routing info of this host's router.
	 * @return The routing info.
	 */
	public RoutingInfo getRoutingInfo() {
		return this.router.getRoutingInfo();
	}
	/**
	 * Returns the interface objects of the node
	 */
	public List<NetworkInterface> getInterfaces() {
		return net;
	}
	/**
	 * Find the network interface based on the index
	 */
	public NetworkInterface getInterface(int interfaceNo) {
		NetworkInterface ni = null;
		try {
			ni = net.get(interfaceNo-1);
		} catch (IndexOutOfBoundsException ex) {
			throw new SimError("No such interface: "+interfaceNo +
					" at " + this);
		}
		return ni;
	}
	/**
	 * Find the network interface based on the interfacetype
	 */
	protected NetworkInterface getInterface(String interfacetype) {
		for (NetworkInterface ni : net) {
			if (ni.getInterfaceType().equals(interfacetype)) {
				return ni;
			}
		}
		return null;
	}
	/**
	 * Force a connection event
	 */
	public void forceConnection(DTNHost anotherHost, String interfaceId,
			boolean up) {
		NetworkInterface ni;
		NetworkInterface no;
		if (interfaceId != null) {
			ni = getInterface(interfaceId);
			no = anotherHost.getInterface(interfaceId);
			assert (ni != null) : "Tried to use a nonexisting interfacetype "+interfaceId;
			assert (no != null) : "Tried to use a nonexisting interfacetype "+interfaceId;
		} else {
			ni = getInterface(1);
			no = anotherHost.getInterface(1);
			assert (ni.getInterfaceType().equals(no.getInterfaceType())) :
				"Interface types do not match.  Please specify interface type explicitly";
		}
		if (up) {
			ni.createConnection(no);
		} else {
			ni.destroyConnection(no);
		}
	}
	/**
	 * for tests only --- do not use!!!
	 */
	public void connect(DTNHost h) {
		Debug.p("WARNING: using deprecated DTNHost.connect(DTNHost)" +
		"Use DTNHost.forceConnection(DTNHost,null,true) instead");
		forceConnection(h,null,true);
	}
	/**
	 * Updates node's network layer and router.
	 * @param simulateConnections Should network layer be updated too
	 */
	public void update(boolean simulateConnections,DTNHost host) {
		//System.out.println(this.destination);
		if (!isRadioActive()) {
			// Make sure inactive nodes don't have connections
			tearDownAllConnections();
			return;
		}
		if (simulateConnections) {
			for (NetworkInterface i : net) {
				i.update();
			}
		}
		this.router.update();
	}
	/**
	 * Tears down all connections for this host.
	 */
	private void tearDownAllConnections() {
		for (NetworkInterface i : net) {
			// Get all connections for the interface
			List<Connection> conns = i.getConnections();
			if (conns.size() == 0) continue;
			// Destroy all connections
			List<NetworkInterface> removeList =
				new ArrayList<NetworkInterface>(conns.size());
			for (Connection con : conns) {
				removeList.add(con.getOtherInterface(i));
			}
			for (NetworkInterface inf : removeList) {
				i.destroyConnection(inf);
			}
		}
	}
	/**
	 * Moves the node towards the next waypoint or waits if it is
	 * not time to move yet
	 * @param timeIncrement How long time the node moves
	 */
	//public void move(double timeIncrement) {
	public void move(double timeIncrement,DTNHost host) {
		double possibleMovement;
		double distance;
		double dx, dy;
       
		if (!isMovementActive() || SimClock.getTime() < this.nextTimeToMove) {
			return;
		}
		

		
		
		//初期位置から最終目的地点までのパスを設定
		   if(this.destination == null) {
			  
					if (!setNextWaypoint(host)) {
						return;
					}
			}
		   
		 //初期位置から最終目的地点までのパスを設定
		   if(this.koritu == true) {
			   
			   if (setAnotherNextWaypoint(host.PathNodeList.get(PathCount+1))) {
				   host.location.setLocation(host.destination); 
				   host.koritu=false;
					return;
				}
			}
			
		  
		
		   if(host.returning==true) {
			   
				host.returning=false;
				
				
				
				//①自分のパスに災害発生エッジが含まれていたら→最寄りのマップノードに移動し、パスを再計算
				if(Coord.PathContainsAvoidanceEdge(host.PathNodeList,host.AvoidanceEdge,host)) {
					 
					
					if(PathCount>=0)
						
						
					//既に災害発生エッジに足を踏み入れている→直前のマップノードに移動
						if(Coord.AboveAvoidanceEdge(host.AvoidanceEdge,host)) { 
							 
								host.destination=host.PathNodeList.get(host.PathCount).location;
						}
						
					//直後のマップノードに分岐点がなかったら→直前のマップノードに移動
						else if(host.PathNodeList.get(host.PathCount+1).getNeighbors().size()<3) {						
							if(PathCount>=0) 
								host.destination=host.PathNodeList.get(host.PathCount).location;
						}
							  				
					//直後のマップノードに分岐点がある→次のマップノードまで移動
						else
							host.returning2=true; //今目指している地点を前マップノードとみなす
					
					
				}
		   }
		  
		  
		   
		
		 //②前マップノードに到着→災害地を避けてパス選択
			if(host.returning2)
				if(Coord.CompareIntEqualV3(host.destination, host.location)) {
					 if(host.PathCount!=-1)
					    DTNSimGUI.AllNodeDistance+=host.BeforeMapNode.location.distance(host.destination);
						setAnotherNextWaypoint(host.PathNodeList.get(PathCount+1));//別ルートのNextwayPointを決める

				}
			
			
			 //②前マップノードに到着→災害地を避けてパス選択
			if(host.BeforeMapNode!=null&&Coord.CompareIntEqual(host.destination,host.BeforeMapNode.location)) {
				
				//１つ前のマップノードに戻ったら
				 if(Coord.CompareIntEqualV2(host.BeforeMapNode.location,host.location)){
					 if(host.PathCount!=-1)
					    DTNSimGUI.AllNodeDistance+=host.PathNodeList.get(PathCount).location.distance(host.destination);
						setAnotherNextWaypoint(host.BeforeMapNode);//別ルートのNextwayPointを決める
						
						}
				}
			
			
		
			if(host.koritu==false) {
				
      //destinationに向けてホストを少しずつ動かす
			possibleMovement = timeIncrement * speed;
			distance = host.location.distance(host.destination);
			
			while (possibleMovement >= distance) {
				host.location.setLocation(host.destination); // snap to destination
				possibleMovement -= distance;
				if (!setNextWaypoint(host)) { // get a new waypoint
					return; // no more waypoints left
				}
				distance = host.location.distance(host.destination);
			}
			// move towards the point for possibleMovement amount
			dx = (possibleMovement/distance) * (host.destination.getX() -
					host.location.getX());
			dy = (possibleMovement/distance) * (host.destination.getY() -
					host.location.getY());
			host.location.translate(dx, dy);
			
			}
				
				

			
		//最終目的地点についた被災者は停止
			if(host.LastMapNode!=null) {
				if(Coord.CompareIntEqual(host.location,host.LastMapNode)) {					
				   DataManager.ReachManagement(host);
					host.MoveActive=false;	
				}
			}
	}
	/**
	 * Sets the next destination and speed to correspond the next waypoint
	 * on the path.
	 * @return True if there was a next waypoint to set, false if node still
	 * should wait
	 */
	private boolean setNextWaypoint(DTNHost host) {
		
		if (path == null) {
			path = movement.getPath();
			host.PathNodeList=movement.getPathNodeList();
			//System.out.println(this.PathNodeList=movement.getPathNodeList());
			
			//パスの第一マップノードを初期位置として登録
			if(host.PathNodeList!=null)
			host.initiallocation=host.PathNodeList.get(0).location;
			
		
		}
		//System.out.println("パス"+path);
		if (path == null || !path.hasNext()) {
			host.nextTimeToMove = movement.nextPathAvailable();
			host.path = null;
			return false;
		}
		//最後に通ったマップノードを保持する
		if(host.destination!=null) {
			//新しいマップノードに到着したら、最後に通ったマップノードを更新する
			if(Coord.CompareIntEqual(host.location,host.destination)) {
				
				 if(host.PathCount!=-1)
				  DTNSimGUI.AllNodeDistance+=host.PathNodeList.get(PathCount).location.distance(host.destination);
			     host.PathCount++;
			     host.BeforeMapNode=host.PathNodeList.get(PathCount);
			     
			    	 
			     
				   // System.out.println(this+"は分岐点"+this.destination+"に到着、前分岐点情報を更新"+this.Beforedestination);
			//	 System.out.println(this+"のパスノードリスト"+this.PathNodeList.get(PathCount)+""+this.PathCount);
			     }
		}
		
		
		 
		host.destination = path.getNextWaypoint();

		if(host.PathCount>=0&&host.address==418) {
			System.out.println("325が到着マップノードの隣接ノード数"+this.PathNodeList.get(PathCount).getNeighbors().size());
			}
		host.speed = path.getSpeed();
		//System.out.println("目的地"+this.destination);
		if (host.movListeners != null) {
			for (MovementListener l : host.movListeners) {
				l.newDestination(host, host.destination, host.speed);
			}
		}
		return true;
	}
	
	
	
	
	
	
	
	private boolean setAnotherNextWaypoint(MapNode BeforeMapNode){
		path = movement.getPathAnotherRout(BeforeMapNode);
		if(movement.getAnotherPathNodeList(BeforeMapNode).size()!=0)
			this.PathNodeList=movement.getAnotherPathNodeList(BeforeMapNode);
		//System.out.println(this+"   "+this.PathNodeList.size()+"   交わすノード"+this.AvoidanceNode);
		//System.out.println(this.path.getCoords().size()==0);

		

		//災害地を交わしたルートがない場合は停止
		if(this.path.getCoords().size()==0) {
			this.path=null;
			this.koritu=true;
			if(this.address==489) {
				System.out.println(this.PathNodeList);
				System.out.println(this.location);
				System.out.println(this.BeforeMapNode+""+this.PathNodeList.get(PathCount+1));
				}
			
			return true;
			
		}
		this.PathCount=-1;
		
		this.destination = path.getNextWaypoint();
		this.speed = path.getSpeed();
         
		if (this.movListeners != null) {
			for (MovementListener l : this.movListeners) {
				l.newDestination(this, this.destination, this.speed);
			}
		}
		return true;
	}
	/**
	 * Sends a message from this host to another host
	 * @param id Identifier of the message
	 * @param to Host the message should be sent to
	 */
	public void sendMessage(String id, DTNHost to) {
		this.router.sendMessage(id, to);
	}
	/**
	 * Start receiving a message from another host
	 * @param m The message
	 * @param from Who the message is from
	 * @return The value returned by
	 * {@link MessageRouter#receiveMessage(Message, DTNHost)}
	 */
	public int receiveMessage(Message m, DTNHost from) {
		int retVal = this.router.receiveMessage(m, from);
		if (retVal == MessageRouter.RCV_OK) {
			m.addNodeOnPath(this);	// add this node on the messages path
		}
		return retVal;
	}
	/**
	 * Requests for deliverable message from this host to be sent trough a
	 * connection.
	 * @param con The connection to send the messages trough
	 * @return True if this host started a transfer, false if not
	 */
	public boolean requestDeliverableMessages(Connection con) {
		return this.router.requestDeliverableMessages(con);
	}
	/**
	 * Informs the host that a message was successfully transferred.
	 * @param id Identifier of the message
	 * @param from From who the message was from
	 */
	public void messageTransferred(String id, DTNHost from) {
		this.router.messageTransferred(id, from);
	}
	/**
	 * Informs the host that a message transfer was aborted.
	 * @param id Identifier of the message
	 * @param from From who the message was from
	 * @param bytesRemaining Nrof bytes that were left before the transfer
	 * would have been ready; or -1 if the number of bytes is not known
	 */
	public void messageAborted(String id, DTNHost from, int bytesRemaining) {
		this.router.messageAborted(id, from, bytesRemaining);
	}
	/**
	 * Creates a new message to this host's router
	 * @param m The message to create
	 */
	public void createNewMessage(Message m) {
		this.router.createNewMessage(m);
	}
	/**
	 * Deletes a message from this host
	 * @param id Identifier of the message
	 * @param drop True if the message is deleted because of "dropping"
	 * (e.g. buffer is full) or false if it was deleted for some other reason
	 * (e.g. the message got delivered to final destination). This effects the
	 * way the removing is reported to the message listeners.
	 */
	public void deleteMessage(String id, boolean drop) {
		this.router.deleteMessage(id, drop);
	}
	/**
	 * Returns a string presentation of the host.
	 * @return Host's name
	 */
	public String toString() {
		return name;
	}
	/**
	 * Checks if a host is the same as this host by comparing the object
	 * reference
	 * @param otherHost The other host
	 * @return True if the hosts objects are the same object
	 */
	public boolean equals(DTNHost otherHost) {
		return this == otherHost;
	}
	/**
	 * Compares two DTNHosts by their addresses.
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(DTNHost h) {
		return this.getAddress() - h.getAddress();
	}
	
	
}