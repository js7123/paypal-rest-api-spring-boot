package com.js.springbootpaypalapi.controller;

import com.js.springbootpaypalapi.PayPalHttpClient;
import com.js.springbootpaypalapi.dao.Order;
import com.js.springbootpaypalapi.dao.OrderDAO;
import com.js.springbootpaypalapi.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/checkout")
@Slf4j
public class CheckoutController {
    private final PayPalHttpClient payPalHttpClient;
    private final OrderDAO orderDAO;

    @Autowired
    public CheckoutController(PayPalHttpClient payPalHttpClient, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.payPalHttpClient = payPalHttpClient;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderDTO orderDTO) throws Exception {
        var appContext = new PayPalAppContextDTO();
        appContext.setReturnURL("http://localhost:8989/checkout/success");
        appContext.setBrandName("My brand");
        appContext.setLandingPage(PaymentLandingPageEnum.BILLING);
        orderDTO.setApplicationContext(appContext);
        var orderResponse = payPalHttpClient.createOrder(orderDTO);

        var entity = new Order();
        entity.setPaypalOrderId(orderResponse.getId());
        entity.setPaypalOrderStatus(orderResponse.getStatus().toString());
        var out = orderDAO.save(entity);
        log.info("Saved order: {}", out);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(value = "/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) {
        var orderId = request.getParameter("token");
        var out = orderDAO.findByPaypalOrderId(orderId);
        out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
        orderDAO.save(out);
        return ResponseEntity.ok().body("Payment success");
    }
}
