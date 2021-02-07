package com.projteam.app.utils;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class InitializableTests
{
	@Test
	public void canInitializeInitializable()
	{
		Initializable obj = Mockito.mock(Initializable.class);
		assertDoesNotThrow(() -> Initializable.initialize(obj));
	}
	@Test
	public void canInitializeCollection()
	{
		Collection<Object> col = Collections.emptyList();
		assertDoesNotThrow(() -> Initializable.initialize(col));
	}
	@Test
	public void canInitializeMap()
	{
		Map<Object, Object> map = Collections.emptyMap();
		assertDoesNotThrow(() -> Initializable.initialize(map));
	}
	@Test
	public void canInitializeIterable()
	{
		Iterable<Object> it = () -> Collections.emptyIterator();
		assertDoesNotThrow(() -> Initializable.initialize(it));
	}
	
	@Test
	public void canInitInitializable()
	{
		Initializable obj = Mockito.mock(Initializable.class);
		assertDoesNotThrow(() -> Initializable.init(obj));
	}
}
