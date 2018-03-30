package com.mfanw.test.bayes;

public class Consts {
	/**
	 * 邮件存放的基准路径 "D:\\bayes\\src\\main\\resources\\chinese_email\\";
	 */
	public static final String BASE_PATH = "D:\\bayes\\src\\main\\resources\\chinese_email\\";
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

	/**
	 * 垃圾邮件先验概率
	 */
	public static double SPAM_RATE = 0.1D;
}
