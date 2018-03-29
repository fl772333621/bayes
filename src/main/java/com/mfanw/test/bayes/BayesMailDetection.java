package com.mfanw.test.bayes;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

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
	public static final String BASE_PATH = "D:\\Bayes\\email\\";
	/**
	 * 正常邮件语料
	 */
	public static final String NORMAL_EMAIL_PATH = BASE_PATH + "ham\\";
	/**
	 * 垃圾邮件语料
	 */
	public static final String SPAM_EMAIL_PATH = BASE_PATH + "spam\\";
	/**
	 * 要判别的邮件
	 */
	public static final String TEST_EMAIL_PATH = BASE_PATH + "test\\";

	public static void main(String[] args) throws Exception {
		// 1、计算正常邮件语料的词频
		Map<String, Double> normalRates = createRateMap(NORMAL_EMAIL_PATH);
		printMap("正常邮件语料", normalRates);

		// 2、计算垃圾邮件语料的词频
		Map<String, Double> spamRates = createRateMap(SPAM_EMAIL_PATH);
		printMap("垃圾邮件语料", spamRates);

		// 3、应用bayes公式计算垃圾邮件中词对判定垃圾邮件的概率值
		Map<String, Double> preditRates = createPredictMap(spamRates, normalRates);
		printMap("预测", preditRates);

		// 4、根据分词结果判断是垃圾邮件的概率
		judgeMail(TEST_EMAIL_PATH, preditRates);
	}

	/**
	 * 从给定的垃圾邮件、正常邮件语料中建立map <切出来的词,出现的频率>
	 */
	public static Map<String, Double> createRateMap(String filePath) throws Exception {
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
			List<String> words = segment(contents);
			for (String word : words) {
				if (word == null || word.trim().isEmpty() || word.length() < 2) {
					continue;
				}
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
	 * 应用bayes公式计算垃圾邮件中词对判定垃圾邮件的概率值<br />
	 * 邮件中出现word时<br />
	 * 该邮件为垃圾邮件的概率 P(Spam|word) =P(Spam)/P(word)*P(word|Spam)
	 */
	public static Map<String, Double> createPredictMap(Map<String, Double> spamRates, Map<String, Double> normalRates) {
		Map<String, Double> preditRates = new HashMap<String, Double>();
		for (Iterator<String> it = spamRates.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			double rate = spamRates.get(key);
			double allRate = rate;
			if (normalRates.containsKey(key)) {
				allRate += normalRates.get(key);
			}
			preditRates.put(key, rate / allRate);
		}
		return preditRates;
	}

	/**
	 * 给定邮件,分词,根据分词结果判断是垃圾邮件的概率
	 * P(Spam|t1,t2,t3……tn)=（P1*P2*……PN）/(P1*P2*……PN+(1-P1)*(1-P2)*……(1-PN))
	 */
	public static void judgeMail(String filePath, Map<String, Double> preditRates) throws Exception {
		File parent = new File(filePath);
		if (parent == null || !parent.isDirectory()) {
			return;
		}
		File[] children = parent.listFiles();
		if (children == null || children.length == 0) {
			return;
		}
		for (File child : children) {
			List<String> words = segment(FileUtils.readFileToString(child));
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

	/**
	 * 中文分词
	 */
	public static List<String> segment(String message) {
		Result result = BaseAnalysis.parse(message);
		List<Term> terms = result.getTerms();
		List<String> words = Lists.newArrayList();
		if (terms == null || terms.isEmpty()) {
			return words;
		}
		for (Term term : terms) {
			if (term == null) {
				continue;
			}
			if (term.getName() == null || term.getName().isEmpty() || term.getName().length() < 2) {
				continue;
			}
			words.add(term.getName());
		}
		return words;
	}

	public static void printMap(String message, Map<String, Double> datas) {
		System.out.println("===== " + message + " =====");
		if (datas == null || datas.isEmpty()) {
			return;
		}
		for (String key : datas.keySet()) {
			System.out.println(key + "\t\t" + datas.get(key));
		}
	}

}