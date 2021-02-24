package ru.job4j.store;

import ru.job4j.models.*;

import java.util.List;
import java.util.Set;

public interface Store {
    List<Advertisement> findAllAdvertisements();
    List<Brand> findAllBrands();
    List<Model> findModelByBrandId(Integer brandId);
    User findUserByLogin(String login);
    Integer createAdvertisement(Advertisement advertisement);
    void updateAdvertisement(Advertisement advertisement);
    Integer createUser(User user);
    Advertisement findAdvertisementById(Integer advertisementId);
    List<Status> findAllStatus();
    Set<Advertisement> findAdvertisementsByParams(Integer brandId, boolean withPhoto, boolean currDate);
}
