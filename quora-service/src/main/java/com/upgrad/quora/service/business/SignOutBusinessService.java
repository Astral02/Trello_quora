package com.upgrad.quora.service.business;/* Create by Mansi Elhance */

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

//Service class for "signout" endpoint

@Service
public class SignOutBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String authorizationToken) throws SignOutRestrictedException {

        UserAuthEntity userAuthEntity=userDao.getUserAuthToken(authorizationToken);
        if(userAuthEntity == null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        else{
            userAuthEntity.setLogout_at(ZonedDateTime.now());
            userDao.updateUserEntity(userAuthEntity);
        }
        return userAuthEntity.getUser_id();
    }
}
