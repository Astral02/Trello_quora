package com.upgrad.quora.service.business;/* Created by Mansi Elhance */

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

//Service class for "signin" endpoint

@Service
public class AuthenticationBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException{

        //getting user by its entered email
        UserEntity userEntity = userDao.getUserByUsername(username);

        if (userEntity == null){
            throw new AuthenticationFailedException("ATH-001","This username does not exist.");
        }

        final  String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
         if (encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity = new UserAuthEntity();
            userAuthEntity.setUser_id(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthEntity.setAccess_token(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthEntity.setUuid(userEntity.getUuid());
            userAuthEntity.setLogin_at(now);
            userAuthEntity.setExpires_at(expiresAt);
            userDao.createAuthToken(userAuthEntity);
            userDao.updateUser(userEntity);

            return userAuthEntity;

        }else {
            throw new AuthenticationFailedException("ATH-002","Password Failed");
        }
    }


//  ----- For AdminController -----

    // Controller calls this getUser method
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String id , final String authorizedToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuth =  userDao.getUserAuthToken(authorizedToken);


        if(userAuth == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        final ZonedDateTime signOutUserTime = userAuth.getLogout_at();

        if(signOutUserTime!=null && userAuth!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }
        UserEntity user = userDao.getUserByUuid(id);

        if(user==null)
        {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist .");

        }
        else {
            return user;
        }
    }
}

