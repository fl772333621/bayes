package com.mfanw.test.bayes.utils;

import java.util.Map;

public class PrintUtils {

	public static void printMap(String message, Map<String, Double> datas) {
		System.out.println("===== " + message + " =====");
		if (datas == null || datas.isEmpty()) {
			return;
		}
		int count = 0;
		for (String key : datas.keySet()) {
			System.out.println(key + "\t\t" + datas.get(key));
			if (count++ > 100) {
				System.out.println("总长度为" + datas.size() + "，仅输出Top100......");
				break;
			}
		}
		System.out.println("");
	}
}
