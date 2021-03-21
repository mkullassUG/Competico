package com.projteam.app.utils;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

class OffsetBasedPageRequestTests
{
	@ParameterizedTest
	@ValueSource(ints = {-9999 -500, -2, -1})
	public void constructorWithNegativeOffsetThrows(int offset)
	{
		assertThrows(IllegalArgumentException.class, () ->
				OffsetBasedPageRequest.of(offset, 1, Sort.by(new String[0])));
	}
	@ParameterizedTest
	@ValueSource(ints = {-9999 -500, -2, -1, 0})
	public void constructorWithNonPositiveLimitThrows(int limit)
	{
		assertThrows(IllegalArgumentException.class, () ->
				OffsetBasedPageRequest.of(1, limit, Sort.by(new String[0])));
	}
	@Test
	public void canCreateRequestWithSort()
	{
		assertNotNull(OffsetBasedPageRequest.of(0, 5, Sort.by(new String[0])));
	}
	@Test
	public void canCreateRequestWithoutSort()
	{
		assertNotNull(OffsetBasedPageRequest.of(0, 5, null));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 5, 8, 28, 150, 9999})
	public void canGetPageNumber(int offset)
	{
		int pageSize = 5;
		assertEquals(OffsetBasedPageRequest.of(offset, pageSize, null)
				.getPageNumber(), offset / pageSize);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 150, 9999})
	public void canGetOffset(int offset)
	{
		assertEquals(OffsetBasedPageRequest.of(offset, 5, null)
				.getOffset(), offset);
	}
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 150, 9999})
	public void canGetPageSize(int pageSize)
	{
		assertEquals(OffsetBasedPageRequest.of(0, pageSize, null)
				.getPageSize(), pageSize);
	}
	@ParameterizedTest
	@MethodSource("sorts")
	public void canGetSorts(Sort sort)
	{
		assertEquals(OffsetBasedPageRequest.of(0, 5, sort)
				.getSort(), sort);
	}
	@Test
	public void canGetNext()
	{
		assertNotNull(OffsetBasedPageRequest.of(0, 5, null).next());
	}
	@Test
	public void canGetPrevious()
	{
		assertNotNull(OffsetBasedPageRequest.of(0, 5, null).previous());
	}
	@Test
	public void canGetPreviousOrFirst()
	{
		assertNotNull(OffsetBasedPageRequest.of(0, 5, null).previousOrFirst());
	}
	@Test
	public void canGetFirst()
	{
		assertNotNull(OffsetBasedPageRequest.of(0, 5, null).first());
	}
	@Test
	public void canCheckIfHasPrevious()
	{
		assertDoesNotThrow(() -> OffsetBasedPageRequest.of(0, 5, null).hasPrevious());
	}
	
	@Test
	public void canConvertToString()
	{
		assertNotNull(OffsetBasedPageRequest.of(0, 5, null).toString());
	}
	
	//---Sources---
	
	public static List<Arguments> sorts()
	{
		return List.of(Arguments.of(Sort.by(new String[0])),
				Arguments.of(Sort.by(Order.asc("prop"))),
				Arguments.of(Sort.by(Order.desc("prop"))),
				Arguments.of((Sort) null));
	}
}
