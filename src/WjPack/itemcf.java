package WjPack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class itemcf {
	/*
	 * 
	 *     主函数~
	 * 
	 */
	/*public static void main(String[] args) throws IOException { 
		
		_Run();
		
	}*/
	
	/*
	 * 
	 *     基于物品的实现~
	 * 
	 */
	
	
	
	
	
	
	static int usersum = 20836;     //用户数
	static int itemsum = 200;	//物品总数
	static int N = 3;           //推荐个数
	static int[][] train; //训练集合user item rate矩阵
	static int[][] test;//测试集合user item rate矩阵
	static double[][] trainuseritem; //训练集合user item 兴趣程度 矩阵
	static int[][] recommend;  //为每个用户推荐N个物品
	static simi [][]simiItem; //排序后的相似性矩阵
	static double [][]itemsim; //未排序的相似性矩阵
	static String road = "data/3 总评论/酒店-评论（已转化）.txt";  //数据路径，格式为用户编号::物品编号::评分
	static String road2 = "data/10 推荐/(已转化)天河酒店ID.txt";
	static String road3 = "data/10 推荐/物品推荐.txt";
	public static String road4 = "data/10 推荐/(已转化)用户ID.txt";
	public static class simi
	{
		double value; //相似值
		int num;	 //相似物品号
	};
	
	public static void _Run() throws IOException {
		
		get_user_hotel_num();
		System.out.println("usersum: "+usersum);
		System.out.println("itemsum: "+itemsum);
		train = new int[itemsum][usersum]; train[0][0] = 0; //训练集合user item rate矩阵
		test = new int[itemsum][usersum]; test[0][0] = 0;  //测试集合user item rate矩阵
		trainuseritem = 
				new double[usersum][itemsum]; trainuseritem[0][0] = 0.0; //训练集合user item 兴趣程度 矩阵
		recommend = new int[usersum][N]; recommend[0][0] = 0;  //为每个用户推荐N个物品
		simiItem = new simi[itemsum][itemsum]; //排序后的相似性矩阵
		
		itemsim = new double[itemsum][itemsum]; //未排序的相似性矩阵

		int i,j,k = 8;        //去用户的k个最近邻居（相似度最高）来计算推荐物品
		
		for(i = 0 ;i < itemsum;++i)
			for(j = 0 ;j < itemsum;++j) simiItem[i][j] = new simi();
		
		System.out.println("1.训练集");
		SplitData(8,1); 
	    //输出初始化的矩阵
		/*for (i=0;i<10;i++)
	 	{
	 		System.out.println("Item"+i+":  ");
	 		for (j=0;j<5;j++)
	 		{
	 			System.out.print(train[i][j]+"  ");
	 		}
	 		System.out.println();
	 	}*/
		
		
		System.out.println("2.计算物品之间相似性，得到相似性矩阵");
		for (i=0;i<itemsum;i++) 
		{
			for (j=0;j<itemsum;j++)
			{
				itemsim[i][j] = Simility(train[i],train[j]);
				if(i == j) itemsim[i][j] = 0;   //此处有bug，已修改
			}
		}
		//输出物品相似性矩阵
		/*for (i=0;i<5;i++)
	 	{
	 		System.out.println("Item"+":  ");
	 		for (j=0;j<100;j++)
	 		{
	 			System.out.print(itemsim[i][j]+"  ");
	 		}
	 		System.out.println();
	 	}*/
		
		System.out.println("3.物品相似度由高到低排序");
		sort();
		//输出排序后的物品相似性矩阵
		
		/*for(i=0;i<5;i++)
		{
			System.out.println("Item"+i+":  ");
			for(j=0;j<10;j++)
			{
				System.out.print(simiItem[i][j].num+","+simiItem[i][j].value+" ");
			}
			System.out.println();
		}*/
	    
		
		
		
		System.out.println("4.得到用户对物品兴趣程度的矩阵");
		for(i=0;i<usersum;i++)
		{
			for(j=0;j<itemsum;j++)
			{
				if(train[j][i]==0)            //如果用户i对物品j没有过行为，才计算i对j的预测兴趣程度
					//trainuseritem[i][j]=
			    	getUserLikeItem(i,j,k);
				
			}
		}
		//输出用户对物品兴趣的矩阵
		/*for (i=0;i<5;i++)
	 	{
	 		System.out.println("User_ins"+i+":  ");
	 		for (j=0;j<10;j++)
	 		{
	 			System.out.print(trainuseritem[i][j]+"  ");
	 		}
	 		System.out.println();
	 	}*/
		System.out.println("5.通过物品兴趣程度，推荐前N个");
		getRecommend();
		//输出推荐矩阵
		for (i=0;i<200;i++)
	 	{
	 		System.out.println("user"+(i+1));
	 		for (j=0;j<N;j++)
	 		{
	 			if(recommend[i][j] != 0)
	 				System.out.print(recommend[i][j]+" ");
	 		}
	 		System.out.println();
	 	}
		
		System.out.println("6.输出到txt");
		out_txt(road2,road3);
		
		
	}
	//获取酒店和用户的数量
    public static void get_user_hotel_num() throws IOException { 
    	FileReader data_about = new FileReader(road2);
 		BufferedReader read_data_about=new BufferedReader(data_about);
 		int num = 0;
		while(read_data_about.readLine() != null) num++;
 		itemsum = num;
		data_about.close();
		read_data_about.close();
		
		FileReader data_about2 = new FileReader(road4);
		BufferedReader read_data_about2=new BufferedReader(data_about2);
 		num = 0;
		while(read_data_about2.readLine() != null) num++;
 		usersum = num;
		data_about2.close();
		read_data_about2.close();

    }
	
    
    //将评论结构输出到txt中
	public static void out_txt(String r1,String r2) throws IOException {
		FileReader data_about=new FileReader(r1);
		BufferedReader read_data_about=new BufferedReader(data_about);
		FileWriter fw=new FileWriter(r2);
		
		String id; //暂存文件一行记录
		int id_num = 1;
		String []tmps = new String[5];
		String []hotel = new String[201];
		while((id=read_data_about.readLine())!=null){ 
			tmps = id.split("::");
			String hotelname = tmps[0];
			String number = tmps[1];
			hotel[Integer.parseInt(number)] = hotelname;
		}
		int i,j;
		for (i=0;i<usersum;i++)
	 	{
			fw.write("user"+(i+1));
	 		for (j=0;j<N;j++)
	 		{
	 			if(recommend[i][j] != 0)
	 				fw.write("::"+hotel[recommend[i][j]]);
	 		}
	 		fw.write("\r\n");
	 	}
		data_about.close();
		read_data_about.close();
		fw.close();
	}
	
	//这里全部为测试集，k并没有用
	//拆分数据集为测试集test和训练集trainuser，其中1/m为测试集,取不同的k<=m-1值 在相同的随即种子下可得到不同的测/训集合
	public static int SplitData(int m, int k)
	{	   
		
		int usernum = 0;
		int itemnum = 0;
                
		try {
			FileReader data_about=new FileReader(road);
			BufferedReader read_data_about=new BufferedReader(data_about);
			String s2; //暂存文件一行记录
			try {
				while((s2=read_data_about.readLine())!=null){
					//寻找数据集每条记录对应的用户号和物品号
					int sum = 0,ok = 0;
					for(int m_ = 0;m_ < s2.length();++m_) {
						if(s2.charAt(m_) != ':')
							sum = sum * 10 + s2.charAt(m_) - 48;
						else {
							m_ += 1;
							if(ok == 0) {usernum = sum;ok = 1;}
							else {
								itemnum = sum;
								break;
							}
							sum = 0;
						}
					}
					
					if (usernum <= usersum && itemnum <= itemsum)
					{
						//if(System.currentTimeMillis()%(m-1)==k) //设置当前时间为随机种子  //判断随机产生0-7之间的随机数是否等于k
						//   test[itemnum-1][usernum-1] = 1;        //rate为评分，再此实验中只需统计有无评分的，无需讨论具体评分
					    //else
						   train[itemnum-1][usernum-1] = 1;  //用户号的物品号均从0开始算起，
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 		try {
				data_about.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		try {
				read_data_about.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}

	//利用训练集计算用户之间相似度
	/* 计算向量ItemA和ItemB的相似性，返回值为ItemA和ItemB的相似度 */
	public static double Simility(int[] ItemA, int[] ItemB)
	{
		int comUser = 0;                   //ItemA与ItemB的都被用户评论的用户个数
		double simility = 0.0;
		int countIa = 0;
		int countIb = 0;

		int i;
		for (i=0;i<usersum;i++)      //此处有bug，已修改
		{
			if (ItemA[i]>0&&ItemB[i]>0)
			{
				comUser++;//查找ItemA与ItemB的都被用户评论的用户个数
			}
			if (ItemA[i]>0){
				countIa++;//评论ItemA的用户数量
			}
			if (ItemB[i]>0){
				countIb++;//评论ItemB的用户数量
			}
		}
		double tem = Math.sqrt(countIa*countIb);

		if(tem == 0)
		{
			return 0;
		}
		else
		{
	    	simility = comUser/tem;
		    return simility;
		}
		
	}


	/*物品相似性矩阵排序（根据相似性由高到低排序）*/
	public static void quickSort(int x, int start, int end) {   
	    if (start < end) {   
	    	double base = simiItem[x][start].value; // 选定的基准值（第一个数值作为基准值）   
	    	double temp; // 记录临时中间值   
	    	int i_tmp;
	        int i = start, j = end;   
	        do {   
	            while ((simiItem[x][i].value > base) && (i < end))   
	                i++;   
	            while ((simiItem[x][j].value < base) && (j > start))   
	                j--;   
	            if (i <= j) {    
	                temp = simiItem[x][i].value;   
	                simiItem[x][i].value = simiItem[x][j].value;   
	                simiItem[x][j].value = temp;  
	                i_tmp = simiItem[x][i].num;   
	                simiItem[x][i].num = simiItem[x][j].num;   
	                simiItem[x][j].num = i_tmp;   
	                i++;   
	                j--;   
	            }   
	        } while (i <= j);   
	        if (start < j)   
	            quickSort(x, start, j);   
	        if (end > i)   
	            quickSort(x, i, end);   
	    }   
	}  
	public static int sort()
	{
		for (int i=0;i<itemsum;i++)
		{
			
			for(int j = 0; j < itemsum; ++j) {
				simiItem[i][j].num = j;
				simiItem[i][j].value = itemsim[i][j];
			}
			quickSort(i,0,itemsum-1);
		}
		return 1;

	}

	//得到用户i对物品j预测兴趣程度，用于推荐
	public static double getUserLikeItem(int i,int j,int k)
	{
		for(int x=0;x<k;x++)//从物品j最相似的k个物品中，找出用户i有过行为的物品
		{
			//System.out.println(simiItem[j][x].num);
			if(train[simiItem[j][x].num][i]>0)//若这个用户同样对相似物品也有过行为
			{
				trainuseritem[i][j]+=simiItem[j][x].value;
			}
		}
		return trainuseritem[i][j];
	}
	
	/*通过物品兴趣程度，推荐前N个*/ 
	public static int getRecommend() //有bug,已修改
	{
		int maxnum;//当前最感兴趣物品号
		for(int i=0;i<usersum;i++)
		{

			int []finflag = new int[itemsum];

			for (int x=0;x<N;x++)//推荐N个
			{
				maxnum = 0;
				while(maxnum < itemsum && finflag[maxnum]!=0)
					maxnum++;
				for (int j=0;j<itemsum;j++)  //每循环一次就寻找此次感兴趣最大的物品
				{
				
					if (trainuseritem[i][maxnum] < trainuseritem[i][j]&&finflag[j]==0)
						maxnum = j;
				}
				finflag[maxnum] = 1;
				if(trainuseritem[i][maxnum] != 0)
					recommend[i][x]=maxnum+1;//recommend数组从1开始使用
			}
		}
		return 1;
	}

}
