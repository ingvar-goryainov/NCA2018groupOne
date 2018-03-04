package ncadvanced2018.groupeone.parent.service.impl;

import lombok.extern.slf4j.Slf4j;
import ncadvanced2018.groupeone.parent.dao.AddressDao;
import ncadvanced2018.groupeone.parent.dao.OrderDao;
import ncadvanced2018.groupeone.parent.dto.OrderHistory;
import ncadvanced2018.groupeone.parent.event.UpdateOrderEvent;
import ncadvanced2018.groupeone.parent.exception.EntityNotFoundException;
import ncadvanced2018.groupeone.parent.exception.NoSuchEntityException;
import ncadvanced2018.groupeone.parent.model.entity.Address;
import ncadvanced2018.groupeone.parent.model.entity.Order;
import ncadvanced2018.groupeone.parent.model.entity.OrderStatus;
import ncadvanced2018.groupeone.parent.model.entity.User;
import ncadvanced2018.groupeone.parent.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private OrderDao orderDao;
    private AddressDao addressDao;
    private final ApplicationEventPublisher publisher;


    @Autowired
    public OrderServiceImpl(OrderDao orderDao, AddressDao addressDao, ApplicationEventPublisher publisher) {
        this.orderDao = orderDao;
        this.addressDao = addressDao;
        this.publisher = publisher;
    }

    @Override
    public Order create(Order order) {

        if (order == null) {
            log.info("Order object is null by creating");
            throw new EntityNotFoundException("Order object is null");
        }

        User user = order.getUser();
        if (user == null) {
            log.info("User object is null by creating");
            throw new EntityNotFoundException("User object is null");
        }

        OrderStatus orderStatus = order.getOrderStatus();
        if (orderStatus == null) {
            log.info("Order Status is null by creation");
            throw new EntityNotFoundException("OrderStatus is null");
        }

        Address receiverAddress = order.getReceiverAddress();
        if (receiverAddress != null) {
            receiverAddress = addressDao.create(receiverAddress);
            order.setReceiverAddress(receiverAddress);
        }

        Address senderAddress = order.getSenderAddress();
        if (senderAddress != null) {
            senderAddress = addressDao.create(senderAddress);
            order.setSenderAddress(senderAddress);
        }

        LocalDateTime creationTime = LocalDateTime.now();
        order.setCreationTime(creationTime);
        Order createdOrder = orderDao.create(order);
        return createdOrder;
    }

    @Override
    public Order findById(Long id) {
        if (id <= 0) {
            log.info("Illegal id");
            throw new IllegalArgumentException();
        }
        return orderDao.findById(id);
    }

    @Override
    public List <OrderHistory> findByUserId(Long userId) {
        if (userId <= 0) {
            log.info("Illegal user id");
            throw new IllegalArgumentException();
        }
        List <Order> orders = orderDao.findByUserId(userId);
        List <OrderHistory> orderHistories = new ArrayList <>();
        for (Order order : orders) {
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setId(order.getId());
            orderHistory.setCreationTime(order.getCreationTime());
            orderHistory.setOrderStatus(order.getOrderStatus());
            orderHistory.setSenderAddress(order.getSenderAddress());
            orderHistory.setReceiverAddress(order.getReceiverAddress());
            orderHistories.add(orderHistory);
        }
        return orderHistories;
    }

    @Override
    public Order update(Order order) {
        if (order == null) {
            log.info("Order object is null by creating");
            throw new EntityNotFoundException("Order object is null");
        }

        if (orderDao.findById(order.getId()) == null) {
            log.info("No such order entity");
            throw new NoSuchEntityException("Order id is not found");
        }

        User user = order.getUser();
        if (user == null) {
            log.info("User object is null by creating");
            throw new EntityNotFoundException("User object is null");
        }
        Address receiverAddress = order.getReceiverAddress();
        addressDao.update(receiverAddress);
        order.setReceiverAddress(receiverAddress);

        Address senderAddress = order.getSenderAddress();
        if (senderAddress != null) {
            addressDao.update(senderAddress);
            order.setSenderAddress(senderAddress);
        }
        Order original = findById(order.getId());
        Order updatedOrder = orderDao.update(order);

        UpdateOrderEvent updateOrderEvent = new UpdateOrderEvent(this, original, updatedOrder);
        publisher.publishEvent(updateOrderEvent);

        return updatedOrder;
    }

    @Override
    public List <Order> findAllOrders() {
        List <Order> orders = orderDao.findAllOrders();
        return orders;
    }

    @Override
    public List <Order> findAllProcessingOrders() {
        List <Order> orders = orderDao.findAllProcessingOrders();
        return orders;
    }

    @Override
    public boolean delete(Order order) {
        if (order == null) {
            log.info("Order object is null by deleting");
            throw new EntityNotFoundException("Order object is null");
        }

        Address receiverAddress = order.getReceiverAddress();
        Address senderAddress = order.getSenderAddress();
        boolean isDeleted = orderDao.delete(order);
        addressDao.delete(receiverAddress);
        addressDao.delete(senderAddress);

        return isDeleted;
    }

    @Override
    public boolean delete(Long id) {
        if (id <= 0) {
            log.info("Illegal id");
            throw new IllegalArgumentException();
        }
        Order order = orderDao.findById(id);
        if (orderDao.findById(order.getId()) == null) {
            log.info("No such order entity");
            throw new NoSuchEntityException("Order id is not found");
        }


        boolean isDeleted = orderDao.delete(id);

        return isDeleted;
    }
}