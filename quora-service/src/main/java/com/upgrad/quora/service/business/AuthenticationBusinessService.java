package com.upgrad.quora.service.business;/* Create by Mansi Elhance */

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
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
    public UserAuthEntity authenticate(final String email, final String password) throws AuthenticationFailedException{

        UserEntity userEntity = userDao.getUserByEmail(email);

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
}

