package com.ntg.engine.repository.customImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ntg.engine.repository.custom.SqlHelperDao;
import com.ntg.engine.util.Setting;

@Repository
public class SqlHelperDaoImpl implements SqlHelperDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedParamJdbcTemplate;
	
	private String createTableIFNotExist;

	@Autowired
	public void setDataSource(javax.sql.DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<Map<String, Object>> queryForList(String query) {
		List<Map<String, Object>> objs = jdbcTemplate.query(query, new ColumnMapRowMapper() {

			@Override
			protected String getColumnKey(String columnName) {
				return columnName.toLowerCase();
			}
		});
		return objs;
	}

	public List<Long> findAllScheduleNotRunnibgJobs() {
		return jdbcTemplate.queryForList("select recid from adm_schedule_conf where job_Running=0", Long.class);
	}

	@Override
	@Transactional
	public void updateScheduleJobsStatus(List<Long> recids) {
		Map<String, List<Long>> params = Collections.singletonMap("recids", recids);
		String sql = "update  adm_schedule_conf set job_Running=1 where recid in (:recids)";
		namedParamJdbcTemplate.update(sql, params);
	}

	@Override
	public List<Map<String, Object>> queryForList(String query, Object[] params) {
		List<Map<String, Object>> objs = jdbcTemplate.query(query, params, new ColumnMapRowMapper() {

			@Override
			protected String getColumnKey(String columnName) {
				return columnName.toLowerCase();
			}
		});
		return objs;
	}

	@Override
	public void updateTable(List<Long> recids, String sql) {
		Map<String, List<Long>> params = Collections.singletonMap("recids", recids);
		// String sql = "update adm_schedule_conf set job_Running=1 where recid in
		// (:recids)";
		namedParamJdbcTemplate.update(sql, params);
	}

	public long FtechRecID(String SeqnaceName) {
		long nextId = jdbcTemplate.queryForObject(this.SquanceFetchSql(SeqnaceName), Long.class);
		return nextId;
	}

	private String SquanceFetchSql(String SequanceName) {
		String Sql = "";
		if (this.getConnectionType() == 1) {// oracle
			Sql = "Select " +SequanceName+".NEXTVAL from dual";
		} else {// postgres syntax
			Sql = "select nextval('" + SequanceName + "') ";
		}
		return Sql;
	}

	public int getConnectionType() { // return 1 for oracle AND 2 FOR POSTGRES

		return (Setting.databasetype.indexOf("oracle") > -1) ? 1 : 2;
	}

	public String LimitSql(int limitSize) {
		String Sql = "";
		if (this.getConnectionType() == 1) {// oracle
			Sql = " rowNum <  " + limitSize;
		} else { // postgres syntax
			Sql = " Limit (" + limitSize + ")";
		}
		return Sql;
	}
	
	public String[] BuildCreateTableSql(String TableName, String TableFields, String PrimaryKeyField) {
		String[] createUdaTableQuery = new String[] { "", "" };
		if (this.getConnectionType() == 1) {// oracle
			createUdaTableQuery[0] = "declare" + "  eAlreadyExists exception;"
					+ "  pragma exception_init(eAlreadyExists, -00955);" + " begin"
					+ " execute immediate 'CREATE TABLE " + TableName + " ( " + TableFields
					+ ((PrimaryKeyField != null)
							? (", CONSTRAINT " + TableName + "_p PRIMARY KEY (" + PrimaryKeyField + ")") : "")
					+ ")';" + " exception when eAlreadyExists then" + " null;" + " end;";
			// Sequence Creation Syntax
			createUdaTableQuery[1] = "declare" + "  eAlreadyExists exception;"
					+ "  pragma exception_init(eAlreadyExists, -00955);" + " begin"
					+ " execute immediate 'CREATE SEQUENCE  " + TableName + "_s';"
					+ " exception when eAlreadyExists then" + " null;" + " end;";
		} else { // postgres syntax
			createUdaTableQuery[0] = createTableIFNotExist.replaceAll("TableName", TableName) + TableFields
					+ ((PrimaryKeyField != null)
							? (",CONSTRAINT " + TableName + "_p PRIMARY KEY (" + PrimaryKeyField + ")") : "")
					+ ")";
			// Sequence Creation Syntax
			createUdaTableQuery[1] = "CREATE SEQUENCE if not EXISTS " + TableName + "_s";
		}
		return createUdaTableQuery;
	}
	
	
}
