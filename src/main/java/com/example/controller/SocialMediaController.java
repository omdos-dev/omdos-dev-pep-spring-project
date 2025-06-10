package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.AccountService.AuthenticationException;
import com.example.service.AccountService.DuplicateUsernameException;
import com.example.service.MessageService;


@RestController
@RequestMapping("/")
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account toRegister) {
        try {
            Account saved = accountService.register(toRegister);
            return ResponseEntity.ok(saved);
        } catch (DuplicateUsernameException dupEx) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException badReq) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account credentials) {
        try {
            Account loggedIn = accountService.login(credentials);
            return ResponseEntity.ok(loggedIn);
        } catch (AuthenticationException authEx) {
            // Username/password mismatch → HTTP 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message toPost) {
        try {
            Message saved = messageService.create(toPost);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException badReq) {
            // invalid text or postedBy not found → HTTP 400
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> all = messageService.getAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> found = messageService.getById(messageId);
        if (found.isPresent()) {
            return ResponseEntity.ok(found.get());
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer messageId) {
        boolean existed = messageService.deleteById(messageId);
        if (existed) {
            return ResponseEntity.ok(1);
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessageText(
            @PathVariable Integer messageId,
            @RequestBody Message partial
    ) {
        String newText = partial.getMessageText();
        try {
            boolean updated = messageService.updateText(messageId, newText);
            if (updated) {
                return ResponseEntity.ok(1);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException badReq) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable Integer accountId) {
        List<Message> userMessages = messageService.getByUser(accountId);
        return ResponseEntity.ok(userMessages);
    }
}
