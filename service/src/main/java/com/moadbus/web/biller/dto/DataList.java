package com.moadbus.web.biller.dto;

import java.util.List;

public interface DataList<T> {
	public long getTotal();

	public List<T> getRows();
}
