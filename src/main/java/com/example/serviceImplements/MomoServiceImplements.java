package com.example.serviceImplements;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.entity.Order;
import com.example.service.MomoService;
import com.example.service.OrderService;

@Service
public class MomoServiceImplements implements MomoService {
    private final RestTemplate restTemplate; private final OrderService orderService;
    private final String url, partnerCode, accessKey, secretKey, baseUrl, ipnUrl;
    public MomoServiceImplements(RestTemplate restTemplate, OrderService orderService,
            @Value("${app.payment.momo.url}") String url, @Value("${app.payment.momo.partner-code:}") String partnerCode,
            @Value("${app.payment.momo.access-key:}") String accessKey, @Value("${app.payment.momo.secret-key:}") String secretKey,
            @Value("${app.payment.base-url}") String baseUrl, @Value("${app.payment.momo.ipn-url}") String ipnUrl) {
        this.restTemplate=restTemplate; this.orderService=orderService; this.url=url; this.partnerCode=partnerCode;
        this.accessKey=accessKey; this.secretKey=secretKey; this.baseUrl=baseUrl; this.ipnUrl=ipnUrl;
    }
    @Override public String createPaymentUrl(Order order) {
        if (partnerCode.isBlank() || accessKey.isBlank() || secretKey.isBlank()) throw new IllegalStateException("MoMo chưa được cấu hình");
        String requestId=partnerCode+"_"+order.getId()+"_"+UUID.randomUUID(), orderId=String.valueOf(order.getId());
        String amount=String.valueOf(order.getTotal_price().longValue()), info="Thanh toan don hang #"+order.getId(), extra="", type="captureWallet";
        String redirect=baseUrl+"/payment-result";
        String raw="accessKey="+accessKey+"&amount="+amount+"&extraData="+extra+"&ipnUrl="+ipnUrl+"&orderId="+orderId+"&orderInfo="+info+"&partnerCode="+partnerCode+"&redirectUrl="+redirect+"&requestId="+requestId+"&requestType="+type;
        Map<String,Object> body=new LinkedHashMap<>(); body.put("partnerCode",partnerCode); body.put("requestId",requestId); body.put("amount",amount); body.put("orderId",orderId); body.put("orderInfo",info); body.put("redirectUrl",redirect); body.put("ipnUrl",ipnUrl); body.put("lang","vi"); body.put("extraData",extra); body.put("requestType",type); body.put("signature",hmac(secretKey,raw));
        HttpHeaders headers=new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
        Map<?,?> response=restTemplate.postForObject(url,new HttpEntity<>(body,headers),Map.class);
        if(response==null || response.get("payUrl")==null) throw new IllegalStateException("MoMo không trả về payUrl");
        return String.valueOf(response.get("payUrl"));
    }
    @Override public boolean verifyCallback(Map<String,String> p) {
        if(secretKey.isBlank() || !p.containsKey("signature")) return false;
        String raw="accessKey="+accessKey+"&amount="+v(p,"amount")+"&extraData="+v(p,"extraData")+"&message="+v(p,"message")+"&orderId="+v(p,"orderId")+"&orderInfo="+v(p,"orderInfo")+"&orderType="+v(p,"orderType")+"&partnerCode="+v(p,"partnerCode")+"&payType="+v(p,"payType")+"&requestId="+v(p,"requestId")+"&responseTime="+v(p,"responseTime")+"&resultCode="+v(p,"resultCode")+"&transId="+v(p,"transId")+"&userFee="+v(p,"userFee")+"&userFeeAmount="+v(p,"userFeeAmount");
        return MessageDigest.isEqual(hmac(secretKey,raw).getBytes(StandardCharsets.UTF_8),p.get("signature").getBytes(StandardCharsets.UTF_8));
    }
    private String v(Map<String,String> p,String k){return p.getOrDefault(k,"");}
    @Override public void markPaymentResult(Map<String,String> p){ if(!verifyCallback(p)) return; try{orderService.findById(Integer.valueOf(p.get("orderId"))).ifPresent(o->{o.setStatus("0".equals(p.get("resultCode"))?"paid":"payment_failed");orderService.save(o);});}catch(NumberFormatException ignored){} }
    private String hmac(String key,String data){try{Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));StringBuilder b=new StringBuilder();for(byte x:mac.doFinal(data.getBytes(StandardCharsets.UTF_8)))b.append(String.format("%02x",x));return b.toString();}catch(Exception e){throw new IllegalStateException("Không tạo được chữ ký MoMo",e);}}
}
