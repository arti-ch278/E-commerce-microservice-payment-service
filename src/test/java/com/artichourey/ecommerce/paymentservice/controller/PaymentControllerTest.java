package com.artichourey.ecommerce.paymentservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.artichourey.ecommerce.paymentservice.dto.PaymentStatusUpdateRequest;
import com.artichourey.ecommerce.paymentservice.dto.RequestPayment;
import com.artichourey.ecommerce.paymentservice.dto.ResponsePayment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentMethod;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import com.artichourey.ecommerce.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    void createPayment_ValidRequest_ShouldReturn201() throws Exception {
        RequestPayment request = RequestPayment.builder()
                .orderId("ORD123")
                .amount(new BigDecimal("499.99"))
                .paymentMethod(PaymentMethod.UPI)
                .userId(1L)
                .build();

        ResponsePayment response = ResponsePayment.builder()
                .paymentId(1L)
                .orderId("ORD123")
                .amount(new BigDecimal("499.99"))
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId("TXN123")
                .build();

        when(paymentService.createPayment(any(RequestPayment.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // ✅ now works
                .andExpect(jsonPath("$.orderId").value("ORD123"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    void getByOrderID_ExistingOrder_ShouldReturn200() throws Exception {
        ResponsePayment response = ResponsePayment.builder()
                .paymentId(1L)
                .orderId("ORD123")
                .amount(new BigDecimal("499.99"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN123")
                .build();

        when(paymentService.getPaymentByOrderId("ORD123")).thenReturn(response);

        mockMvc.perform(get("/api/payments/order/ORD123"))
                .andExpect(status().isOk()) // ✅ now works
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }

    @Test
    void updateStatus_ValidRequest_ShouldReturn200() throws Exception {
        PaymentStatusUpdateRequest updateRequest = new PaymentStatusUpdateRequest(PaymentStatus.SUCCESS);

        ResponsePayment response = ResponsePayment.builder()
                .paymentId(1L)
                .orderId("ORD123")
                .amount(new BigDecimal("499.99"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN123")
                .build();

        when(paymentService.updatePaymentStatus(eq(1L), any(PaymentStatusUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // ✅ now works
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }
}