package WjPack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/*
 * 
 * 因为抓到的用户ID和酒店都是数据都是类似434132这么长的编号，为了方便我用数组存，
 * 事先我全部转化为1开始的编号了。比如说有两个4654654,32131321，
 * 那我就转成1,2了，处理完推荐完再把1,2转成4654654,32131321
 * 
 */
public class id_change {
	
	public static HashMap<String,Integer> user_id = new HashMap<String,Integer>();
	public static HashMap<String,Integer> hotel_id = new HashMap<String,Integer>();
	
	//数据路径
	public static String road_main = "data";
	public static String road1 = road_main + "/0 基本数据/用户ID.txt";
	public static String road1_1 = road_main + "/10 推荐/(已转化)用户ID.txt";
	public static String road2 = road_main + "/0 基本数据/天河酒店ID.txt";
	public static String road2_1 = road_main + "/10 推荐/(已转化)天河酒店ID.txt";
	public static String road3 = road_main + "/9 社交网络/用户-关注.txt";
	public static String road3_1 = road_main + "/10 推荐/(已转化)用户-关注.txt";
	public static String road4 = road_main + "/3 总评论/酒店-评论.txt";
	public static String road4_1 = road_main + "/3 总评论/酒店-评论（已转化）.txt";
	
	
	
	/*public static void main(String[] args) throws IOException, InterruptedException {
		get_rule(user_id,road1,road1_1);//转化用户ID
		get_rule(hotel_id,road2,road2_1);//转化酒店ID
		
		change_text_ins(user_id,road3,road3_1);//转化用户与关注用户的ID
		
		//原始数据看文件夹“3 总评论”的 “完整-用户XXX.txt” 
		//这边我没给出相关的评论评分程序，所以这个函数跑完并没有得出评分
		//跑完大概就是（比如第一条）1::1::这个酒店不错啊
		//只是转了酒店和用户的ID，评论并没有转成评分
		//就不跑了，文件夹里有给跑完后的数据
		//change_text_three(user_id,hotel_id,road4,road4_1);//转化用户-酒店-评分
	}*/
	
	
	
	
	public static void get_rule(HashMap<String,Integer> thing,String r1,String r2) throws IOException {
		FileReader data_about=new FileReader(r1);
		BufferedReader read_data_about=new BufferedReader(data_about);
		FileWriter fw=new FileWriter(r2);
		
		String id; 
		int id_num = 1;
		while((id=read_data_about.readLine())!=null){ 
			if(!thing.containsKey(id)) {
				fw.write(id+"::"+id_num+"\r\n");
				thing.put(id,id_num++);
			}
				
		}
		data_about.close();
		read_data_about.close();
		fw.close();
	}
	public static void change_text_ins(HashMap<String,Integer> thing,String r1,String r2) throws IOException {
		FileReader data_about=new FileReader(r1);
		BufferedReader read_data_about=new BufferedReader(data_about);
		FileWriter fw=new FileWriter(r2);
		
		String []tmps = new String[5];
		
		
		String id; 

		while((id=read_data_about.readLine())!=null){ 
			
			tmps = id.split("::");
			String username = tmps[0];
			String fansname = tmps[1];
			
			if(thing.containsKey(username) && thing.containsKey(fansname)) {
				fw.write(thing.get(username)+"::"+thing.get(fansname)+"\r\n");
			}
				
		}
		data_about.close();
		read_data_about.close();
		fw.close();
	}
	
	public static void change_text_three(HashMap<String,Integer> thing,HashMap<String,Integer> thing2,String r1,String r2) throws IOException {
		FileReader data_about=new FileReader(r1);
		BufferedReader read_data_about=new BufferedReader(data_about);
		FileWriter fw=new FileWriter(r2);
		
		String []tmps = new String[5];
		
		
		String id; 

		while((id=read_data_about.readLine())!=null){ 
			
			tmps = id.split("::");
			String username = tmps[0];
			String fansname = tmps[1];
			String othersname;
			if(tmps.length > 2) {
				othersname= tmps[2];
				
				if(thing.containsKey(username) && thing2.containsKey(fansname)) {
					fw.write(thing.get(username)+"::"+thing2.get(fansname)+"::"+othersname+"\r\n");
				}
			}
				
				
		}
		data_about.close();
		read_data_about.close();
		fw.close();
	}
	



}
