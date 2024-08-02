package com.PayVang.Mobile.Models;

public class EncryptedKeyGenericResponse {

	public String encryptedKey;
	public String comment;
	public String getEncryptedKey() {
		return encryptedKey;
	}
	public EncryptedKeyGenericResponse(String encryptedKey, String comment)
	{
		this.encryptedKey = encryptedKey;
		this.comment = comment;
	}
	public void setEncryptedKey(String encryptedKey) {
		this.encryptedKey = encryptedKey;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
