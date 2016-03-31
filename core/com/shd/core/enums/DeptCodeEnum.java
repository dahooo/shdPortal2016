package com.shd.core.enums;

public enum DeptCodeEnum {

	DeptLevel50("50"), 
	DeptLevel60("60"), 
	DeptLevel70("70"), 
	DeptLevel80("80"), 
	DeptLevel90("90"),
	DeptLevel100("100"),
	DeptLevel110("110"); 
	
	
	private String index; 

	DeptCodeEnum(String idx) { 
        this.index = idx; 
    } 

    public String getIndex() { 
        return index; 
    }  
}
