package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.exception.BlacklistException;
import com.emil.linksy_user.exception.BlockedException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.BlackListRepository;
import com.emil.linksy_user.repository.SubscriptionsRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeopleService {
    private final UserRepository userRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final BlackListRepository blackListRepository;
    private final LinksyCacheManager linksyCacheManager;
    public List<UserResponse> findByLink(Long userId, String startsWith) {
        List<User> userList = userRepository.findByLinkStartingWith(startsWith);
        var filterUer = userList.stream().filter(user -> !user.getBlocked()).toList();
        return mapToUserResponse(userId, filterUer);
    }

    public List<UserResponse> findByUsername(Long userId, String startsWith) {
        List<User> userList = userRepository.findByUsernameStartingWith(startsWith);
        var filterUer = userList.stream().filter(user -> !user.getBlocked()).toList();
        return mapToUserResponse(userId, filterUer);
    }

    private List<UserResponse> mapToUserResponse(Long userId, List<User> users) {
        return users.stream()
                .filter(user -> !user.getId().equals(userId))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getAvatarUrl(),
                        user.getUsername(),
                        user.getLink(),user.getOnline(),user.getConfirmed()
                ))
                .collect(Collectors.toList());
    }

    public void subscribe(Long subscriberId,Long userId){
        User subscriber = linksyCacheManager.getUserById(subscriberId);
        User user = linksyCacheManager.getUserById(userId);
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setSubscriber(subscriber);
        subscriptions.setUser(user);
        subscriptionsRepository.save(subscriptions);
    }

    public void unsubscribe(Long subscriberId,Long userId){
        User subscriber = linksyCacheManager.getUserById(subscriberId);
        User user = linksyCacheManager.getUserById(userId);
        Subscriptions subscriptions = subscriptionsRepository.findByUserAndSubscriber(user,subscriber)
                .orElseThrow(() -> new NotFoundException("Subscriptions not found"));
        subscriptionsRepository.delete(subscriptions);
    }


    public List<UserResponse> getUserSubscribers(Long userId) {
        User user = linksyCacheManager.getUserById(userId);
        List<Subscriptions> subscriptionsList = subscriptionsRepository.findAllByUser(user);
        List<User> subscribers = subscriptionsList.stream()
                .map(Subscriptions::getSubscriber)
                .toList();
        return mapToUserResponse(userId, subscribers);
    }


    public List<UserResponse> getUserSubscriptions(Long userId) {
        User user = linksyCacheManager.getUserById(userId);
        List<Subscriptions> subscriptionsList = subscriptionsRepository.findAllBySubscriber(user);
        List<User> subscriptions = subscriptionsList.stream()
                .map(Subscriptions::getUser)
                .toList();
        return mapToUserResponse(userId,subscriptions);
    }



    public UserPageData getUserPageData (Long finderId,Long id){
        User finder = linksyCacheManager.getUserById(finderId);
        User user = linksyCacheManager.getUserById(id);
        if(user.getBlocked()) throw new BlockedException("User is blocked");
        boolean isBlockedByPageOwner = isBlocked(finder,user);
        if (isBlockedByPageOwner) throw new BlacklistException("Access is denied: you are blocked by the owner of the page.");
        String username = user.getUsername();
        String link = user.getLink();
        String avatarUrl = user.getAvatarUrl();
        String birthday = null;
        Boolean isSubscriber = subscriptionsRepository.existsByUserAndSubscriber(user,finder);
        Boolean isSubscription =  subscriptionsRepository.existsByUserAndSubscriber(finder,user);
        var userBirthday = user.getBirthday();
        if (userBirthday!=null){
            birthday = formatBirthday(userBirthday);
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        Long subscriptionsCount = subscriptionsRepository.countBySubscriber(user);
        Long subscribersCount = subscriptionsRepository.countByUser(user);
        Boolean isPageOwnerBlockedByViewer = isBlocked(user,finder);
        return new UserPageData(username,link,avatarUrl,birthday,isSubscriber,
                subscriptionsCount,subscribersCount, isPageOwnerBlockedByViewer,
                user.getMessageMode(),isSubscription,user.getConfirmed(),user.getOnline(),user.getLastActive().format(dateFormatter));
    }




    public boolean isSubscriber(User user,User finder){
       return subscriptionsRepository.existsByUserAndSubscriber(user,finder);
    }
    public boolean isSubscription(User user,User finder){
        return  subscriptionsRepository.existsByUserAndSubscriber(finder,user);
    }


    private String formatBirthday(Date birthday) {
        if (birthday == null) {
            return null;
        }

        java.sql.Date sqlDate = (java.sql.Date) birthday;
        LocalDate birthDate = sqlDate.toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(birthDate) + " (" + age + ")";
    }



       public void addToBlackList (Long initiatorId,Long userId){
           User initiator = linksyCacheManager.getUserById(initiatorId);
           User user = linksyCacheManager.getUserById(userId);

           BlackList blacklist = new BlackList();
           blacklist.setInitiator(initiator);
           blacklist.setBlocked(user);
           blackListRepository.save(blacklist);
       }


    public void removeFromBlackList (Long initiatorId,Long userId){
        User initiator = linksyCacheManager.getUserById(initiatorId);

        User user = linksyCacheManager.getUserById(userId);;

        BlackList blacklist = blackListRepository.findByInitiatorAndBlocked(initiator,user)
                .orElseThrow(() -> new NotFoundException("Not found"));
        blackListRepository.delete(blacklist);
    }


    public List<UserResponse> getEveryoneOffTheBlacklist (Long userId){
        User user = linksyCacheManager.getUserById(userId);;
        var blacklists = blackListRepository.findByInitiator(user);
        return blacklists.stream().map(blackList -> {
            User blocked = blackList.getBlocked();
            return new UserResponse(blocked.getId(), blocked.getAvatarUrl(), blocked.getUsername(), blocked.getLink(),blocked.getOnline(),blocked.getConfirmed());
        }).toList();

    }

    private boolean isBlocked (User finder,User user){
          return blackListRepository.existsByInitiatorAndBlocked(user,finder);
    }


}
