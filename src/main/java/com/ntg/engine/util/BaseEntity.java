package com.ntg.engine.util;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
public class BaseEntity implements Serializable {

	@JsonView(View.Summary.class)
	@Column(name = "created_by_id")
	@XmlElement(required = false)
	private String createdById;

	@JsonView(View.Summary.class)
	@Column(name = "updated_by", nullable = true)
	@XmlElement(required = false)
	private String updatedBy;

	@JsonView(View.Summary.class)
	@Column(name = "updated_by_id", nullable = true)
	@XmlElement(required = false)
	private String updatedById;

	@JsonView(View.Summary.class)
	@Column(name = "created_by")
	@XmlElement(required = false)
	private String createdBy;

	@JsonView(View.Summary.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "created_date")
	@XmlElement(required = false)
	private Date createdDate;

	@JsonView(View.Summary.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_date", nullable = true)
	@XmlElement(required = false)
	private Date updatedDate;

	@JsonView(View.Summary.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "status_last_change_date", nullable = true)
	@XmlElement(required = false)
	private Date statusLastChangeDate;

	@JsonView(View.Summary.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "group_id", nullable = true)
	@XmlElement(required = false)
	private Long groupId;

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedById() {
		return updatedById;
	}

	public void setUpdatedById(String updatedById) {
		this.updatedById = updatedById;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getStatusLastChangeDate() {
		return statusLastChangeDate;
	}

	public void setStatusLastChangeDate(Date statusLastChangeDate) {
		this.statusLastChangeDate = statusLastChangeDate;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

}
