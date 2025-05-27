package com.example.service;

public interface QRCodeService {

	byte[] generateQRCode(String content) throws Exception;
	
}
