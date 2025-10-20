package com.sk.restaurant.services;

import com.sk.restaurant.domain.GeoLocation;
import com.sk.restaurant.domain.entities.Address;

public interface GeoLocationService {
    GeoLocation geoLocate(Address address);
}
