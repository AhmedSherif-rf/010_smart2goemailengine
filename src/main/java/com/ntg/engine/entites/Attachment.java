package com.ntg.engine.entites;

import java.util.Date;

public class Attachment {

	private Long recid = 0L;


	private String attachmentName;

	private String attachmentNote;
	
	private Long objectid = 0L;


	private String attachmentUrl;

	
	private Long udaID = 0L;

	
	private Double size = 0.0;

	private String uploadedby;

	private Long uploadedbyid = 0L;

	
	private Date date;

	
	private Integer attachmenttype = 0;


	private Boolean ispublic;


	public long getObjectid() {
		return objectid;
	}

	public void setObjectid(long objectid) {
		this.objectid = objectid;
	}


	public String getUploadedby() {
		return uploadedby;
	}

	public void setUploadedby(String uploadedby) {
		this.uploadedby = uploadedby;
	}

	public String getAttachmentNote() {
		return attachmentNote;
	}

	public void setAttachmentNote(String attachmentNote) {
		this.attachmentNote = attachmentNote;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public long getUdaID() {
		return udaID;
	}

	public void setUdaID(long udaID) {
		this.udaID = udaID;
	}

	public Long getRecid() {
		if (recid == null)
			recid = 0L;
		return recid;
	}

	public void setRecid(Long recid) {
		this.recid = recid;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public Boolean getIspublic() {
		return ispublic;
	}

	public void setIspublic(Boolean ispublic) {
		this.ispublic = ispublic;
	}

	public Double getSize() {
		if (size == null)
			size = 0.0;
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public Integer getAttachmenttype() {
		if (attachmenttype == null)
			attachmenttype = 0;
		return attachmenttype;
	}

	public void setAttachmenttype(Integer attachmenttype) {
		this.attachmenttype = attachmenttype;
	}



	public Long getUploadedbyid() {
		if (uploadedbyid == null)
			uploadedbyid = 0L;
		return uploadedbyid;
	}

	public void setUploadedbyid(Long uploadedbyid) {
		this.uploadedbyid = uploadedbyid;
	}

	

}
