package com.projectparty.service;

import com.projectparty.dao.TradingItemDao;
import com.projectparty.dao.UserDao;
import com.projectparty.entities.RoleType;
import com.projectparty.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    public static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private static final int OIL_ID = 10;
    private static final int METALS_ID = 20;
    private static final long INITIAL_CASH = 100;
    private static final int INITIAL_ITEM_AMOUNT = 100;
    private static final int INITIAL_FROZEN_ITEM_AMOUNT = 0;

    @Autowired
    private final UserDao userDao;

    @Autowired
    private final TradingItemDao tradingItemDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, TradingItemDao tradingItemDao) {
        this.userDao = userDao;
        this.tradingItemDao = tradingItemDao;
    }

    @Override
    public boolean save(User user) {
        setUserData(user);
        logger.log(Level.SEVERE, "Current user data: ", user.toString());
        userDao.save(user);
        logger.log(Level.INFO, "New user has been saved to DB, username: " + user.getUsername());
        return true;
    }


    private void setUserData(User user) {
        user.setRole(RoleType.ROLE_USER);
        Map<Integer, Integer> map = Map.of(
                OIL_ID, INITIAL_ITEM_AMOUNT,
                METALS_ID, INITIAL_ITEM_AMOUNT
        );

        Map<Integer, Integer> frozenMap = Map.of(
                OIL_ID, INITIAL_FROZEN_ITEM_AMOUNT,
                METALS_ID, INITIAL_FROZEN_ITEM_AMOUNT
        );
        user.setCash(INITIAL_CASH);
        user.setItems(map);
        user.setFrozenItems(frozenMap);
    }

    @Override
    public List<User> readAll() {
        logger.log(Level.INFO, "Getting all users from database");
        return userDao.readAll();
    }

    @Override
    public User read(int id) {
        logger.log(Level.INFO, "Getting user's information from database, user_id =" + id);
        return userDao.read(id);
    }

    @Override
    public boolean update(User user, int id) {
        logger.log(Level.INFO, "Updating properties of user with id = " + id);
        return userDao.update(user, id);
    }

    @Override
    public boolean updateCash(User user, int id) {

        logger.log(Level.INFO, "Updating cash of user with id = " + id);

        Map<String, Integer> items = new HashMap<>();
        for(Integer key : user.getItems().keySet()){
            items.put(tradingItemDao.read(key).getName(), user.getItems().get(key));
        }

        Map<String, Integer> frozenItems = new HashMap<>();
        for(Integer key : user.getFrozenItems().keySet()){
            frozenItems.put(tradingItemDao.read(key).getName(), user.getFrozenItems().get(key));
        }
        user.setItemNames(items);
        user.setFrozenItemNames(frozenItems);

        return userDao.update(user, id);
    }

    @Override
    public boolean delete(int id) {
        logger.log(Level.INFO, "Deleting user with id = " + id);
        return userDao.delete(id);
    }

    @Override
    public boolean deal(User customer, User seller) {
        logger.log(Level.INFO, "Users are making a deal; users: "
                + customer.getUsername() + " and " + seller.getUsername());
        return userDao.update(customer, customer.getId()) &&
                userDao.update(seller, seller.getId());
    }
}
