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
public class VectorRouterprottype extends ActiveRouter {
	
	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public VectorRouterprottype(Settings s) {
		super(s);
		//TODO: read&use epidemic router specific settings (if any)
	}
	
	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected VectorRouterprottype(VectorRouterprottype r) {
		super(r);
		//TODO: copy epidemic settings here (if any)
	}
			
	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; // メッセージを転送中もしくはまだ他のホストと接続がない
		}
		
		// 今、接続しているホストの中に最終宛先ののーどが含まれている可能性がある場合
		if (exchangeDeliverableMessages() != null) {
			return; // 転送を開始、ほかのノードにはまだ転送しない
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
	
		//hostの持っているメッセージを集める
		Collection<Message> msgCollection = getMessageCollection();
		
		//接続1つ1つに注目して、見ていく
		for (Connection con : getConnections()) {
			DTNHost other = con.getOtherNode(getHost());//getHost():自分　other:相手
			ProphetV2Router othRouter = (ProphetV2Router)other.getRouter();//相手側のルーター
			
			if (othRouter.isTransferring()) {
				continue; // 通信相手のルーターが他のノードに転送中ならスキップ
			}
			
			//メッセージ1つ1つに注目してみていく
			for (Message m : msgCollection) {
				if (othRouter.hasMessage(m.getId())) {
					continue; // 通信相手がメッセージmをすでに持っていたらこのmをスキップする
				}
				
				//mの災害地方向性と接続先の移動方向性が一致すれば、messagesにmを加える
				if(VectorComParator(DisasterVector(getHost(),m),MovementVector(other)))		
				{
					messages.add(new Tuple<Message, Connection>(m,con));
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
	public VectorRouterprottype replicate() {
		return new VectorRouterprottype(this);
	}
	/**
	 * 災害地方向性と移動方向性を比較する
	 * @param vecA
	 * @param vecB
	 * @return　移動方向性が災害地方向性の±5°以内ならtrue,そうでなければfalse
	 */
	protected boolean VectorComParator(double vecA,double vecB) {
	
		if((vecA+5.0)>=vecB&&(vecA-5.0)<=vecB) {
				return true;
		}	 
	 return false;
	}
	
	
	/**
	 * メッセージの災害地方向性を算出する
	 * @param host　
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
	 * @param host　
	 * @return 角度［°］
	 */
	protected double MovementVector(DTNHost host) {
		
		double x1=host.initiallocation.getX(),
				y1=host.initiallocation.getY(),
		 		x2=host.location.getX(),
				y2=host.location.getY();
						
		return Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
	}

}

