package com.mfanw.test.bayes.utils;

import java.io.File;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class WhiteNum {

	private static Random random = new Random();

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 10; i++) {
			File child = new File("D:\\bayes\\src\\main\\resources\\num_email\\ham\\ham_0000" + i + ".txt");
			FileUtils.writeStringToFile(child, writeEmail());
		}
		for (int i = 0; i < 10; i++) {
			File child = new File("D:\\bayes\\src\\main\\resources\\num_email\\spam\\spam_0000" + i + ".txt");
			FileUtils.writeStringToFile(child, writeEmail());
		}
		for (int i = 0; i < 10; i++) {
			File child = new File("D:\\bayes\\src\\main\\resources\\num_email\\test\\test_0000" + i + ".txt");
			FileUtils.writeStringToFile(child, writeEmail());
		}
	}

	public static String writeEmail() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			sb.append(" 0" + random.nextInt(10));
		}
		return sb.toString();
	}
}
