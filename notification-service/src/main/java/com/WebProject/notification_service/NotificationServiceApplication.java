package com.WebProject.notification_service;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(
            topics = "notificationTopic")
    public void handleNotification(OrderPlacedEvent orderPlaceEvent)
    {

        // send out on email notification
        log.info("received notification for Order - {}",orderPlaceEvent.getOrderNumber());

        //System.out.println("Order Placed - "+orderPlaceEvent.getOrderNumber());

    }

}
