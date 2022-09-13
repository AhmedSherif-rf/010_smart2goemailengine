package com.ntg.engine.service;

import java.util.List;
import java.util.Map;

public interface RecurringService {

	public List<Map<String, Object>> findObjectsDependOnRecurringType(Long recurringType, Long recurringValue, Long recurringUnit, Long scheduleID,
			String tableName, String query);

	public void saveScheduleJobHistor(Long objectId, Long typeId, Long scheduleId, String mail, String tableName, Long milestoneI);
}
