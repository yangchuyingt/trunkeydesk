package com.iiordanov.aSPICE.user;

import java.util.Map;

public class APIQueryVmInstanceReply  {
	private String success;
	private  ApiInventory[] inventories;
	
	public ApiInventory[] getInventories() {
		return inventories;
	}
	public void setInventories(ApiInventory[] inventories) {
		this.inventories = inventories;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}

	

}
