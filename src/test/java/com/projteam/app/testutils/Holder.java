package com.projteam.app.testutils;

import java.io.Serializable;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class Holder<T> implements Serializable
{
	private T heldObj;
	private GetterAnswer getterAnswer;
	private SetterAnswer setterAnswer;
	
	public Holder()
	{
		getterAnswer = new GetterAnswer();
		setterAnswer = new SetterAnswer();
	}
	
	public T getHeldObject()
	{
		return heldObj;
	}
	public void setHeldObject(T heldObj)
	{
		this.heldObj = heldObj;
	}
	
	public Answer<T> getterAnswer()
	{
		return getterAnswer;
	}
	public Answer<T> setterAnswer()
	{
		return setterAnswer;
	}
	
	private class GetterAnswer implements Serializable, Answer<T>
	{
		@Override
		public T answer(InvocationOnMock invocation) throws Throwable
		{
			return getHeldObject();
		}
	}
	private class SetterAnswer implements Serializable, Answer<T>
	{
		@Override
		public T answer(InvocationOnMock invocation) throws Throwable
		{
			setHeldObject(invocation.getArgument(0));
			return getHeldObject();
		}
	}
}
