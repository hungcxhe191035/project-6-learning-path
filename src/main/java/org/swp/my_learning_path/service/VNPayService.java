package org.swp.my_learning_path.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.pay-url}")
    private String vnp_Url;

    public String createPaymentUrl(Long transactionId, long amount, String ipAddress, String returnUrl) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Nap tien vao vi fCourse - Giao dich ID: " + transactionId;
        String vnp_OrderType = "other";
        String vnp_TxnRef = String.valueOf(transactionId);
        
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay uses cents (x100)
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        List<String> activeFields = new ArrayList<>();
        for (String name : fieldNames) {
            String val = vnp_Params.get(name);
            if (val != null && !val.isEmpty()) {
                activeFields.add(name);
            }
        }

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < activeFields.size(); i++) {
            String fieldName = activeFields.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            
            hashData.append(fieldName).append('=').append(queryUrlEncode(fieldValue));
            query.append(queryUrlEncode(fieldName)).append('=').append(queryUrlEncode(fieldValue));
            
            if (i < activeFields.size() - 1) {
                hashData.append('&');
                query.append('&');
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnp_Url + "?" + queryUrl;
    }

    public boolean verifyCallback(Map<String, String> fields) {
        String vnp_SecureHash = fields.get("vnp_SecureHash");
        if (vnp_SecureHash == null) {
            return false;
        }

        Map<String, String> signFields = new HashMap<>(fields);
        signFields.remove("vnp_SecureHash");
        signFields.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(signFields.keySet());
        Collections.sort(fieldNames);

        List<String> activeFields = new ArrayList<>();
        for (String name : fieldNames) {
            String val = signFields.get(name);
            if (val != null && !val.isEmpty()) {
                activeFields.add(name);
            }
        }

        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < activeFields.size(); i++) {
            String fieldName = activeFields.get(i);
            String fieldValue = signFields.get(fieldName);
            
            hashData.append(fieldName).append('=').append(queryUrlEncode(fieldValue));
            
            if (i < activeFields.size() - 1) {
                hashData.append('&');
            }
        }

        String calculatedHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        return calculatedHash.equalsIgnoreCase(vnp_SecureHash);
    }

    private String queryUrlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.US_ASCII.toString());
        } catch (Exception e) {
            return "";
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
