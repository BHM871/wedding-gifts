package com.example.wedding_gifts.core.usecases.account;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.example.wedding_gifts.infra.dtos.account.AccountResponseAccountDTO;
import com.example.wedding_gifts.infra.dtos.account.AccountResponseIdDTO;
import com.example.wedding_gifts.infra.dtos.account.UpdateAccountDTO;
import com.example.wedding_gifts.infra.dtos.commun.MessageDTO;

public interface IAccountController {

    public ResponseEntity<AccountResponseIdDTO> gifterBegin(String brideGroom) throws Exception;

    public ResponseEntity<AccountResponseAccountDTO> getAccountById(UUID id) throws Exception;

    public ResponseEntity<AccountResponseAccountDTO> updateAccount(String token, UpdateAccountDTO account, UUID id) throws Exception;

    public ResponseEntity<MessageDTO> deleteAccount(String token, UUID id) throws Exception;
}
