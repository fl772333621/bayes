package com.mfanw.test.bayes;

public class WordInfo {
	/**
	 * 词
	 */
	private String word;
	/**
	 * 该词在正常邮件的概率
	 */
	private double normalRate;
	/**
	 * 该词在垃圾邮件的概率
	 */
	private double spamRate;

	public WordInfo(String word, double normalRate, double spamRate) {
		super();
		this.word = word;
		this.normalRate = normalRate;
		this.spamRate = spamRate;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public double getNormalRate() {
		return normalRate;
	}

	public void setNormalRate(double normalRate) {
		this.normalRate = normalRate;
	}

	public double getSpamRate() {
		return spamRate;
	}

	public void setSpamRate(double spamRate) {
		this.spamRate = spamRate;
	}

}