package com.mfanw.test.bayes;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.mfanw.test.bayes.utils.AnsjUtil;
import com.mfanw.test.bayes.utils.PrintUtils;

/**
 * <b>贝叶斯垃圾邮件检测<b>
 * <p>
 * 利用朴素贝叶斯原理对邮件进行分类
 * </p>
 * 
 * @author mengwei
 */
public class BayesMailPredict {

	/**
	 * 从给定的垃圾邮件、正常邮件语料中建立map <切出来的词,出现的频率>
	 */
	public Map<String, Double> createRateMap(String filePath) throws Exception {
		Map<String, Double> rates = new HashMap<String, Double>();
		File parent = new File(filePath);
		if (parent == null || !parent.isDirectory()) {
			return rates;
		}
		File[] children = parent.listFiles();
		if (children == null || children.length == 0) {
			return rates;
		}
		Map<String, Double> wordMaps = new HashMap<String, Double>();
		for (File child : children) {
			String contents = FileUtils.readFileToString(child, "UTF-8");
			List<String> words = AnsjUtil.segment(contents);
			for (String word : words) {
				wordMaps.put(word, wordMaps.containsKey(word) ? wordMaps.get(word) + 1 : 1);
			}
		}
		double rate = 0.0;
		for (Iterator<String> it = wordMaps.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			rate = wordMaps.get(key) / wordMaps.size();
			rates.put(key, rate);
		}
		return rates;
	}

	/**
	 * 统计垃圾邮件中词综合正常邮件后的概率<br />
	 * 
	 * word垃圾邮件预测值=垃圾概率/(垃圾概率+正常概率)
	 */
	public Map<String, Double> createPredictMap(Map<String, Double> spamRates, Map<String, Double> normalRates) {
		Map<String, Double> preditRates = new HashMap<String, Double>();
		for (Iterator<String> it = spamRates.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			double spamRate = spamRates.get(key);
			double normalRate = 0.0001;
			if (normalRates.containsKey(key)) {
				normalRate = normalRates.get(key);
			}
			// 贝叶斯公式
			double bayesRate = (Consts.SPAM_RATE * spamRate) / (Consts.SPAM_RATE * spamRate + (1 - Consts.SPAM_RATE) * normalRate);
			preditRates.put(key, bayesRate);
		}
		return preditRates;
	}

	/**
	 * 给定邮件,分词,根据分词结果判断是垃圾邮件的概率<br />
	 * P(Spam|t1,t2,t3……tn)=（P1*P2*……PN）/(P1*P2*……PN+(1-P1)*(1-P2)*……(1-PN))
	 */
	public void judgeMail(String filePath, Map<String, Double> preditRates) throws Exception {
		File parent = new File(filePath);
		if (parent == null || !parent.isDirectory()) {
			return;
		}
		File[] children = parent.listFiles();
		if (children == null || children.length == 0) {
			return;
		}
		for (File child : children) {
			List<String> words = AnsjUtil.segment(FileUtils.readFileToString(child));
			double rate = 1.0;
			double wordRate = 1.0;
			for (String word : words) {
				if (preditRates.containsKey(word)) {
					double predit = preditRates.get(word);
					wordRate *= 1 - predit;
					rate *= predit;
				}
			}
			double probability = rate / (rate + wordRate);
			if (probability > 0.5) {
				System.err.println(child.getName() + "\t 这是垃圾邮件 \t" + probability);
			} else {
				System.out.println(child.getName() + "\t 这是正常邮件 \t" + probability);
			}
		}
	}

	public void start() throws Exception {
		// 1、计算正常邮件语料的词频
		Map<String, Double> normalRates = createRateMap(Consts.NORMAL_EMAIL_PATH);
		PrintUtils.printMap("正常邮件语料", normalRates);

		// 2、计算垃圾邮件语料的词频
		Map<String, Double> spamRates = createRateMap(Consts.SPAM_EMAIL_PATH);
		PrintUtils.printMap("垃圾邮件语料", spamRates);

		// 3、应用bayes公式计算垃圾邮件中词对判定垃圾邮件的概率值
		Map<String, Double> preditRates = createPredictMap(spamRates, normalRates);
		PrintUtils.printMap("预测", preditRates);

		// 4、根据分词结果判断是垃圾邮件的概率
		judgeMail(Consts.TEST_EMAIL_PATH, preditRates);
	}

	public static void main(String[] args) throws Exception {
		new BayesMailPredict().start();
	}

}