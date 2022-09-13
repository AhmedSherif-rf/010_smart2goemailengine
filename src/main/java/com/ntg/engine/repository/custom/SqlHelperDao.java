package com.ntg.engine.repository.custom;

import java.util.List;
import java.util.Map;

//import com.ntg.engine.entites.SlaMilestones;

public interface SqlHelperDao {

	public List<Map<String, Object>> queryForList(String query);

	public List<Map<String, Object>> queryForList(String query, Object[] params);

	public List<Long> findAllScheduleNotRunnibgJobs();

	public void updateScheduleJobsStatus(List<Long> recids);

	void updateTable(List<Long> recids, String sql);

//	public void saveMilestonesOnObject(Long incedentCustomer, Long imsla, Double percentage, SlaMilestones slaMilestones);
}
