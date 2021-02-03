package com.projteam.app.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Initializable
{
	public void initialize();
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void initialize(Object... objs)
	{
		for (Object obj: objs)
		{
			if (obj instanceof Initializable)
				((Initializable) obj).initialize();
			else if (obj instanceof Collection)
				initialize(((Collection) obj).toArray());
			else if (obj instanceof Map)
			{
				Map m = (Map) obj;
				initialize(m.keySet().toArray());
				initialize(m.values().toArray());
			}
			else if (obj instanceof Iterable)
			{
				List<Object> list = new ArrayList<>();
				((Iterable) obj).forEach(list::add);
				initialize(list.toArray());
			}
		}
	}
	public static <T extends Initializable> T init(T obj)
	{
		obj.initialize();
		return obj;
	}
}
