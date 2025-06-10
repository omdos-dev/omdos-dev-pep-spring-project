package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Message;
import com.example.repository.MessageRepository;
import com.example.repository.AccountRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepo;
    private final AccountRepository accountRepo;

    @Autowired
    public MessageService(MessageRepository messageRepo, AccountRepository accountRepo) {
        this.messageRepo = messageRepo;
        this.accountRepo = accountRepo;
    }

 
    public Message create(Message toPost) {
        if (toPost.getMessageText() == null || toPost.getMessageText().trim().isEmpty()) {
            throw new IllegalArgumentException("messageText cannot be blank.");
        }
        if (toPost.getMessageText().length() > 255) {
            throw new IllegalArgumentException("messageText cannot exceed 255 characters.");
        }
        // Validate postedBy exists
        Integer userId = toPost.getPostedBy();
        if (userId == null || !accountRepo.existsById(userId)) {
            throw new IllegalArgumentException("postedBy must refer to an existing user.");
        }
        // If timePostedEpoch not provided, set to current millis
        if (toPost.getTimePostedEpoch() == null) {
            toPost.setTimePostedEpoch(System.currentTimeMillis());
        }
        return messageRepo.save(toPost);
    }

    public List<Message> getAll() {
        return messageRepo.findAll();
    }


    public Optional<Message> getById(Integer messageId) {
        return messageRepo.findById(messageId);
    }

    public boolean deleteById(Integer messageId) {
        if (!messageRepo.existsById(messageId)) {
            return false;
        }
        messageRepo.deleteById(messageId);
        return true;
    }


    public boolean updateText(Integer messageId, String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("messageText cannot be blank.");
        }
        if (newText.length() > 255) {
            throw new IllegalArgumentException("messageText cannot exceed 255 characters.");
        }
        Optional<Message> existingOpt = messageRepo.findById(messageId);
        if (!existingOpt.isPresent()) {
            return false;
        }
        Message existing = existingOpt.get();
        existing.setMessageText(newText);
        messageRepo.save(existing);
        return true;
    }

    public List<Message> getByUser(Integer accountId) {
        return messageRepo.findAllByPostedBy(accountId);
    }
}
