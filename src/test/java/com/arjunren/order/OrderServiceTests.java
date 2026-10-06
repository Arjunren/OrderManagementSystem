package com.arjunren.order;

import static com.arjunren.order.dto.Dtos.*;
import static org.assertj.core.api.Assertions.*;
import com.arjunren.order.domain.*;
import com.arjunren.order.entity.AppUser;
import com.arjunren.order.exception.DomainException;
import com.arjunren.order.repository.*;
import com.arjunren.order.service.OrderService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest @ActiveProfiles("test")
class OrderServiceTests {
    @Autowired OrderService service; @Autowired UserRepository users; @Autowired ProductRepository products; @Autowired OrderRepository orders; @Autowired AuthTokenRepository tokens; @Autowired PasswordEncoder encoder;
    AppUser admin,staff,customer,other;

    @BeforeEach void setup(){tokens.deleteAll();orders.deleteAll();products.deleteAll();users.deleteAll();admin=user("admin@test.dev",Role.ADMIN);staff=user("staff@test.dev",Role.STAFF);customer=user("customer@test.dev",Role.CUSTOMER);other=user("other@test.dev",Role.CUSTOMER);}

    @Test void placeOrderCalculatesTotalAndReservesStock(){var keyboard=product("KEY-1","Keyboard","50.00",10);var mouse=product("MOUSE-1","Mouse","20.00",8);var order=service.place(new OrderCreate(List.of(new OrderLineRequest(keyboard.id(),2),new OrderLineRequest(mouse.id(),1))),customer);assertThat(order.totalAmount()).isEqualByComparingTo("120.00");assertThat(products.findById(keyboard.id()).orElseThrow().getStock()).isEqualTo(8);assertThat(order.items()).hasSize(2);}

    @Test void insufficientStockRollsBackWholeOrder(){var scarce=product("SCARCE","Scarce","25.00",1);var error=catchThrowableOfType(()->service.place(new OrderCreate(List.of(new OrderLineRequest(scarce.id(),2))),customer),DomainException.class);assertThat(error.getStatus()).isEqualTo(HttpStatus.CONFLICT);assertThat(orders.count()).isZero();assertThat(products.findById(scarce.id()).orElseThrow().getStock()).isEqualTo(1);}

    @Test void customerCannotReadAnotherCustomersOrder(){var product=product("PRIVATE","Private","9.00",5);var order=service.place(new OrderCreate(List.of(new OrderLineRequest(product.id(),1))),customer);assertThatThrownBy(()->service.get(order.id(),other)).isInstanceOf(DomainException.class).extracting("status").isEqualTo(HttpStatus.FORBIDDEN);}

    @Test void cancellationRestoresReservedStock(){var product=product("RESTORE","Restore","10.00",5);var order=service.place(new OrderCreate(List.of(new OrderLineRequest(product.id(),3))),customer);service.cancel(order.id(),customer);assertThat(products.findById(product.id()).orElseThrow().getStock()).isEqualTo(5);assertThat(orders.findById(order.id()).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);}

    @Test void staffCanAdvanceOrderThroughFulfillment(){var product=product("SHIP","Ship","12.50",5);var order=service.place(new OrderCreate(List.of(new OrderLineRequest(product.id(),2))),customer);service.confirm(order.id(),staff);var fulfilled=service.fulfill(order.id(),admin);assertThat(fulfilled.status()).isEqualTo(OrderStatus.FULFILLED);assertThat(service.dashboard(staff).fulfilledRevenue()).isEqualByComparingTo("25.00");}

    private AppUser user(String email,Role role){return users.save(new AppUser(email,encoder.encode("TestPassword123!"),email,role));}
    private ProductResponse product(String sku,String name,String price,int stock){return service.createProduct(new ProductCreate(sku,name,"Test product",new BigDecimal(price),stock),admin);}
}
