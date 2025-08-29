package tn.sip.property_service.services;

import tn.sip.property_service.entities.Property;
import tn.sip.property_service.entities.UserPreferenceProfile;

import java.util.List;

public interface LikeService {
    void likeProperty(Long propertyId, Long userId);

    void unlikeProperty(Long propertyId, Long userId);

    boolean isLikedByUser(Long propertyId, Long userId);

    long getLikesCount(Long propertyId);

    List<Property> getFavoritesByUser(Long userId);

    UserPreferenceProfile buildUserPreferenceProfile(List<Property> likedProperties);

    List<Property> getRecommendedPropertiesForUser(Long userId);
}
