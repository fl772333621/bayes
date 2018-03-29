package com.mfanw.test.bayes.utils;

import java.util.Map;

public class PrintUtils {

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
