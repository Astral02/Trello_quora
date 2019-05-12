package com.upgrad.quora.api.controller;/* Create by Amit Punia */

import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    @RequestMapping(method = RequestMethod.DELETE , path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<com.upgrad.quora.api.model.UserDeleteResponse> deleteUser(@PathVariable("userId") final String userid, @RequestHeader("authorization")
    final String authorization) throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {

        String uuid = adminBusinessService.deleteUser(userid ,authorization);
        com.upgrad.quora.api.model.UserDeleteResponse userDeleteResponse=new com.upgrad.quora.api.model.UserDeleteResponse().id(uuid).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<com.upgrad.quora.api.model.UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
