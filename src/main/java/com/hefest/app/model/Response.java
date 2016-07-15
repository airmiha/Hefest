package com.hefest.app.model;

import java.util.List;

public class Response<T> {

	private List<Object> metadata;
	private List<T> data;
	private Integer total;
	
	public Response(List<Object> metadata, List<T> data) {
		this(metadata, data, null);
	}
	
	public Response(List<Object> metadata, List<T> data, Integer total) {
		this.metadata = metadata;
		this.data = data;
		this.total = total;
	}
	public List<Object> getMetadata() {
		return metadata;
	}
	public List<T> getData() {
		return data;
	}
	
	public Integer getTotal() {
		return total;
	}
}
