package com.artichourey.ecommerce.paymentservice.util;

import java.util.UUID;

public class TransactionGenerate {
	
	public static String generate() {
		
		return "txn-"+UUID.randomUUID().toString();
		
	}
	

}
