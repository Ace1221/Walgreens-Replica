package com.walgreens.payment.service.command;

import com.stripe.exception.StripeException;
import com.stripe.model.SourceTransaction;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.walgreens.payment.cache.CustomerCache;
import com.walgreens.payment.model.CartItem;
import com.walgreens.payment.model.ProductsDto;
import com.walgreens.payment.repository.CouponRepository;
import com.walgreens.payment.repository.CustomerRepository;
import com.walgreens.payment.service.responses.CheckoutSessionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class CreateCheckoutCommand implements Command{
    private UUID customerUuid;

    private UUID couponUuid;

    private UUID cartUuid;

    private List<CartItem> cartItems=new ArrayList<>();
    private String sessionIdCart;

    Logger logger= LoggerFactory.getLogger(CreateCheckoutCommand.class);



    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CustomerCache customerCache;


    @Override
    public void execute() {
        try{
            String customerId = "";
            boolean customerExists = customerRepository.check_customer_exists(this.customerUuid);
            if(!customerExists){
                CreateCustomerCommand createCustomerCommand = (CreateCustomerCommand) applicationContext.getBean("createCustomerCommand");
                createCustomerCommand.setCustomerUuid(customerUuid);
                createCustomerCommand.execute();
            }
            if(customerCache.getStripeId(this.customerUuid) == null ){
                customerId = customerRepository.get_customer(this.customerUuid);
                System.out.println("i'll start hereeee");
                System.out.println(this.customerUuid);
                System.out.println(customerId);
                customerCache.cacheUserIds(this.customerUuid,customerId);
            }else{
                 customerId = customerCache.getStripeId(this.customerUuid);
            }
//            String customerId = customerRepository.get_customer(this.customerUuid);
            cartUuid = UUID.randomUUID();
            String couponId = null;
            if(couponUuid != null){
               couponId =  couponRepository.get_coupon(couponUuid);
            }
            String clientUrl = "https://localhost:4200";
            SessionCreateParams.Builder sessionCreateParamsBuilder =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setCustomer(customerId)
                            .setSuccessUrl(clientUrl + "/sucess?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(clientUrl + "/failure");

            List<ProductsDto> products = this.getProducts();

            for(ProductsDto product : products){
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity((long) product.getProductQuantity())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("USD")
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(product.getProductName())
                                        .build()
                                )
                                .setUnitAmountDecimal(BigDecimal.valueOf(product.getProductPrice() * 100))
                                .build()

                        )
                        .build();
                sessionCreateParamsBuilder.addLineItem(lineItem);

            }
//            sessionCreateParamsBuilder.addLineItem(
//                    SessionCreateParams.LineItem.builder()
//                            .setQuantity(1L)
//                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
//                                    .setCurrency("USD")
//                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                            .putMetadata("cart_id", "123")
//                                            .putMetadata("user_id", customerUuid.toString())
//                                            .setName("Towers XL")
//                                            .build()
//                                    )
//                                    .setUnitAmountDecimal(BigDecimal.valueOf(2300))
//                                    .build()
//                            )
//                            .build()
//            );

            sessionCreateParamsBuilder.setPaymentIntentData(
                    SessionCreateParams.PaymentIntentData.builder()
                            .putMetadata("cart_uuid", String.valueOf(cartUuid))
                            .putMetadata("customer_uuid", this.customerUuid.toString())
                            .putMetadata("sessionIdCart", sessionIdCart)
                            .build()
            );

            if(couponId != null){
                sessionCreateParamsBuilder.addDiscount(
                        SessionCreateParams.Discount.builder()
                                .setCoupon(couponId)
                                .build()
                );
            }



            System.out.println("CUSTOMER UUID: " + this.customerUuid);
            System.out.println("ORDER UUID: " + cartUuid);


            Session session = Session.create(sessionCreateParamsBuilder.build());

            CheckoutSessionResponse response= new CheckoutSessionResponse(session.getUrl(), null);

            System.out.println(response.getSessionUrl());

            logger.info(response.getSessionUrl());
            logger.info("Checking out");


        }catch (StripeException e){
            log.error("Error in command CreateCheckoutCommand " + e.getMessage());


        }
    }

    public List<ProductsDto> getProducts() {
        List<ProductsDto> products = new ArrayList<>();

        if (this.cartItems!=null && this.cartItems.size() > 0) {
            for (CartItem cartItem : this.cartItems) {
                ProductsDto productDto = new ProductsDto();
                productDto.setProductName(cartItem.getItemName());
                productDto.setProductPrice(cartItem.getPurchasedPrice());
                productDto.setProductQuantity(cartItem.getItemCount());
                products.add(productDto);
            }
        } else {
        ProductsDto product1 = new ProductsDto("Shoes XL", 40, 2);
        ProductsDto product2 = new ProductsDto("Towels", 10, 10);
        ProductsDto product3 = new ProductsDto("Pillows", 20, 5);
        ProductsDto product4 = new ProductsDto("Cups", 5, 20);
        ProductsDto product5 = new ProductsDto("Mobile", 5, 200);
        ProductsDto product6 = new ProductsDto("TV", 5, 200);
        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);
        products.add(product6);
    }
        return products;

    }
}
