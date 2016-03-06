package com.meiah.po;


/**
 * 七大类实体类。包含验证是否符合的方法。
 * 
 * @author hepl
 * @date 2010-04-20
 * 
 */
public class KwScore {
	private Integer id;
	private String taskid;
	private String tid;
	private Integer sortid;
	private String kwlist;
	private Integer score;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getKwlist() {
		return kwlist;
	}
	public void setKwlist(String kwlist) {
		this.kwlist = kwlist;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Integer getSortid() {
		return sortid;
	}
	public void setSortid(Integer sortid) {
		this.sortid = sortid;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	
}
