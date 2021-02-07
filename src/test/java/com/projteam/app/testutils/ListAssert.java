package com.projteam.app.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ListAssert
{
	public static <T, U, V> void assertListContentEquals(
			List<T> list1, List<U> list2,
			Function<T, V> elemMapper1, Function<U, V> elemMapper2)
	{
		assertEquals(list1.size(), list2.size());
		Iterator<T> it1 = list1.iterator();
		Iterator<U> it2 = list2.iterator();
		while (it1.hasNext() && it2.hasNext())
		{
			assertEquals(
					elemMapper1.apply(it1.next()),
					elemMapper2.apply(it2.next()));
		}
		assertFalse(it1.hasNext());
		assertFalse(it2.hasNext());
	}
	public static <T, U, V> void assertListContentMatches(
			List<T> list1, List<U> list2,
			BiPredicate<T, U> predicate)
	{
		assertEquals(list1.size(), list2.size());
		Iterator<T> it1 = list1.iterator();
		Iterator<U> it2 = list2.iterator();
		while (it1.hasNext() && it2.hasNext())
			assertTrue(predicate.test(it1.next(), it2.next()));
		
		assertFalse(it1.hasNext());
		assertFalse(it2.hasNext());
	}
	public static <T, U, V> void assertListContentMatches(
			List<T> list1, List<U> list2,
			BiConsumer<T, U> matcher)
	{
		assertEquals(list1.size(), list2.size());
		Iterator<T> it1 = list1.iterator();
		Iterator<U> it2 = list2.iterator();
		while (it1.hasNext() && it2.hasNext())
			matcher.accept(it1.next(), it2.next());
		
		assertFalse(it1.hasNext());
		assertFalse(it2.hasNext());
	}
}
