package com.projteam.competico.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class OffsetBasedPageRequest implements Pageable
{
	private int pageSize;
	private long offset;
	private final Sort sort;
	
	private OffsetBasedPageRequest(long offset, int limit, Sort sort)
	{
		if (offset < 0)
			throw new IllegalArgumentException("Offset index must not be less than zero!");

		if (limit < 1)
			throw new IllegalArgumentException("Limit must not be less than one!");
		
		this.pageSize = limit;
		this.offset = offset;
		this.sort = sort;
	}
	
	public static OffsetBasedPageRequest of(long offset, int limit, Sort sort)
	{
		return new OffsetBasedPageRequest(offset, limit, sort);
	}
	
	@Override
	public int getPageNumber()
	{
		return (int) (offset / pageSize);
	}
	@Override
	public Pageable next()
	{
		return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
	}
	public OffsetBasedPageRequest previous()
	{
		return hasPrevious() ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
	}
	@Override
	public Pageable previousOrFirst()
	{
		return hasPrevious() ? previous() : first();
	}
	@Override
	public Pageable first()
	{
		return new OffsetBasedPageRequest(0, getPageSize(), getSort());
	}
	@Override
	public boolean hasPrevious()
	{
		return offset > pageSize;
	}
}