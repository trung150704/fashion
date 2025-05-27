package com.example.serviceImplements;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.example.service.QRCodeService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

@Service
public class QRCodeServiceImplements implements QRCodeService {

    @Override
    public byte[] generateQRCode(String content) throws Exception {
        // Tạo QR code với kích thước 300x300
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Chuyển đổi BitMatrix thành ảnh PNG và ghi ra ByteArrayOutputStream
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        }
    }
}
