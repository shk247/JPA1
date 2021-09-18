package com.example.jpashop.service;

import com.example.jpashop.domain.Address;
import com.example.jpashop.domain.Member;
import com.example.jpashop.domain.Order;
import com.example.jpashop.domain.OrderStatus;
import com.example.jpashop.domain.item.Book;
import com.example.jpashop.domain.item.Item;
import com.example.jpashop.exception.NotEnoughStockException;
import com.example.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
//    @Rollback(value = false)
    public void 상품주문() throws Exception {
        //given
        Member member = createMember("회원");

        Book book = createBook("jpa", 10000, 10);

        System.out.println("book.getId() = " + book.getId());
        System.out.println("member.getId() = " + member.getId());

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        Assert.assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류 수가 정확해야 한다", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격*수량 이다", 10000 * orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야한다", 8, book.getStockQuantity());
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember("member");
        Book book = createBook("book", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOne(orderId);

        Assert.assertEquals("주문 상태는 cancel입니다", OrderStatus.CANCEL, order.getStatus());
        Assert.assertEquals("주문 취소된 상품은 그만큼 주문이 증가해야 한다", 10, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember("회원");
        Item book = createBook("jpa", 10000, 10);

        //when
        int orderCount = 11;
        orderService.order(member.getId(), book.getId(), orderCount);

        //then
        fail("재고 수량 북족 예외 발생해야 한다.");
    }


    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울","경기","123"));
        em.persist(member);
        return member;
    }


}
