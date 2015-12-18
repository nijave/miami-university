package venengnj.models;

import java.io.IOException;
import java.util.UUID;
import venengnj.handlers.*;

public class RestKey {
	private String key;
	private int valid = -1;
	
	public RestKey() throws IOException {
		key = UUID.randomUUID().toString();
		valid = 1;
		try {
			boolean res = (new MySQL_Controller()).addRestKey(key);
			if(!res)
				throw new IOException();
		} catch (IOException e) {
			throw new IOException("Could not add rest key to database");
		}
	}
	
	public RestKey(String key) {
		this.key = key;
	}
	
	/**
	 * Checks to see if a key is valid (exists and was issued in the last hour)
	 * @return whether the key is valid
	 */
	public boolean validate() {
		if(valid == -1) {
			try {
				boolean res = (new MySQL_Controller()).validateRestKey(this.getKey());
				if(res)
					valid = 1;
			} catch (IOException e) {
				Logger.log("ERR: Could not validate: " + this.getKey());
			}
		}
		return valid == 1;
	}
	
	public String getKey() {
		return key;
	}
}
