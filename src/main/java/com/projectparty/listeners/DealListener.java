package com.projectparty.listeners;

import com.projectparty.controllers.MessageController;
import com.projectparty.entities.Deal;
import com.projectparty.entities.TradingItem;
import com.projectparty.exceptions.BusinessException;
import com.projectparty.messages.DealMessage;
import com.projectparty.service.TradingItemServiceImpl;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DealListener implements PostUpdateEventListener, PostInsertEventListener {

    private final MessageController messageController;
    private final TradingItemServiceImpl tradingItemService;

    @Autowired
    public DealListener(MessageController messageController, TradingItemServiceImpl tradingItemService) {
        this.messageController = messageController;
        this.tradingItemService = tradingItemService;
    }

    private void onInsertOrUpdate(Deal deal) {
        //Update tradingItemPrice
        TradingItem tradingItem = tradingItemService.read(deal.getDealItemId());
        tradingItem.setPrice(deal.getPrice());
        tradingItemService.update(tradingItem, deal.getDealItemId());

        //Sending message to subscribers
        DealMessage dealMessage = new DealMessage(deal);
        try {
            messageController.sendToGraph(dealMessage);
        } catch (Exception e) {
            throw new BusinessException("Could not send message to subscribers", e);
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent postInsertEvent) {
        Object entity = postInsertEvent.getEntity();

        if (entity instanceof Deal) {
            onInsertOrUpdate((Deal) entity);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        Object entity = postUpdateEvent.getEntity();

        if (entity instanceof Deal) {
            onInsertOrUpdate((Deal) entity);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}
