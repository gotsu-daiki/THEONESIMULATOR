/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import core.Connection;
import core.Coord;
import core.DTNHost;
import core.Message;
import core.Settings;

import util.Tuple;

/**
 * Epidemic message router with drop-oldest buffer and only single transferring
 * connections at a time.
 */
public class VectorRouterProtoType extends ActiveRouter {
	
	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public VectorRouterProtoType(Settings s) {
		super(s);
		//TODO: read&use epidemic router specific settings (if any)
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected VectorRouterProtoType(VectorRouterProtoType r) {
		super(r);
		//TODO: copy epidemic settings here (if any)
	}
			
	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; // メッセージを転送中もしくはまだ他のホストと接続がない
		}
		
		// Try first the messages that can be delivered to final recipient
		if (exchangeDeliverableMessages() != null) {
			return; // started a transfer, don't try others (yet)
		}
		
		tryOtherMessages();
	}
	
	/**
	 * 方向性が一致するメッセージのみを転送の準備を行うメソット
	 * @return
	 */
	private Tuple<Message, Connection> tryOtherMessages() {
		List<Tuple<Message, Connection>> messages = 
			new ArrayList<Tuple<Message, Connection>>(); 
	
		Collection<Message> msgCollection = getMessageCollection();
		

		for (Connection con : getConnections()) {
			DTNHost host=getHost();
			DTNHost other = con.getOtherNode(host);
			VectorRouterProtoType othRouter = (VectorRouterProtoType)other.getRouter();
			
			//通信相手がメッセージ転送中ならスキップ
			if (othRouter.isTransferring()) 
				continue; 
			
			//ホストの保持メッセージでTTLが0以下のものは削除→ふつうにめっせーじるーたーのTTLCHECKあるかた必要ないかもしれへん
			//dropExpiredMessages();
			
			for (Message m : msgCollection) {
				// 通信相手がメッセージmをすでに持っていたらこのmをスキップする
				if (othRouter.hasMessage(m.getId())) {
					continue; 
				}
				
				//System.out.println(m.getId()+"    "+m.getTtl());
				
				//災害地からは無条件で転送			
				if(host.name.contains("d-")) {
					messages.add(new Tuple<Message, Connection>(m,con));
				}
				//被災者ノードは自分の位置情報のみを被災者ノードにのみ転送
				if(m.getId().contains("location"+host.address)&&!other.name.contains("d-")) {
					//messages.add(new Tuple<Message, Connection>(m,con));
				}
				
				//通信相手が被災者&自分が災害地情報を転送する?
				if(other.name.contains("p")&&other.StartPoint!=null&&m.getId().contains("disaster")) {	
				
				 // if(VectorComParator(DisasterVector(host,m),MovementVector(other)))
						messages.add(new Tuple<Message, Connection>(m,con));
						
				   
				   
				   
				   
				   
				   
					/*if(host.getRouter().messages.get("location"+other.address)!=null) {
						
						//自分のメッセージリストから相手の位置情報を取り出す
						Message msg=host.getRouter().messages.get("location"+other.address);
						List<Coord> MovementVectorlist = (List<Coord>)msg.getProperty("MovementVector");
					
						if(MovementVectorlist.get(0)!=null)
						if(VectorComParator(DisasterVector(getHost(),m),MovementVector2(MovementVectorlist))){
							//mの災害地方向性と接続先の移動方向性が一致すれば、messagesにmを加える→提案手法！！
								messages.add(new Tuple<Message, Connection>(m,con));
						}
					}*/
				}
			}		
		}			
		
		//転送するメッセージがなければnull
		if (messages.size() == 0) {
			return null;
		}
		
		//送る用メッセージリストmessagesに加えられたメッセージを送信
		return tryMessagesForConnected(messages);	// try to send messages
	}
	
	
	
	@Override
	public VectorRouterProtoType replicate() {
		return new VectorRouterProtoType(this);
	}
	/**
	 * 災害地方向性と移動方向性を比較する
	 * @param vecA
	 * @param vecB
	 * @return　移動方向性が災害地方向性の±22.5°以内ならtrue,そうでなければfalse
	 */
	protected boolean VectorComParator(double vecA,double vecB) {
	
		if((vecA+2.5)>=vecB&&(vecA-2.5)<=vecB) {
				return true;
		}	 
	 return false;
	}
	
	
	/**
	 * メッセージの災害地方向性を算出する
	 * @param host ホスト　
	 * @param m　災害地情報
	 * @return 角度［°］
	 */
	protected double DisasterVector(DTNHost host,Message m) {
		Coord Disaster = (Coord)m.getProperty("DisasterCoord");
		double x1=host.location.getX(),
				y1=host.location.getY(),
				x2=Disaster.getX(),
				y2=Disaster.getY();
		
		return Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
	}
	
	/**
	 * ホストの移動方向性を算出する
	 * @param host ホスト
	 * @return 角度［°］
	 */
	protected double MovementVector(DTNHost host) {
		
		//double x1=host.initiallocation.getX(),
				//y1=host.initiallocation.getY(),
				//x1=host.PathNodeList.get(host.PathCount-1).location.getX(),
				//y1=host.PathNodeList.get(host.PathCount-1).location.getY(),
			double x1=host.StartPoint.getX(),
					y1=host.StartPoint.getY(),
					x2=host.location.getX(),
		 			y2=host.location.getY();
						
		return Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
	}
	/**
	 * ホストの移動方向性を算出する
	 * @param host ホスト
	 * @return 角度［°］
	 */
	protected double MovementVector2(List<Coord> list) {
		
			double x1=list.get(0).getX(),
					y1=list.get(0).getY(),
					x2=list.get(1).getX(),
		 			y2=list.get(1).getY();
						
		return Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
	}

}

