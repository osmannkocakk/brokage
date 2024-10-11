package com.example.brokage;

import com.example.brokage.asset.model.Asset;
import com.example.brokage.asset.service.AssetNotFoundException;
import com.example.brokage.asset.service.AssetService;
import com.example.brokage.asset.service.NoEnoughAssetException;
import com.example.brokage.asset.service.NoEnoughMoneyException;
import com.example.brokage.customer.model.CustomerRoles;
import com.example.brokage.customer.model.Customer;
import com.example.brokage.customer.service.CustomerService;
import com.example.brokage.order.model.Order;
import com.example.brokage.order.model.OrderSide;
import com.example.brokage.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Date;

@SpringBootApplication
@RequiredArgsConstructor
public class BrokageApplication implements CommandLineRunner {


	private final CustomerService customerService;
	private final AssetService assetService;
	private final OrderService orderService;

	public static void main(String[] args) {
		SpringApplication.run(BrokageApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		// Create demo customers
		createCustomer();
		// Create demo assets
		createAsset();
		// Create demo orders
		createOrder();
	}

	private void createOrder() throws AssetNotFoundException, NoEnoughMoneyException, NoEnoughAssetException {
		Order orderBuyASSET1 = Order.builder()
				.assetName("ASSET1")
				.createDate(new Date(System.currentTimeMillis()))
				.price(10.80)
				.orderSide(OrderSide.BUY)
				.size(100)
				.customerId(1L)
				.build();
		orderService.createOrder(orderBuyASSET1);

		Order orderBuyASSET2 = Order.builder()
				.assetName("ASSET2")
				.createDate(new Date(System.currentTimeMillis()))
				.price(1)
				.orderSide(OrderSide.BUY)
				.size(100)
				.customerId(1L)
				.build();
		orderService.createOrder(orderBuyASSET2);
	}

	private void createAsset() {
		Asset Customer1TRY = Asset.builder()
				.assetName("TRY")
				.size(18000)
				.usableSize(18000)
				.customerId(1L)
				.build();
		assetService.create(Customer1TRY);

		Asset Customer1ASSET3 = Asset.builder()
				.assetName("ASSET3")
				.size(5500)
				.usableSize(5500)
				.customerId(1L)
				.build();
		assetService.create(Customer1ASSET3);

		Asset Customer2TRY = Asset.builder()
				.assetName("TRY")
				.size(100000)
				.usableSize(100000)
				.customerId(2L)
				.build();
		assetService.create(Customer2TRY);


	}

	private void createCustomer() {
		Customer cust1 = Customer.builder()
				.username("customer1")
				.name("Name1")
				.surname("Surname1")
				.password("Pass1")
				.role(CustomerRoles.CUSTOMER)
				.build();
		customerService.create(cust1);

		Customer cust2 = Customer.builder()
				.username("customer2")
				.name("Name2")
				.surname("Surname2")
				.password("Pass2")
				.role(CustomerRoles.CUSTOMER)
				.build();
		customerService.create(cust2);

		Customer admin = Customer.builder()
				.username("admin")
				.name("Admin Name")
				.surname("Admin Surname")
				.password("admin")
				.role(CustomerRoles.ADMIN)
				.build();
		customerService.create(admin);

	}
}
