package com.mfanw.test.bayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * <b>贝叶斯垃圾邮件检测<b>
 * <p>
 * 利用朴素贝叶斯原理对邮件进行分类
 * </p>
 * 
 * @author mengwei
 */
public class BayesMailDetection {

	/**
	 * 邮件存放的基准路径
	 */
	public static final String BASE_PATH = "C:\\Users\\zhf\\Desktop\\mail";
	/**
	 * 正常邮件语料
	 */
	public static final String NORMAL_EMAIL_PATH = BASE_PATH + "\\train_legal.txt";
	/**
	 * 垃圾邮件语料
	 */
	public static final String SPAM_EMAIL_PATH = BASE_PATH + "\\train_illegal.txt";
	/**
	 * 要判别的邮件
	 */
	public static final String TEST_EMAIL_PATH = BASE_PATH + "\\email.txt";

	public static final String DICT_PATH = "";

	public static void main(String[] args) throws Exception {
		BayesMailDetection smc = new BayesMailDetection();
		Map<String, Double> normalWFMap = smc.createWordFrequencyMap(NORMAL_EMAIL_PATH);
		Map<String, Double> spamWFMap = smc.createWordFrequencyMap(SPAM_EMAIL_PATH);
		Map<String, Double> wordRateMap = smc.createSpamProbabilityMap(spamWFMap, normalWFMap);
		double probability = smc.judgeMail(TEST_EMAIL_PATH, wordRateMap);
		if (probability > 0.5)// 概率大于0.5则判定为垃圾
			System.out.println("这是正常邮件");
		else
			System.out.println("这是垃圾邮件");

	}

	/**
	 * 给定邮件,分词,根据分词结果判断是垃圾邮件的概率
	 * P(Spam|t1,t2,t3……tn)=（P1*P2*……PN）/(P1*P2*……PN+(1-P1)*(1-P2)*……(1-PN))
	 */
	public double judgeMail(String emailPath, Map<String, Double> ratemap) throws Exception {
		List<String> list = segment(FileUtils.readFileToString(new File(emailPath)));
		double rate = 1.0;
		double tempRate = 1.0;
		for (String str : list) {
			if (ratemap.containsKey(str)) {
				double tmp = ratemap.get(str);
				tempRate *= 1 - tmp;
				rate *= tmp;
			}
		}
		return rate / (rate + tempRate);
	}

	/**
	 * 从给定的垃圾邮件、正常邮件语料中建立map <切出来的词,出现的频率>
	 */
	public Map<String, Double> createWordFrequencyMap(String filePath) throws Exception {
		String str = FileUtils.readFileToString(new File(filePath));
		List<String> list = segment(str);
		Map<String, Integer> tmpmap = new HashMap<String, Integer>();
		Map<String, Double> retmap = new HashMap<String, Double>();
		double rate = 0.0;
		int count = 0;
		for (String s : list) {
			tmpmap.put(s, tmpmap.containsKey(s) ? count + 1 : 1);
		}
		for (Iterator iter = tmpmap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			rate = tmpmap.get(key) / list.size();
			retmap.put(key, rate);
		}
		return retmap;
	}

	/**
	 * 建立map,<str,rate> 邮件中出现ti时,该邮件为垃圾邮件的概率 P( Spam|ti) =P2(ti )/((P1 (ti ) +P2 (
	 * ti ))
	 */
	public Map<String, Double> createSpamProbabilityMap(Map<String, Double> spammap, Map<String, Double> okmap) {
		Map<String, Double> retmap = new HashMap<String, Double>();
		for (Iterator iter = spammap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			double rate = spammap.get(key);
			double allRate = rate;
			if (okmap.containsKey(key)) {
				allRate += okmap.get(key);
			}
			retmap.put(key, rate / allRate);
		}
		return retmap;
	}

	/**
	 * 中文分词
	 */
	public List<String> segment(String str) {
		Map<String, Integer> map = loadDict();
		List<String> list = new ArrayList<String>();
		int len = str.length();
		String term;
		int maxSize = 6;
		int i = 0, j = 0;
		while (i < len) {
			int n = i + maxSize < len ? i + maxSize : len + 1;
			boolean findFlag = false;
			for (j = n - 1; j > i; j--) {
				term = str.substring(i, j);
				if (map.containsKey(term)) {
					list.add(term);
					findFlag = true;
					i = j;
					break;
				}
			}
			if (findFlag == false)
				i = j + 1;
		}
		return list;
	}

	/**
	 * 加载词典文件
	 */
	public Map<String, Integer> loadDict() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		String[] str;
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(DICT_PATH)), "gbk"));
			String tmp = "";
			while ((tmp = br.readLine()) != null) {
				str = tmp.split("\t");
				map.put(str[0], 0);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

}