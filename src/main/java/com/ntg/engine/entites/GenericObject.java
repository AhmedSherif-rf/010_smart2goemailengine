package com.ntg.engine.entites;

import java.util.Date;
import java.util.List;


public class GenericObject {
	
	private String createdBy;

	private String createdById;

	private Date createdDate;

	private String name;

	private long recId;

	private long statusId;

	private long GenericObjectId;

	private String statusName;

	private Long typeId;

	private long parentTypeId;

	private long templateId;

	private String table_Name;

	private String typeName;

	private List<UDAsWithValues> udasValues;

	private String updatedBy;

	private String updatedbyById;

	private Long groupId = 0L;

	
	private String ruleMessage;

	private boolean ruleFaild;

	private boolean isDeleted;


	private boolean preventSave;

	private String $Date;

	private String $DateTime;

	private long rowIndex;


	private String addingError;
	
	private long[] needToBeModified;


	private String customError;
	

	public String getCreatedBy() {
		return createdBy;
	}

	public String getCreatedById() {
		return createdById;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getName() {
		return name;
	}

	public long getRecId() {
		return recId;
	}

	public long getStatusId() {
		return statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	
	public String get$Date() {
		return $Date;
	}

	public void set$Date(String $Date) {
		this.$Date = $Date;
	}

	public String get$DateTime() {
		return $DateTime;
	}

	public void set$DateTime(String $DateTime) {
		this.$DateTime = $DateTime;
	}

	public boolean isRuleFaild() {
		return ruleFaild;
	}

	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Long getTypeId() {
		return typeId;
	}

	public long getParentTypeId() {
		return parentTypeId;
	}

	public void setParentTypeId(long parentTypeId) {
		this.parentTypeId = parentTypeId;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getTypeName() {
		return typeName;
	}

	public List<UDAsWithValues> getUdasValues() {
		return udasValues;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public String getUpdatedbyById() {
		return updatedbyById;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRecId(long recId) {
		this.recId = recId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = (statusId ==null)?-1:statusId;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setUdasValues(List<UDAsWithValues> udasValues) {
		this.udasValues = udasValues;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setUpdatedbyById(String updatedbyById) {
		this.updatedbyById = updatedbyById;
	}


	public void setRuleMessage(String ruleMessage) {
		this.ruleMessage = ruleMessage;
	}

	public void setRuleFaild(boolean ruleFaild) {
		this.ruleFaild = ruleFaild;
	}

	public boolean isPreventSave() {
		return preventSave;
	}

	public void setPreventSave(boolean preventSave) {
		this.preventSave = preventSave;
	}

	public String getRuleMessage() {
		return ruleMessage;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getTable_Name() {
		return table_Name;
	}

	public void setTable_Name(String table_Name) {
		this.table_Name = table_Name;
	}

	public long getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(long rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String getAddingError() {
		return addingError;
	}

	public void setAddingError(String addingError) {
		this.addingError = addingError;
	}

	public long getGenericObjectId() {
		return GenericObjectId;
	}

	public void setGenericObjectId(long genericObjectId) {
		GenericObjectId = genericObjectId;
	}
	
	public long[] getNeedToBeModified() {
		return needToBeModified;
	}

	public void setNeedToBeModified(long[] needToBeModified) {
		this.needToBeModified = needToBeModified;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	public String getCustomError() {
		return customError;
	}

	public void setCustomError(String customError) {
		this.customError = customError;
	}

}
