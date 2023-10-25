package com.example.wedding_gifts.application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wedding_gifts.adapters.security.ITokenManager;
import com.example.wedding_gifts.common.Validation;
import com.example.wedding_gifts.core.domain.dtos.account.AccountResponseAccountDTO;
import com.example.wedding_gifts.core.domain.dtos.account.CreateAccountDTO;
import com.example.wedding_gifts.core.domain.dtos.account.LoginDTO;
import com.example.wedding_gifts.core.domain.dtos.authentication.AuthenticationResponseDTO;
import com.example.wedding_gifts.core.domain.dtos.commun.MessageDTO;
import com.example.wedding_gifts.core.domain.exceptions.account.AccountInvalidValueException;
import com.example.wedding_gifts.core.domain.exceptions.account.AccountNotNullableException;
import com.example.wedding_gifts.core.domain.exceptions.common.MyException;
import com.example.wedding_gifts.core.domain.model.Account;
import com.example.wedding_gifts.core.usecases.account.IAccountRepository;
import com.example.wedding_gifts.core.usecases.auth.IAuthenticationController;
import com.example.wedding_gifts.core.usecases.token.ITokenUseCase;

import lombok.var;

@RestController
@RequestMapping("/auth")
public class AuthenticationController implements IAuthenticationController {

    @Autowired
    private IAccountRepository repository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ITokenManager tokenManager;
    @Autowired
    private ITokenUseCase tokenService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<AccountResponseAccountDTO> register(
        @RequestBody CreateAccountDTO account
    ) throws Exception {
        try{
            validData(account);

            if(repository.getByEmail(account.email()) != null) return ResponseEntity.status(HttpStatus.CONFLICT).build();

            String encrypPassword = new BCryptPasswordEncoder().encode(account.password());

            CreateAccountDTO createAccount = new CreateAccountDTO(
                account.firstName(), 
                account.lastName(), 
                account.brideGroom(), 
                account.weddingDate(), 
                account.email(), 
                encrypPassword, 
                account.pixKey()
            );

            Account savedAccount = repository.createAccount(createAccount);

            AccountResponseAccountDTO newAccount = new AccountResponseAccountDTO(
                savedAccount.getId(), 
                savedAccount.getBrideGroom(), 
                savedAccount.getWeddingDate(), 
                savedAccount.getFirstName(), 
                savedAccount.getLastName(), 
                savedAccount.getEmail());


            return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
        } catch (MyException e){
            e.setPath("/auth/register");
            throw e;
        }
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(
        @RequestBody LoginDTO login
    ) throws Exception {
        try{
            validData(login);

            var usernamePassword = new UsernamePasswordAuthenticationToken(login.email(), login.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            String token = tokenManager.generatorToken((Account) auth.getPrincipal());

            if(auth.isAuthenticated()) return ResponseEntity.ok(new AuthenticationResponseDTO(token));

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (MyException e){
            e.setPath("/auth/login");
            throw e;
        }
    }

    @Override
    @PatchMapping("/logout")
    public ResponseEntity<MessageDTO> logout(
        @RequestParam String token
    ) {
        tokenService.deleteToken(token);

        return ResponseEntity.ok(new MessageDTO("successfully"));
    }

    private void validData(CreateAccountDTO data) throws Exception {
        String invalid = "%s is invalid";
        String isNull = "%s value is null";
        
        if(data.brideGroom() == null || data.brideGroom().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "brideGroom"));
        if(data.weddingDate() == null) throw new AccountNotNullableException(String.format(isNull, "weddingDate"));
        if(data.firstName() == null || data.firstName().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "firstName"));
        if(data.lastName() == null || data.lastName().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "lastName"));
        if(data.email() == null || data.email().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "email"));
        if(data.password() == null || data.password().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "password"));
        if(data.pixKey() == null || data.pixKey().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "pixKey"));
        
        if(!Validation.brideGroom(data.brideGroom())) throw new AccountInvalidValueException(String.format(invalid, "brideGroom"));
        if(!Validation.date(data.weddingDate())) throw new AccountInvalidValueException(String.format(invalid, "weddingDate"));
        if(!Validation.name(data.firstName(), data.lastName())) throw new AccountInvalidValueException(String.format(invalid, "firstName", "and/or", "lastName").replace("is", "are"));
        if(!Validation.email(data.email())) throw new AccountInvalidValueException(String.format(invalid, "email"));
        if(!Validation.password(data.password())) throw new AccountInvalidValueException(String.format(invalid, "password"));
        if(!Validation.pixKey(data.pixKey())) throw new AccountInvalidValueException(String.format(invalid, "pixKey"));
   
    }

    private void validData(LoginDTO data) throws Exception {
        String invalid = "%s is invalid";
        String isNull = "%s is null";

        if(data.email() == null || data.email().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "email"));
        if(data.password() == null || data.password().isEmpty()) throw new AccountNotNullableException(String.format(isNull, "password"));
        
        if(!Validation.email(data.email())) throw new AccountInvalidValueException(String.format(invalid, "email"));
        if(!Validation.password(data.password())) throw new AccountInvalidValueException(String.format(invalid, "password"));
        
    }
    
}
