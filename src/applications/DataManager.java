package applications;


import core.DTNHost;
import core.Message;
import core.SimClock;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

/**
 *各ノードが受信したメッセージを保管し、共有率を計算する。
 * 
 * @author gotsu
 * @param host & msg
 * @return msg
 */
public class DataManager {
	
	public static List<String> MsgHostData=new ArrayList<String>();//受信ホストリスト
	public static List<String> HostData=new ArrayList<String>();//受信ホストリスト

	/**
	 *各ノードが受信したメッセージを保管し、共有率を計算する。
	 * 
	 * @author ごつ
	 * @param host&msg
	 * @return msg
	 */
	public static Message Management(DTNHost host,Message msg) {
	   double share;//共有率
	   if(!(MsgHostData.contains(host.toString()))){
		   MsgHostData.add(host.toString());
		   share=(double)MsgHostData.size()*(double)100/(double)(PingApplication.destMax);
		   try {
			   FileWriter fw = new FileWriter("result/share.txt",true);
			   
					//テキストファイルに２列で表示するためにStringBufferを形成後に結合
			   StringBuilder sb =new StringBuilder(String.valueOf(SimClock.getIntTime()));
			   sb.append("     "+share);
		
		   			//Stringに変換し、書き込み
			   fw.write(sb.toString());
			   fw.write("\n");
			   fw.close();
			
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   
		  
	    } 
	   
	   share=(double)MsgHostData.size()*(double)100/(double)(PingApplication.destMax);
	  // System.out.println("　共有率:"+share+"%");
	   
		return msg;
		
	}
	
	public static void Manager(DTNHost host) {
		   
		   if(!(MsgHostData.contains(host.toString()))){
			   MsgHostData.add(host.toString());
			   /*try {
				   FileWriter fw = new FileWriter("result/GetDSIF.txt",true);
				   
						//テキストファイルに２列で表示するためにStringBufferを形成後に結合
				   StringBuilder sb =new StringBuilder(String.valueOf(host.toString()));
			
			   			//Stringに変換し、書き込み
				   fw.write(sb.toString());
				   fw.write("\n");
				   fw.close();
				   
				   //System.out.println(MsgHostData.size()+"個のノードが災害地データを取得しました。");
				
			   } catch (IOException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   }*/
			   
			  
		    } 
		
		
	}
	public static void Manager2(DTNHost host) {
		   if(!(HostData.contains(host.toString()))){
			   HostData.add(host.toString());
			   
			   /*try {
				   FileWriter fw2 = new FileWriter("result/HitDSPT.txt",true);
				   
						//テキストファイルに２列で表示するためにStringBufferを形成後に結合
				   StringBuilder sb2 =new StringBuilder(String.valueOf(host.toString()));
			
			   			//Stringに変換し、書き込み
				   fw2.write(sb2.toString());
				   fw2.write("\n");
				   fw2.close();
				   
				   
				
			   } catch (IOException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   }*/
			 //  System.out.println(host+"が災害地に到達しました。");
			  // System.out.println(HostData.size()+"個のノードが災害地に到達しました。");
			  
		    } 
	}	

		   public static void WriteInfile() {
		       try {
		    	   FileWriter fw = new FileWriter("result/GetDSIF&HitDSPT.txt",true);
		    	  // FileWriter fw2 = new FileWriter("result/HitDSPT.txt",true);
			   
		    	   StringBuilder sb =new StringBuilder(String.valueOf(MsgHostData.size())+"   "+String.valueOf(HostData.size()));
				  // sb.append("     "+share);
		    	   fw.write(String.valueOf(sb.toString()));
		    	   fw.write("\n");
		    	   fw.close();	
		    	  	
			    
		       }catch (IOException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   }
	}
	
	public static void updatefile() {
		 File file=new File("result/HitDSPT.txt");
		 File file2=new File("result/GetDSIF.txt");
		   if(file.exists())
			   file.delete();
		   if(file2.exists())
			   file2.delete();
	}
	

}



