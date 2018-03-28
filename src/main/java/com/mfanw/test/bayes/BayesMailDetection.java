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
		Map<String, Double> normalWFMap = createWordFrequencyMap(NORMAL_EMAIL_PATH);
		// 2、计算垃圾邮件语料的词频
		Map<String, Double> spamWFMap = createWordFrequencyMap(SPAM_EMAIL_PATH);
		// 3、应用bayes公式计算垃圾邮件中词对判定垃圾邮件的概率值
		Map<String, Double> wordRateMap = createSpamProbabilityMap(spamWFMap, normalWFMap);
		// 4、根据分词结果判断是垃圾邮件的概率
		judgeMail(TEST_EMAIL_PATH, wordRateMap);
	}

	/**
	 * 从给定的垃圾邮件、正常邮件语料中建立map <切出来的词,出现的频率>
	 */
	public static Map<String, Double> createWordFrequencyMap(String filePath) throws Exception {
		String contents = FileUtils.readFileToString(new File(filePath));
		List<String> list = segment(contents);
		Map<String, Integer> tmpmap = new HashMap<String, Integer>();
		Map<String, Double> retmap = new HashMap<String, Double>();
		double rate = 0.0;
		int count = 0;
		for (String s : list) {
			tmpmap.put(s, tmpmap.containsKey(s) ? count + 1 : 1);
		}
		for (Iterator<String> iter = tmpmap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			rate = tmpmap.get(key) / list.size();
			retmap.put(key, rate);
		}
		return retmap;
	}

	/**
	 * 应用bayes公式计算垃圾邮件中词对判定垃圾邮件的概率值<br />
	 * 邮件中出现word时<br />
	 * 该邮件为垃圾邮件的概率 P(Spam|word) =P(Spam)/P(word)*P(word|Spam)
	 */
	public static Map<String, Double> createSpamProbabilityMap(Map<String, Double> spamWFMap, Map<String, Double> normalWFMap) {
		Map<String, Double> resultMap = new HashMap<String, Double>();
		for (Iterator<String> it = spamWFMap.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			double rate = spamWFMap.get(key);
			double allRate = rate;
			if (normalWFMap.containsKey(key)) {
				allRate += normalWFMap.get(key);
			}
			resultMap.put(key, rate / allRate);
		}
		return resultMap;
	}

	/**
	 * 给定邮件,分词,根据分词结果判断是垃圾邮件的概率
	 * P(Spam|t1,t2,t3……tn)=（P1*P2*……PN）/(P1*P2*……PN+(1-P1)*(1-P2)*……(1-PN))
	 */
	public static void judgeMail(String emailPath, Map<String, Double> rateMap) throws Exception {
		List<String> words = segment(FileUtils.readFileToString(new File(emailPath)));
		double rate = 1.0;
		double tempRate = 1.0;
		for (String word : words) {
			if (rateMap.containsKey(word)) {
				double tmp = rateMap.get(word);
				tempRate *= 1 - tmp;
				rate *= tmp;
			}
		}
		double probability = rate / (rate + tempRate);
		if (probability > 0.5) {
			System.out.println("这是正常邮件");
		} else {
			System.out.println("这是垃圾邮件");
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
				words.add(term.getName());
			}
		}
		return words;
	}

}