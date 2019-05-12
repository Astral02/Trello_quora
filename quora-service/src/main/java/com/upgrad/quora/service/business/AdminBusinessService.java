package com.upgrad.quora.service.business;/* Create by Amit Punia */

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationBusinessService authenticationBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userid , final String accessToken) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity userEntity = authenticationBusinessService.getUser(userid,accessToken);
        UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if(userAuthTokenEntity.getUser_id().getRole().equals("admin")){
            return userDao.deleteUser(userDao.getUserByUuid(userid));
        }
        else
        {
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Logged in user is not an admin");
        }
    }
}
