package com.mfanw.test.bayes;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mfanw.test.bayes.utils.AnsjUtil;
import com.mfanw.test.bayes.utils.PrintUtils;

/**
 * <b>贝叶斯垃圾邮件检测<b>
 * <p>
 * 利用朴素贝叶斯原理检测垃圾邮件 - 主要利用邮件个数
 * </p>
 * 
 * @author mengwei
 */
public class BayesMailPredict2 {

	/**
	 * 正常邮件的个数
	 */
	public static long NORMAL_EMAIL_SIZE;
	/**
	 * 垃圾邮件的个数
	 */
	public static long SPAM_EMAIL_SIZE;

	/**
	 * 从给定的垃圾邮件、正常邮件语料中建立map <切出来的词,合计在多少个邮件中出现该词>
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
		// 统计全部词
		Set<String> allWords = Sets.newHashSet();
		for (File child : children) {
			String contents = FileUtils.readFileToString(child, "UTF-8");
			allWords.addAll(AnsjUtil.segment(contents));
		}
		for (File child : children) {
			String contents = FileUtils.readFileToString(child, "UTF-8");
			// 统计每一篇文章中不重复的词
			Set<String> words = Sets.newHashSet(AnsjUtil.segment(contents));
			for (String word : words) {
				if (allWords.contains(word)) {
					rates.put(word, rates.get(word) == null ? 1D : rates.get(word) + 1);
				}
			}
		}
		return rates;
	}

	/**
	 * 统计垃圾邮件中词综合正常邮件后的概率<br />
	 * 似然估计
	 */
	public Map<String, WordInfo> createPredictMap(Map<String, Double> spamRates, Map<String, Double> normalRates) {
		Map<String, WordInfo> preditRates = Maps.newHashMap();
		for (Iterator<String> it = spamRates.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			double normalCount = normalRates.get(key) == null ? 0D : normalRates.get(key);
			// 利用似然估计计算概率，应用平滑处理解决零概率问题
			double normalRate = (normalCount + 1) / (NORMAL_EMAIL_SIZE + 2);
			double spamRate = (spamRates.get(key) + 1) / (SPAM_EMAIL_SIZE + 2);
			// 将两个概率存储
			preditRates.put(key, new WordInfo(key, normalRate, spamRate));
		}
		return preditRates;
	}

	/**
	 * 给定邮件,分词,根据分词结果判断是垃圾邮件的概率<br />
	 */
	public void judgeMail(String filePath, Map<String, WordInfo> preditRates) throws Exception {
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
			double normalRates = 1.0;
			double spamRates = 1.0;
			for (String word : words) {
				if (preditRates.containsKey(word)) {
					WordInfo predit = preditRates.get(word);
					normalRates *= predit.getNormalRate();
					spamRates *= predit.getSpamRate();
				}
			}
			double probability = spamRates * Consts.SPAM_RATE / (spamRates * Consts.SPAM_RATE + normalRates * (1 - Consts.SPAM_RATE));
			if (probability > 0.5) {
				System.err.println(child.getName() + "\t 这是垃圾邮件 \t" + probability);
			} else {
				System.out.println(child.getName() + "\t 这是正常邮件 \t" + probability);
			}
		}
	}

	public void start() throws Exception {
		// 0、pre
		NORMAL_EMAIL_SIZE = new File(Consts.NORMAL_EMAIL_PATH).listFiles().length;
		SPAM_EMAIL_SIZE = new File(Consts.SPAM_EMAIL_PATH).listFiles().length;

		// 1、计算正常邮件语料的词频
		Map<String, Double> normalRates = createRateMap(Consts.NORMAL_EMAIL_PATH);
		PrintUtils.printMap("正常邮件语料", normalRates);

		// 2、计算垃圾邮件语料的词频
		Map<String, Double> spamRates = createRateMap(Consts.SPAM_EMAIL_PATH);
		PrintUtils.printMap("垃圾邮件语料", spamRates);

		// 3、应用bayes公式计算垃圾邮件中词对判定垃圾邮件的概率值
		Map<String, WordInfo> preditRates = createPredictMap(spamRates, normalRates);

		// 4、根据分词结果判断是垃圾邮件的概率
		judgeMail(Consts.TEST_EMAIL_PATH, preditRates);
	}

	public static void main(String[] args) throws Exception {
		new BayesMailPredict2().start();
	}

}