package com.hefest.app.model;

import java.util.List;

public class MultiResponse <T extends Object> {

	private List<List<T>> metadata;
	private List<List<List<T>>> data;
	
	private Integer total;

	public MultiResponse(List<List<T>> metadata, List<List<List<T>>> data) {
		this.metadata = metadata;
		this.data = data;
	}
	
	public MultiResponse(List<List<T>> metadata, List<List<List<T>>> data, Integer total) {
		this.metadata = metadata;
		this.data = data;
		this.total = total;
	}

	public List<List<T>> getMetadata() {
		return metadata;
	}

	public List<List<List<T>>> getData() {
		return data;
	}
	
	public Integer getTotal() {
		return total;
	}
}
