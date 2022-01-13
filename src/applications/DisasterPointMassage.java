/*
* Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
/*
*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */

package applications;

import java.util.Random;

import report.PingAppReporter;
import core.Application;
import core.Coord;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import core.World;
import movement.map.MapNode;
import routing.MessageRouter;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Simple ping application to demonstrate the application support. The
 * application can be configured to send pings with a fixed interval or to only
 * answer to pings it receives. When the application receives a ping it sends
 * a pong message in response.
 *
 * The corresponding <code>PingAppReporter</code> class can be used to record
 * information about the application behavior.
 *
 * @see PingAppReporter
 * @author teemuk
 */
public class DisasterPointMassage extends Application {
	/** Run in passive mode - don't generate pings but respond */
	public static final String PING_PASSIVE = "passive";
	/** Ping generation interval */
	public static final String PING_INTERVAL = "interval";
	
	public static final String VECTOR_INTERVAL="vevtorinterval";
	/** Ping interval offset - avoids synchronization of ping sending */
	public static final String PING_OFFSET = "offset";
	/** Destination address range - inclusive lower, exclusive upper */
	public static final String PING_DEST_RANGE = "destinationRange";
	/** Seed for the app's random number generator */
	public static final String PING_SEED = "seed";
	/** Size of the ping message */
	public static final String PING_PING_SIZE = "pingSize";
	/** Size of the pong message */
	public static final String PING_PONG_SIZE = "pongSize";
	
	public static final String NORFHOSTS = "norfhosts";

	/** Application ID */
	public static final String APP_ID = "gototest";
	

	// Private vars
	private double	lastPing = 0;
	private double	interval = 1;
	private boolean passive = false;
	private int		seed = 9;
	private int		destMin=0;
	public static int		destMax=1;
	private int		pingSize=1;
	private int		pongSize=1;
	public static int Host=1;
	private Random	rng;
	private int i=0;
	public int vectorinterval=60;
	//private List<String> sharenode =new ArrayList<String>();
	public List<MapNode> type2=new ArrayList<>();
	
	
	


	//private double share;

   // private int share;
	/**
	 * Creates a new ping application with the given settings.
	 *
	 * @param s	Settings to use for initializing the application.
	 */
	public DisasterPointMassage(Settings s) {
		if (s.contains(PING_PASSIVE)){
			this.passive = s.getBoolean(PING_PASSIVE);
		}
		if (s.contains(PING_INTERVAL)){
			this.interval = s.getDouble(PING_INTERVAL);
		}
		if (s.contains(PING_OFFSET)){
			this.lastPing = s.getDouble(PING_OFFSET);
		}
		if (s.contains(PING_SEED)){
			this.seed = s.getInt(PING_SEED);
		}
		if (s.contains(PING_PING_SIZE)) {
			this.pingSize = s.getInt(PING_PING_SIZE);
		}
		if (s.contains(PING_PONG_SIZE)) {
			this.pongSize = s.getInt(PING_PONG_SIZE);
		}
		if (s.contains(PING_DEST_RANGE)){
			int[] destination = s.getCsvInts(PING_DEST_RANGE,2);
			this.destMin = destination[0];
			this.destMax = destination[1];
		}
		if (s.contains(NORFHOSTS)){
			this.Host = s.getInt(NORFHOSTS);
		}
		if(s.contains(VECTOR_INTERVAL)) {
			this.vectorinterval=s.getInt(VECTOR_INTERVAL);
		}

		rng = new Random(this.seed);
		super.setAppID(APP_ID);
	}

	/**
	 * Copy-constructor
	 *
	 * @param a
	 */
	public DisasterPointMassage(DisasterPointMassage a) {
		super(a);
	
		this.seed = a.getSeed();
		this.pongSize = a.getPongSize();
		this.pingSize = a.getPingSize();
		this.rng = new Random(this.seed);

	}

	
	
	/**
	 * Handles an incoming message. If the message is a ping message replies
	 * with a pong message. Generates events for ping and pong messages.
	 *メッセージを受け取ったノードの対応
	 * @param msg	message received by the router
	 * @param host	host to which the application instance is attached
	 * 
	 */
	@Override
	public Message handle(Message msg, DTNHost host) {
	 
	 
		if(msg.getId().contains("location"))
			System.out.println(msg.getTtl());
	//受け取ったデータの中にある被災地の位置情報をホストは取得する
	Coord type = (Coord)msg.getProperty("DisasterCoord");
  //  host.DisasterPointlist.add(type);
	
    
    
	if(msg.getId().contains("disaster508"))
	{
		System.out.println(host);
	}
	//メッセージの回避エッジリストをホストが所持する回避エッジリストに追加していく
  // if(msg.path.get(msg.path.size()-2).address>=500) {
	List<List<MapNode>> list = (List<List<MapNode>>)msg.getProperty("AVOID");
    
	if(list!=null) {
		int length=list.size();
		
		for(int i=0;i<length;i++) {
			if(!host.AvoidanceEdge.contains(list.get(i)))
				host.AvoidanceEdge.add(list.get(i));
		}			
	}

	
   
		
	
//災害地からデータを受け取った時messageにエッジ情報を乗せる
	if(msg.path.get(msg.path.size()-2).name.contains("d")) {
		
		List<MapNode> AvoidanceNode1=new ArrayList<>();
		List<MapNode> AvoidanceNode2=new ArrayList<>();
	
		
		if(host.PathCount>=0) {
			//災害が分岐点に発生していた時
			if(host.PathNodeList.size()-host.PathCount>=2) {
				//if(Coord.containsIntlocation(host.DisasterPointlist, host.PathNodeList.get(host.PathCount+1).location)) {
					
					//AvoidanceNode1.add(host.PathNodeList.get(host.PathCount+1));
					//AvoidanceNode1.add(host.PathNodeList.get(host.PathCount+2));
					AvoidanceNode1.add(host.PathNodeList.get(host.PathCount));
					AvoidanceNode1.add(host.PathNodeList.get(host.PathCount+1));
				
			//avoidanceNode2で避けるエッジを複数個所持できるようにする2				
					host.AvoidanceEdge.add(AvoidanceNode1);
					//msg.updateProperty("AVOID", host.AvoidanceEdge);
					
					
			
				}
				else{
				//avoidanceNode1で１つのエッジ→マップノードのペアを作る
					AvoidanceNode2.add(host.PathNodeList.get(host.PathCount));
					AvoidanceNode2.add(host.PathNodeList.get(host.PathCount+1));
				
				//avoidanceNode2で避けるエッジを複数個所持できるようにする
					host.AvoidanceEdge.add(AvoidanceNode2);
					
			
					//if(host.address==325)
					//System.out.println(host.AvoidanceEdge);
				}
		}
			
	//メッセージが持つ回避エッジ情報を更新
	msg.updateProperty("AVOID", host.AvoidanceEdge);
	}
	
	
	
	//被災者ノードから情報を受け取ったノードは自分のルートに回避ノードが存在するか確かめる	
			host.returning=true;

		if (type==null)
				return msg; // Not a ping/pong message

			
		
		
   return msg;
	}
	

	/**
	 * Draws a random host from the destination range
	 *
	 * @return host
	 */
	private DTNHost randomHost() {
		World w = SimScenario.getInstance().getWorld();
		return w.getNodeByAddress(100);
	}

	@Override
	public Application replicate() {
		return new DisasterPointMassage(this);
	}

	/**
	 * Sends a ping packet if this is an active application instance.
	 *
	 * @param host to which the application instance is attached
	 */
	@Override
	public void update(DTNHost host) {
		if (this.passive) return;

		 double curTime = SimClock.getTime();
		
		 
			if(host.DateSendPermisstion==true||curTime - this.lastPing >= this.vectorinterval&&host.name.contains("p")) {
				
				host.DateSendPermisstion=false;
				
				//データを送信準備のメソッド
					this.DataSend(host);

				// リスナに知らせる
				super.sendEventToListeners("SentPing", null, host);
				//メッセージを最後に送った時間
				
				if(host.name.contains("p"))
				 this.lastPing = curTime;

		}
	}

	
public void DataSend(DTNHost host) {

  //災害地が送るメッセージ
		if(host.name.contains("d")){
				Message m = new Message(host,randomHost(),"disaster"+host.address/*+SimClock.getTime()*/,getPingSize());
				m.addProperty("DisasterCoord", host.location);
		//回避エッジ専用オブジェクト		
				m.addProperty("AVOID", null);
				m.setAppID(APP_ID);
				m.setTtl(100);
		//メッセージ送信
				host.createNewMessage(m);
		//リスナに知らせる
				super.sendEventToListeners("Disaster", null, host);

		}
		
	//被災者が送るメッセージ：自身の移動方向性ベクトル(始点と終点)を載せたメッセージを常に所持している
		if(host.name.contains("p")) {
			
			List<Coord> MovementVector=new ArrayList<>();
			MovementVector.add(host.StartPoint);
			MovementVector.add(host.location);
			
			Message m2 = new Message(host,randomHost(),"location"+host.address,getPingSize());
			m2.addProperty("MovementVector",MovementVector);
			m2.setTtl(0);//TTLを1秒もしくは1ホップに設定
			m2.setAppID(APP_ID);
			host.createNewMessage(m2);
			
			super.sendEventToListeners("location", null, host);
		}

	}
	/**
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return the pongSize
	 */
	public int getPongSize() {
		return pongSize;
	}

	/**
	 * @param pongSize the pongSize to set
	 */
	public void setPongSize(int pongSize) {
		this.pongSize = pongSize;
	}

	/**
	 * @return the pingSize
	 */
	public int getPingSize() {
		return pingSize;
	}

	/**
	 * @param pingSize the pingSize to set
	 */
	public void setPingSize(int pingSize) {
		this.pingSize = pingSize;

	}

	
	/**
	 * データの大きさ(bytes)を計算する
	 *
	 * @param String配列
	 * @return　バイト数
	 */
	public int getIntByte(List<String> data) {

		int s=0;
	//String配列をStringに変換し、後に","を削除。
		String str=String.join(",", data).replace(",", "");
		//一文字につき、８bit加算（文字分類機能なし）
		for(int n=1;n<=str.length();n++)
			s+=8;
		
		return s;
	}

}
