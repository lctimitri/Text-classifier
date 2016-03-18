package textclassification;

import java.io.*;
import java.util.ArrayList;
import java.util.*;
import java.util.Scanner;


public class TextClassify {

	static double[] advertisevect;//store log（P（Wi|advertise）*p（advertise））
	static double[] financialvect;//store log（P（Wi|financial）*p（financial））
	static double[] noticevect;// store log（P（Wi|notice）*p（notice））
	
	static double pAdvertise;//advertise probability
	static double pFinancial;//financial probability
	
	static ArrayList<String> wordList;//all words in training set
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TextClassify mc=new TextClassify();
		try {
			//training algorithm
			System.out.println("=====training starts=====");
			mc.Train();
			System.out.println("=====training complete=====");
			System.out.println("=====test starts=====");
			mc.Test();
			System.out.println("=====test complete=====");
			//read one line, let console not quit
			Scanner sc=new Scanner(System.in);
			String ts=sc.nextLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	//training process
	private void Train() throws IOException{
		int advertiseNum=ReadFiles.GetFileNum("C:/Users/lctimitri/Desktop/TextClassify/dataset/training set/advertise");//get ad number
		System.out.println("advertise emails for training "+advertiseNum);
		
		int financialNum=ReadFiles.GetFileNum("C:/Users/lctimitri/Desktop/TextClassify/dataset/training set/financial");//get financial number
		System.out.println("financial emails for training "+financialNum);
		
		int noticeNum=ReadFiles.GetFileNum("C:/Users/lctimitri/Desktop/TextClassify/dataset/training set/notice");//get notice number
		System.out.println("notice emails for training "+noticeNum);
		
		wordList=ReadFiles.GetWordsList("C:/Users/lctimitri/Desktop/TextClassify/dataset/training set");
		int wordNum=wordList.size();//total words number
		
		int mailNum=advertiseNum+financialNum+noticeNum;//total email number
		
		pAdvertise=advertiseNum/(mailNum+0.0);//the ad probability in all emails
		pFinancial=financialNum/(mailNum+0.0);//the financial probability in all emails
		
		
		MyArray advertiseArray=new MyArray(wordNum);//counter to record all word in ads
		advertiseArray.InitArray(1);
		
		MyArray financialArray=new MyArray(wordNum);//counter to record all word in financial emails
		financialArray.InitArray(1);
		
		MyArray noticeArray=new MyArray(wordNum);//counter to record all word in notice emails
		noticeArray.InitArray(1);
		
		System.out.println(wordNum);
		
		int advertiseWords=2,financialWords=2,noticeWords=2;//counters for word number in ads, financial and notices respectly
		
		//get the word vector in training emails
		ArrayList<MyArray> trainMatrix=ReadFiles.GetMatrix("C:/Users/lctimitri/Desktop/TextClassify/dataset/training set", wordList);
		
		//process the ads in trainMatrix first part
		for(int i=0;i<advertiseNum;i++)
		{
			advertiseArray.Add(trainMatrix.get(i));//the sum of vectors
			advertiseWords+=trainMatrix.get(i).NumOfOne();//how many kinds of different words in this ad
		}
		
		//process the financial emails in trainMatrix second part
		for(int i=advertiseNum;i<advertiseNum+financialNum;i++)
		{
			financialArray.Add(trainMatrix.get(i));//the sum of vectors
			financialWords+=trainMatrix.get(i).NumOfOne();//how many kinds of different words in this financial email
		}
		
		//process the notices in trainMatrix third part
		for(int i=advertiseNum+financialNum;i<mailNum;i++)
		{
			noticeArray.Add(trainMatrix.get(i));//the sum of vectors
			noticeWords+=trainMatrix.get(i).NumOfOne();//how many kinds of different words in this notice
		}
		
		advertisevect=new double[wordNum];//store log（P（Wi|advertise）*p（advertise））
		financialvect=new double[wordNum];//store log（P（Wi|financial）*p（financial））
		noticevect=new double[wordNum];//store log（P（Wi|notice）*p（notice））
		
		//compute log（P（Wi|advertise）*p（advertise））,log（P（Wi|financial）*p（financial））和log（P（Wi|notice）*p（notice））
		// sames as p（w1|ad）*p（w2|ad）*·····p（wn|ad）
		for(int i=0;i<wordNum;i++)
		{
			advertisevect[i]=Math.log(advertiseArray.getArray()[i]/(advertiseWords+0.0));
			financialvect[i]=Math.log(financialArray.getArray()[i]/(financialWords+0.0));
			noticevect[i]=Math.log(noticeArray.getArray()[i]/(noticeWords+0.0));
		}
		
		/*System.out.println("advertisevect[i]");
	    for(int i=0;i<wordNum;i++)
		{   
	    	System.out.println("advertisevect[i]");
			System.out.print(advertisevect[i]+"  ");
			if(i%20==0)
				System.out.println();
				
		}
		
	    System.out.println("financialvect[i]");
		for(int i=0;i<wordNum;i++)
		{   
			System.out.print(financialvect[i]+"  ");
			if(i%20==0)
				System.out.println();
				
		}
		
		System.out.println("noticevect[i]");
		for(int i=0;i<wordNum;i++)
		{   
			System.out.print(noticevect[i]+"  ");
			if(i%20==0)
				System.out.println();
				
		}
	    */
	}
	
	//test process
	public void Test() throws IOException {
		File file = new File("C:/Users/lctimitri/Desktop/text classification result.txt");
		
		int tAdvertiseNum=ReadFiles.GetFileNum("C:/Users/lctimitri/Desktop/TextClassify/dataset/test set/advertise");//number of ad in test set
		System.out.println("advertise emails for test "+tAdvertiseNum);
		
		int tFinancialNum=ReadFiles.GetFileNum("C:/Users/lctimitri/Desktop/TextClassify/dataset/test set/financial");//number of financial emails in test set
		System.out.println("financial emails for test "+tFinancialNum);
		
		int tNoticeNum=ReadFiles.GetFileNum("C:/Users/lctimitri/Desktop/TextClassify/dataset/test set/notice");//number of notice emails in test set
		System.out.println("notice emails for test "+tNoticeNum);
		
		int tMailNum=tAdvertiseNum+tFinancialNum+tNoticeNum;// total emails in test
		
		
		int tRightNum=0;//correct decision emails number

		//test set's data
		ArrayList<MyArray> testMatrix=ReadFiles.GetMatrix("C:/Users/lctimitri/Desktop/TextClassify/dataset/test set", wordList);
		//System.out.println(testMatrix.size());
		//get the file path in order 
		ArrayList<String> fileName=ReadFiles.GetFileName("C:/Users/lctimitri/Desktop/TextClassify/dataset/test set");
		//System.out.println(fileName.size());
		try{
			FileWriter fw = new FileWriter(file);
			BufferedWriter bufw = new BufferedWriter(fw);
			bufw.write("=====The bayesian result=====");
			bufw.newLine();
			
		for(int i=0;i<tMailNum;i++)
		{
			double tPAdvertise=0;//the probability to be ad
			double tPFinancial=0;//the probability to be financial email
			double tPNotice=0;//the probability to be notice
			
			//compute testMatrix.get(i)*advertisevect
			for(int j=0;j<wordList.size();j++)
			{
				tPAdvertise+=testMatrix.get(i).getArray()[j]*advertisevect[j];
				tPFinancial+=testMatrix.get(i).getArray()[j]*financialvect[j];
				tPNotice+=testMatrix.get(i).getArray()[j]*noticevect[j];
			}
			//use log
			tPAdvertise+=Math.log(pAdvertise);
			tPFinancial+=Math.log(pFinancial);
			tPNotice+=Math.log(1-pAdvertise-pFinancial);
			
			//if the probability to be ad is highest, it's ad
			if(tPAdvertise>tPFinancial && tPAdvertise>tPNotice)
			{
				System.out.println("advertise emails:  "+fileName.get(i));
				bufw.write("advertise emails: "+fileName.get(i));
				bufw.newLine();
				
				if(i<tAdvertiseNum)
					tRightNum++;
			}
			else if (tPFinancial>tPAdvertise && tPFinancial>tPNotice)
			{
				System.out.println("financial emails:  "+fileName.get(i));
				bufw.write("financial emails: "+fileName.get(i));
				bufw.newLine();
				
				if(i>=tAdvertiseNum && i<tAdvertiseNum+tFinancialNum)
					tRightNum++;
			}
			else if (tPNotice>tPAdvertise && tPNotice>tPFinancial)
			{
				System.out.println("notice emails:  "+fileName.get(i));
				bufw.write("notice emails: "+fileName.get(i));
				bufw.newLine();
				
				if(i>=tAdvertiseNum+tFinancialNum)
					tRightNum++;
			}
			
		}
		
		bufw.newLine();
		bufw.write("The well-judged email number is "+tRightNum);
		bufw.newLine();
		
		bufw.write("The total email for test is: "+Integer.toString(tMailNum));
		bufw.newLine();
		
		bufw.write("classification accuracy="+tRightNum/(tAdvertiseNum+tFinancialNum+tNoticeNum+0.0));
		bufw.newLine();
		
		bufw.close();
		fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("The well-judged email number is "+tRightNum);
		System.out.println("The total email number for test is "+(tAdvertiseNum+tFinancialNum+tNoticeNum));
		System.out.println("classification accuracy="+tRightNum/(tAdvertiseNum+tFinancialNum+tNoticeNum+0.0));

	}

}

