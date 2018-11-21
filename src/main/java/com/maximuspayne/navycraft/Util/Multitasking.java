package com.maximuspayne.navycraft.Util;

import java.util.ArrayList;
import java.util.List;


public class Multitasking {

	public static int CORE_COUNT = Runtime.getRuntime().availableProcessors();

	public static <B> List<ListToObject<B>> spiltList(List<B> object, int split) {
		List<ListToObject<B>> objects = new ArrayList<ListToObject<B>>();
		for (int A = 0; A < split; A++) {
			List<B> list = new ArrayList<B>();
			int devide = object.size() / split;
			int start = devide * (A);
			int end = start + devide;
			if (A == (split - 1)) {
				end = (object.size() - 1);
			}
			for (int C = start; C <= end; C++) {
				list.add(object.get(C));
			}
			objects.add(new ListToObject<B>(list));

		}
		return objects;
	}

	public static class ListToObject<A> {
		List<A> OBJECT;

		public ListToObject(List<A> object) {
			OBJECT = object;
		}

		public List<A> getList() {
			return OBJECT;
		}

	}

}