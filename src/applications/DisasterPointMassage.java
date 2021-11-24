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
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
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

	/** Application ID */
	public static final String APP_ID = "gototest";

	// Private vars
	private double	lastPing = 0;
	private double	interval = 10;
	private boolean passive = false;
	private int		seed = 9;
	private int		destMin=0;
	public static int		destMax=1;
	private int		pingSize=1;
	private int		pongSize=1;
	private Random	rng;
	private int i=0;
	//private List<String> sharenode =new ArrayList<String>();
	private List<String> data =new ArrayList<>();


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

	//受け取ったデータの中にある被災地の位置情報をホストは取得する
		Coord type = (Coord)msg.getProperty("DisasterCoord");
		
	   host.DisasterPoint=type;
		//host.DisasterPoint2.add(type);
		
		//  System.out.println(host+"は"+type+"を取得");
		
		
	//どこからどんなデータを受け取ったか通知
		//System.out.print("目的ノード:"+msg.getTo()+" 受信ノード:"+host+
				// "  時間:"+SimClock.getIntTime());

	   if(!host.toString().contains("d")) {
		DataManager.Manager(host);
	   }
		
		
		if (type==null)
				return msg; // Not a ping/pong message
		
		// Respond with pong if we're the recipient
		//if (msg.getTo()==host && type.equalsIgnoreCase("disaster")) {

			//String id = "pong" + SimClock.getIntTime() + "-" +
			//	host.getAddress();
			//Message m = new Message(host, msg.getFrom(), id, getPongSize());
			//m.addProperty("type", "pong");

		//	System.out.println(host+"は"+msg.getFrom()+"からgototestを受信。　 時間 :"+
		//	+SimClock.getIntTime()+" データのサイズ :"+msg.size+
			//"  スループット:"+(double)msg.size/(double)(SimClock.getIntTime()-interval)+"(kbps)");
			
		
		return msg;
	}

	/**
	 * Draws a random host from the destination range
	 *
	 * @return host
	 */
	private DTNHost randomHost() {
		int destaddr = 0;
		if (destMax == destMin) {
			destaddr = destMin;
		}
		destaddr = destMin + rng.nextInt(destMax - destMin);
		World w = SimScenario.getInstance().getWorld();

		return w.getNodeByAddress(20);
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
		/*if (curTime - this.lastPing >= this.interval) {
				//データを送信準備のメソッド
				this.DataSend(host);

			// リスナに知らせる
			super.sendEventToListeners("SentPing", null, host);
			this.lastPing = curTime;*/
		 
			if (curTime - this.lastPing >= this.interval&&host.DateSendPermisstion==true) {
					//データを送信準備のメソッド
					this.DataSend(host);

				// リスナに知らせる
				super.sendEventToListeners("SentPing", null, host);
				this.lastPing = curTime;
				host.DateSendPermisstion=false;
		}
	}

	
public void DataSend(DTNHost host) {

  //ソースホストを被災地ノードに限定する
		if(host.address>=1){
				Message m = new Message(host,randomHost(),"disaster"+host.address,getPingSize());
				m.addProperty("DisasterCoord", host.location);;
				m.setAppID(APP_ID);
		  
		  
		//メッセージ送信
				host.createNewMessage(m);
		  
		  
       //  System.out.println(host+"はデータを"+m.getTo()+"あてに送りました。時間は:"+SimClock.getIntTime()+"秒"+
			//	"　データの大きさ："+m.size+"kbytes");

		  
		//リスナに知らせる
				super.sendEventToListeners("SentPing", null, host);

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
