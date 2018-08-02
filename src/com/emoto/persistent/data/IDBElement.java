package com.emoto.persistent.data;

public interface IDBElement {
	public int getId();
	public void copyFrom(IDBElement elem);
}
