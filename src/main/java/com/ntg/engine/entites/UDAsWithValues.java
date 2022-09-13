package com.ntg.engine.entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UDAsWithValues implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long recId = 0L;

	private String udaName;
	
	private Long udaType = 0L; // 1 number , 2 string ,
	
	private Long udaFormOption;

	private String udaTableName;
	

	private String visibleCondition;

	private String normalCondition;

	private String readOnlyCondition;

	private String isMandatoryCondition;

	private String udaPanelCaption;

	private Long udaPanelID;

	private String udaValue;

	private List<Attachment> attachemnts;

	private Map<String, Object> formObject;

	private List<HashMap<String, Object>> rowData = new ArrayList<HashMap<String, Object>>();

	private Long object_id = 0L;
	
	private Long udaOrder = 0L;

	private String udaCaption;

	private Long dateTypes = 0L;
	
	private String valueListCondition;

	private String valueListConditionDB;

	private Long sourceOperationId;
	
	private Long sourceOperationDataId;

	private Long bindDataId;

	private Long relatedValueListId;

	private UDAsWithValues repositoryUDA;

	private String calulatedUda;

	private String ai_Comp;

	private String ai_Orga;
	
	private String ai_Group;

	private String ai_Mem;
	
	private Boolean allowManual;
	
	private Boolean copyTemplate;
	
	private Boolean enableUdaLog;
	
	private Long visableType = 0L;
	
	private Long calculatedVisibleType;
	
	private Boolean allowReAssign;
	
	private Boolean privilegeIsPublic;

	private Long udaId;


	private String defaultValue;
	
	private Long typeId;
	
	private Boolean showMemper;

	private byte[] htextValue;
	
	private String autoIDPreFix;
	
	private Long autoIDNumDigits;
	

	public String getAutoIDPreFix() {
		return autoIDPreFix;
	}

	public void setAutoIDPreFix(String autoIDPreFix) {
		this.autoIDPreFix = autoIDPreFix;
	}

	public Long getAutoIDNumDigits() {
		return autoIDNumDigits;
	}

	public void setAutoIDNumDigits(Long autoIDNumDigits) {
		this.autoIDNumDigits = autoIDNumDigits;
	}

	public byte[] getHtextValue() {
		return htextValue;
	}

	public void setHtextValue(byte[] htextValue) {
		this.htextValue = htextValue;
	}

	public String getUdaTableName() {
		return udaTableName;
	}

	public void setUdaTableName(String udaTableName) {
		this.udaTableName = udaTableName;
	}

	public void setUdaType(Long udaType) {
		this.udaType = udaType;
	}

	public void setVisableType(Long visableType) {
		this.visableType = visableType;
	}

	public Boolean getShowMemper() {
		return showMemper;
	}

	public void setShowMemper(Boolean showMemper) {
		this.showMemper = showMemper;
	}

	public long getUdaPanelID() {
		return (udaPanelID == null) ? -1 : udaPanelID;
	}

	public void setUdaPanelID(Long udaPanelID) {
		this.udaPanelID = udaPanelID;
	}


	public Long getDateTypes() {
		if (dateTypes == null)
			dateTypes = 0L;
		return dateTypes;
	}

	public void setDateTypes(Long dateTypes) {
		this.dateTypes = dateTypes;
	}

	public String getUdaCaption() {
		return udaCaption;
	}

	public void setUdaCaption(String udaCaption) {
		this.udaCaption = udaCaption;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Long getUdaOrder() {
		return udaOrder;
	}

	public void setUdaOrder(Long udaOrder) {
		this.udaOrder = udaOrder;
	}



	public long getVisableType() {
		return visableType;
	}

	public void setVisableType(long visableType) {
		this.visableType = visableType;
	}


	public Long getObject_id() {
		if (object_id == null)
			object_id = 0L;
		return object_id;
	}

	public void setObject_id(Long object_id) {
		this.object_id = object_id;
	}




	public Long getRecId() {
		if (recId == null)
			recId = 0L;
		return recId;
	}

	public void setRecId(Long recId) {
		this.recId = recId;
	}

	public String getUdaName() {
		return udaName;
	}

	public void setUdaName(String udaName) {
		this.udaName = udaName;
	}

	public long getUdaType() {
		return udaType;
	}

	public void setUdaType(long udaType) {
		this.udaType = udaType;
	}

	public String getUdaPanelCaption() {
		return udaPanelCaption;
	}

	public void setUdaPanelCaption(String udaPanelCaption) {
		this.udaPanelCaption = udaPanelCaption;
	}

	public String getUdaValue() {
		return udaValue;
	}

	public void setUdaValue(String udaValue) {
		this.udaValue = udaValue;
	}

	public Map<String, Object> getFormObject() {
		if (formObject == null)
			formObject = new HashMap<String, Object>();
		return formObject;
	}

	public void setFormObject(Map<String, Object> formObject) {
		this.formObject = formObject;
	}

	public List<Attachment> getAttachemnts() {
		return attachemnts;
	}

	public void setAttachemnts(List<Attachment> attachemnts) {
		this.attachemnts = attachemnts;
	}

	public List<HashMap<String, Object>> getRowData() {
		return rowData;
	}

	public void setRowData(List<HashMap<String, Object>> rowData) {
		this.rowData = rowData;
	}


	public Long getUdaFormOption() {
		return udaFormOption;
	}

	public void setUdaFormOption(Long udaFormOption) {
		this.udaFormOption = udaFormOption;
	}

	public String getVisibleCondition() {
		return visibleCondition;
	}

	public void setVisibleCondition(String visibleCondition) {
		this.visibleCondition = visibleCondition;
	}

	public Long getCalculatedVisibleType() {
		return calculatedVisibleType;
	}

	public void setCalculatedVisibleType(Long calculatedVisibleType) {
		this.calculatedVisibleType = calculatedVisibleType;
	}

	public String getNormalCondition() {
		return normalCondition;
	}

	public void setNormalCondition(String normalCondition) {
		this.normalCondition = normalCondition;
	}

	public String getReadOnlyCondition() {
		return readOnlyCondition;
	}

	public void setReadOnlyCondition(String readOnlyCondition) {
		this.readOnlyCondition = readOnlyCondition;
	}

	public String getIsMandatoryCondition() {
		return isMandatoryCondition;
	}

	public void setIsMandatoryCondition(String isMandatoryCondition) {
		this.isMandatoryCondition = isMandatoryCondition;
	}

	private String valiedCondition;

	public String getValiedCondition() {
		return valiedCondition;
	}

	public void setValiedCondition(String valiedCondition) {
		this.valiedCondition = valiedCondition;
	}

	public String getAddedMessage() {
		return addedMessage;
	}

	public void setAddedMessage(String addedMessage) {
		this.addedMessage = addedMessage;
	}


	private String addedMessage;

	public String getValueListCondition() {
		return valueListCondition;
	}

	public void setValueListCondition(String valueListCondition) {
		this.valueListCondition = valueListCondition;
	}

	public Long getSourceOperationId() {
		return sourceOperationId;
	}

	public void setSourceOperationId(Long sourceOperationId) {
		this.sourceOperationId = sourceOperationId;
	}

	public Long getSourceOperationDataId() {
		return sourceOperationDataId;
	}

	public void setSourceOperationDataId(Long sourceOperationDataId) {
		this.sourceOperationDataId = sourceOperationDataId;
	}

	public Long getBindDataId() {
		return bindDataId;
	}

	public void setBindDataId(Long bindDataId) {
		this.bindDataId = bindDataId;
	}

	public String getValueListConditionDB() {
		return valueListConditionDB;
	}

	public void setValueListConditionDB(String valueListConditionDB) {
		this.valueListConditionDB = valueListConditionDB;
	}

	public Long getRelatedValueListId() {
		return relatedValueListId;
	}

	public void setRelatedValueListId(Long relatedValueListId) {
		this.relatedValueListId = relatedValueListId;
	}

	public UDAsWithValues getRepositoryUDA() {
		return repositoryUDA;
	}

	public void setRepositoryUDA(UDAsWithValues repositoryUDA) {
		this.repositoryUDA = repositoryUDA;
	}

	public String getCalulatedUda() {
		return calulatedUda;
	}

	public void setCalulatedUda(String calulatedUda) {
		this.calulatedUda = calulatedUda;//
	}

	public String getAi_Comp() {
		return ai_Comp;
	}

	public void setAi_Comp(String ai_Comp) {
		this.ai_Comp = ai_Comp;
	}

	public String getAi_Orga() {
		return ai_Orga;
	}

	public void setAi_Orga(String ai_Orga) {
		this.ai_Orga = ai_Orga;
	}

	public String getAi_Group() {
		return ai_Group;
	}

	public void setAi_Group(String ai_Group) {
		this.ai_Group = ai_Group;
	}

	public String getAi_Mem() {
		return ai_Mem;
	}

	public void setAi_Mem(String ai_Mem) {
		this.ai_Mem = ai_Mem;
	}

	public Boolean getAllowManual() {
		return allowManual;
	}

	public void setAllowManual(Boolean allowManual) {
		this.allowManual = allowManual;
	}

	public Boolean getCopyTemplate() {
		return copyTemplate;
	}

	public void setCopyTemplate(Boolean copyTemplate) {
		this.copyTemplate = copyTemplate;
	}

	
	public Boolean getEnableUdaLog() {
		return (enableUdaLog == null) ? false : enableUdaLog;
	}

	public void setEnableUdaLog(Boolean enableUdaLog) {
		this.enableUdaLog = enableUdaLog;
	}

	public Boolean getAllowReAssign() {
		return allowReAssign;
	}

	public void setAllowReAssign(Boolean allowReAssign) {
		this.allowReAssign = allowReAssign;
	}

	public Boolean getPrivilegeIsPublic() {
		return privilegeIsPublic;
	}

	public void setPrivilegeIsPublic(Boolean privilegeIsPublic) {
		this.privilegeIsPublic = privilegeIsPublic;
	}

	public Long getUdaId() {
		return (udaId == null) ? -1 : udaId;
	}

	public void setUdaId(Long udaId) {
		this.udaId = udaId;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

}
