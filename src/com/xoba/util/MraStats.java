package com.xoba.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * various statistical methods
 * 
 */
public class MraStats {

	private MraStats() {
	}

	public static double getPercentileOfValue(Collection<? extends Number> c, double value) {
		Number[] array = c.toArray(new Number[c.size()]);
		Arrays.sort(array);
		int index = -1;
		for (int i = 0; i < array.length && index < 0; i++) {
			if (value <= array[i].doubleValue()) {
				index = i;
			}
		}
		return 100.0 * index / (c.size() - 1);
	}

	public static double getPercentile(Collection<? extends Number> c, double fraction) {
		return getPercentiles(c, new double[] { fraction })[0];
	}

	public final static double[] getQuintiles(final Collection<? extends Number> c) {
		return getPercentiles(c, new double[] { 0, 0.2, 0.4, 0.6, 0.8, 1.0 });
	}

	public final static double[] getPercentiles(final Collection<? extends Number> c, List<Double> fractions) {
		double[] array1 = new double[fractions.size()];
		Double[] array2 = fractions.toArray(new Double[fractions.size()]);
		for (int i = 0; i < array2.length; i++) {
			array1[i] = array2[i];
		}
		return getPercentiles(c, array1);
	}

	public final static double[] getPercentiles(final Collection<? extends Number> c, double[] fractions) {

		final List<Double> list = new ArrayList<Double>();
		for (final Number n : c) {
			list.add(n.doubleValue());
		}
		Collections.sort(list);

		final double[] out = new double[fractions.length];

		for (int i = 0; i < fractions.length; i++) {
			final double n = fractions[i] * (list.size() - 1);
			final int a = (int) Math.floor(n);
			final int b = (int) Math.ceil(n);
			final double x = list.get(a);
			final double y = list.get(b);
			final double diff = y - x;
			final double frac = n - a;
			out[i] = x + frac * diff;
		}

		return out;
	}

	public static double medianBySort(Collection<? extends Number> numbers) {
		int n = numbers.size();
		if (n == 0) {
			return Double.NaN;
		}
		Number[] array = new Number[n];
		numbers.toArray(array);
		Arrays.sort(array);
		if (MraUtils.isEven(n)) {
			return (array[n / 2 - 1].doubleValue() + array[n / 2].doubleValue()) / 2.0;
		} else {
			return array[n / 2].doubleValue();
		}
	}

	public static double median(Collection<? extends Number> numbers) {
		return medianBySort(numbers);
	}

	public static final List<Double> removeNaNs(Collection<? extends Number> c) {
		List<Double> out = new LinkedList<Double>();
		for (Number n : c) {
			double v = n.doubleValue();
			if (!Double.isNaN(v)) {
				out.add(v);
			}
		}
		return out;
	}

	public static final List<Double> removeNaNsAndInfinites(Collection<? extends Number> c) {
		List<Double> out = new LinkedList<Double>();
		for (Number n : c) {
			double v = n.doubleValue();
			if (!(Double.isNaN(v) || Double.isInfinite(v))) {
				out.add(v);
			}
		}
		return out;
	}

	public static final List<Double> removeInfinites(Collection<? extends Number> c) {
		List<Double> out = new LinkedList<Double>();
		for (Number n : c) {
			double v = n.doubleValue();
			if (!Double.isInfinite(v)) {
				out.add(v);
			}
		}
		return out;
	}

	public static final double mean(Collection<? extends Number> c) {
		if (c.size() == 0) {
			return 0;
		}
		double sum = 0;
		for (final Number n : c) {
			sum += n.doubleValue();
		}
		return sum / c.size();
	}

	public static double sum(Collection<? extends Number> c) {
		double sum = 0;
		for (Number n : c) {
			sum += n.doubleValue();
		}
		return sum;
	}

	public static double abssum(Collection<? extends Number> c) {
		double sum = 0;
		for (Number n : c) {
			sum += Math.abs(n.doubleValue());
		}
		return sum;
	}

	public static double max(Collection<? extends Number> c) {
		if (c.size() == 0) {
			return 0;
		}
		double max = -Double.MAX_VALUE;
		for (Number n : c) {
			if (n.doubleValue() > max) {
				max = n.doubleValue();
			}
		}
		return max;
	}

	public static double min(Collection<? extends Number> c) {
		if (c.size() == 0) {
			return 0;
		}
		double min = Double.MAX_VALUE;
		for (Number n : c) {
			if (n.doubleValue() < min) {
				min = n.doubleValue();
			}
		}
		return min;
	}

	public static <T> T weightedRandomChoice(Map<T, ? extends Number> map, Random r) {
		double sum = sum(map.values());
		T out = null;
		double selection = sum * r.nextDouble();
		double leftBoundary = 0;
		for (T key : map.keySet()) {
			out = key;
			double rightBoundary = leftBoundary + map.get(key).doubleValue();
			if (selection >= leftBoundary && selection < rightBoundary) {
				return out;
			}
			leftBoundary = rightBoundary;
		}
		return out;
	}

	/**
	 * returns probability distribution
	 */
	public static SortedMap<Double, Double> getProbabilityDistribution(Collection<Double> numbers, int n) {
		SortedMap<Integer, Integer> counts = new TreeMap<Integer, Integer>();
		final double min = min(numbers);
		final double max = max(numbers);
		final double dx = (max - min) / n;
		for (Double d : numbers) {
			int bin = (int) ((d.doubleValue() - min) / dx);
			if (!counts.containsKey(bin)) {
				counts.put(bin, 0);
			}
			counts.put(bin, counts.get(bin) + 1);
		}
		final double size = numbers.size();
		SortedMap<Double, Double> out = new TreeMap<Double, Double>();
		for (Integer bin : counts.keySet()) {
			int count = counts.get(bin);
			out.put(bin * dx + min + dx / 2.0, count / size);
		}
		return out;
	}

	public static <T> T getKeyForMin(Map<T, ? extends Number> c) {
		double min = Double.MAX_VALUE;
		T minKey = null;
		for (T key : c.keySet()) {
			Number v = c.get(key);
			if (v.doubleValue() < min) {
				min = v.doubleValue();
				minKey = key;
			}
		}
		return minKey;
	}

	public static <T> T getKeyForMax(Map<T, ? extends Number> c) {
		double max = -Double.MAX_VALUE;
		T maxKey = null;
		for (T key : c.keySet()) {
			Number v = c.get(key);
			if (v.doubleValue() > max) {
				max = v.doubleValue();
				maxKey = key;
			}
		}
		return maxKey;
	}

	public static double variance(Collection<? extends Number> c) {
		if (c.size() < 2) {
			return 0;
		}
		double mean = mean(c);
		double sum = 0;
		for (Number n : c) {
			sum += Math.pow(n.doubleValue() - mean, 2);
		}
		return sum / (c.size() - 1.0);
	}

	public static final <T> double biasedCovariance(Map<T, ? extends Number> a, Map<T, ? extends Number> b) {
		double sum = 0;
		final double aMean = mean(a.values());
		final double bMean = mean(b.values());
		double n = 0;
		for (T key : a.keySet()) {
			if (a.containsKey(key) && b.containsKey(key)) {
				sum += ((a.get(key).doubleValue() - aMean) * (b.get(key).doubleValue() - bMean));
				n++;
			}
		}
		if (n == 0) {
			return 0;
		}
		return sum / n;
	}

	public static <T> double biasedCorrelation(Map<T, ? extends Number> a, Map<T, ? extends Number> b) {
		return biasedCovariance(a, b) / Math.sqrt(variance(a.values()) * variance(b.values()));
	}

	public static double standardDeviation(Collection<? extends Number> numbers) {
		return Math.sqrt(variance(numbers));
	}

	public static double stdev(Collection<? extends Number> numbers) {
		return Math.sqrt(variance(numbers));
	}

	public static double standardError(Collection<? extends Number> numbers) {
		return Math.sqrt(variance(numbers) / numbers.size());
	}

	public static <T> SortedMap<T, Double> sortedNormalize(SortedMap<T, Double> map) {
		return new TreeMap<T, Double>(normalize(map));
	}

	public static <T> Map<T, Double> normalize(Map<T, ? extends Number> map) {
		Map<T, Double> out = new HashMap<T, Double>();
		if (map.size() == 0) {
			return out;
		}
		double sum = sum(map.values());
		for (T key : map.keySet()) {
			out.put(key, map.get(key).doubleValue() / sum);
		}
		return out;
	}

	public static <A, B> Map<B, Integer> calculateHistogram(Map<A, B> map) {
		Map<B, Integer> out = new HashMap<B, Integer>();
		for (A key : map.keySet()) {
			B value = map.get(key);
			if (!out.containsKey(value)) {
				out.put(value, 0);
			}
			out.put(value, out.get(value) + 1);
		}
		return out;
	}

	public static double calcEffectiveNumber(double[] weights) {
		List<Double> list = new LinkedList<Double>();
		for (int i = 0; i < weights.length; i++) {
			list.add(weights[i]);
		}
		return calcEffectiveNumber(list);
	}

	public static double calcEffectiveNumber(Collection<? extends Number> weights) {
		double m1 = 0;
		double m2 = 0;
		for (Number y : weights) {
			double x = y.doubleValue();
			m1 += Math.abs(x);
			m2 += x * x;
		}
		return m1 * m1 / m2;
	}

}
