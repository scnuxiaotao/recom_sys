package WjPack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class new_ojld_dis {
	/*
	 * 
	 *     主函数~
	 * 
	 */	

    /*public static void main(String[] args) throws IOException {
    	
    	run();
        
    }*/

	
	/*
	 * 
	 *     基于用户的实现~
	 * 
	 */
	
	
	static String road_main = "data";
	static String road = road_main + "/3 总评论/酒店-评论（已转化）.txt";//数据路径，格式为用户编号::物品编号::评分
	static String road2 = road_main + "/10 推荐/(已转化)用户-关注.txt";  //数据路径，格式为用户编号::关注编号
	static String road3 = road_main + "/10 推荐/(已转化)天河酒店ID.txt";
	static String road4 = road_main + "/10 推荐/用户推荐.txt"; 
	static String road5 = road_main + "/10 推荐/(已转化)用户ID.txt";
	
	static int usersum = 20836;     //用户数
	static int itemsum = 200;	//物品总数
	
    static Map<String,HashMap<String,Integer>> score = new HashMap<String,HashMap<String,Integer>>();
    static Set<String> userSet = new HashSet<String>();
    static Set<String> filmSet = new HashSet<String>();
    
    static FileWriter txtw;
    
    static String tjhotel = "";
    
    static ArrayList<String> arr;
    static {

    	arr = new ArrayList<String>();
        try {
			score = get_score_from_road();
		} catch (IOException e) {
		}
    }
	
    public static void run() throws IOException {
    	txtw=new FileWriter(road4);
    	
    	get_hotelid(road3);
    	for(int m = 0;m < 20836;++m) {
    		tjhotel = "";
    		new_ojld_dis.outNearbyUserList(arr.get(m));
    		
    		if(tjhotel.length() > 1)
    			txtw.write("user"+arr.get(m)+tjhotel+"\r\n");

    	}
    	txtw.close();
    
    }
    public static void init() {
    	try {
			get_user_hotel_num();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	System.out.println(usersum);
    	System.out.println(itemsum);
    	
    }
    public static void get_user_hotel_num() throws IOException { 
    	FileReader data_about = new FileReader(road3);
 		BufferedReader read_data_about=new BufferedReader(data_about);
 		int num = 0;
		while(read_data_about.readLine() != null) num++;
 		itemsum = num;
		data_about.close();
		read_data_about.close();
		
    	data_about = new FileReader(road5);
 		read_data_about=new BufferedReader(data_about);
 		num = 0;
		while(read_data_about.readLine() != null) num++;
 		usersum = num;
		data_about.close();
		read_data_about.close();

    }
	
	public static Map<String,HashMap<String,Integer>> get_score_from_road() throws IOException {
		init();
		String []tmps = new String[5];
		FileReader data_about=new FileReader(road);
		BufferedReader read_data_about=new BufferedReader(data_about);
		String s2;
		String usertmp = null;
		Map<String,HashMap<String,Integer>> score = new HashMap<String,HashMap<String,Integer>>();
        HashMap<String,Integer> tempScore = new HashMap<String,Integer>();
		while((s2=read_data_about.readLine())!=null){

			//寻找数据集每条记录对应的用户号和物品号
			tmps = s2.split("::");
			String username = tmps[0];
			String filmname = tmps[1];
			Integer socrename = Integer.valueOf(tmps[2]);
			
			if(usertmp == null) {usertmp = username;arr.add(usertmp);}
			else if(!usertmp.equals(username)) {
				score.put(usertmp, tempScore);
				usertmp = username;
				arr.add(usertmp);
				tempScore = new HashMap<String,Integer>();
			}
			
	        tempScore.put(filmname, socrename);	     
		}
		score.put(usertmp, tempScore);
		arr.add(usertmp);
		return score;
	}

		
			
    public static void outNearbyUserList(String user) throws IOException {
    	FileReader data_about=new FileReader(road2);
		BufferedReader read_data_about=new BufferedReader(data_about);
    	
        Map<String,Double> scores = new HashMap<String,Double>();
        
        String []tmps = new String[5];
        HashMap<String,Integer> thing = new HashMap<String,Integer>();
		
		String id; 
		int num = 0;
		while((id=read_data_about.readLine())!=null){ 
			
			tmps = id.split("::");
			String username = tmps[0];
			String fansname = tmps[1];
				
			if(username.equals(user)) {
				thing.put(fansname, 1);
			}
				
		}
		
        for (int m = 0;m < arr.size()-1;++m) {
        	String tempUser = arr.get(m);
            if (tempUser.equals(user) || !thing.containsKey(tempUser)) {
                continue;
            }
            
            double score = getOSScore(user, tempUser);
            
            if(score >= 0)
            	scores.put(tempUser, score);
        }
        data_about.close();
		read_data_about.close();
        
    }
    
    private static Double getOSScore(String user1, String user2) throws NumberFormatException, IOException {
    	HashMap<String,Integer> user1Score = (HashMap<String,Integer>) score.get(user1);
    	HashMap<String,Integer> user2Score = (HashMap<String,Integer>) score.get(user2);
        double totalscore = 100;
        ArrayList<String> hobby = new ArrayList<String>();
        Iterator<String> it = user1Score.keySet().iterator();
        while (it.hasNext()) {
            String film = (String) it.next();
            int a1 = (Integer) user1Score.get(film);
            //System.out.println(film);
            if(user2Score.get(film) == null) continue; 
            int b1 = (Integer) user2Score.get(film);
            int a = a1 * a1 - b1 * b1;
            //System.out.println(Math.abs(a));
            totalscore = Math.sqrt(Math.abs(a));
        }
        if(totalscore == 0) {
        	int ok = 0;
        	it = user2Score.keySet().iterator();
        	if(it != null) {
                while (it.hasNext()) {
                    String film = (String) it.next();
                    if(user1Score.get(film) == null) {
                    	if(ok == 0) {
                    		ok = 1; 
                    	}
                    	tjhotel+="::"+hotel[Integer.parseInt(film)];
                    	
                    }
                    
                }
        		
                
        	}
        	
        	
        }
        return totalscore;
    }

    static String []hotel = new String[201];
    public static void get_hotelid(String r1) throws IOException {
		FileReader data_about=new FileReader(r1);
		BufferedReader read_data_about=new BufferedReader(data_about);
		
		String id; 
		String []tmps = new String[5];
		
		while((id=read_data_about.readLine())!=null){ 
			tmps = id.split("::");
			String hotelname = tmps[0];
			String number = tmps[1];
			hotel[Integer.parseInt(number)] = hotelname;
		}
		data_about.close();
		read_data_about.close();
	}

}
