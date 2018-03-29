package com.mfanw.test.bayes.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Read {

	/**
	 * 邮件存放的基准路径
	 */
	public static final String BASE_PATH = "D:\\Bayes\\chinese_email\\";

	/**
	 * 垃圾邮件语料
	 */
	public static final String SPAM_EMAIL_PATH = BASE_PATH + "ham\\";

	public static void main(String[] args) throws IOException {
		File parent = new File(SPAM_EMAIL_PATH);
		for (File child : parent.listFiles()) {
			String s = FileUtils.readFileToString(child, "gbk");
			FileUtils.writeStringToFile(new File("D:\\Bayes\\chinese_email\\nham\\" + child.getName() + ".txt"), s, "utf-8");
		}
	}

}
