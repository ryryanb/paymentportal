package com.moadbus.web.biller.dto;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseList<T> implements DataList<T> {
	
	protected long total;	
	private List<T> rows = new ArrayList<T>();
	
	@Override
	public long getTotal() {
		return this.total;
	}
	
	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public List<T> getRows() {
		return rows;
	}

	public void addRow(T row) {
		this.rows.add(row);
	}
	
	public void addAllRows(List<T> rows) {
		this.rows.addAll(rows);
	}
}
