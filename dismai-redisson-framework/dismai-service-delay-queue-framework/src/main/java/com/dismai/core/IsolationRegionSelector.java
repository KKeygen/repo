package com.dismai.core;

import java.util.concurrent.atomic.AtomicInteger;

public class IsolationRegionSelector {

	private final AtomicInteger count = new AtomicInteger(0);

	private final Integer thresholdValue;

	public IsolationRegionSelector(Integer thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public synchronized int getIndex() {
		int cur = count.getAndUpdate(c -> c >= thresholdValue - 1 ? 0 : c + 1);
		return cur;
	}
}
