package com.mfanw.test.bayes.utils;

import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;

import com.google.common.collect.Lists;

public class AnsjUtil {

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
}
