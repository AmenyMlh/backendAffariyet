package tn.sip.property_service.servicesImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sip.property_service.entities.Property;
import tn.sip.property_service.entities.PropertyLike;
import tn.sip.property_service.entities.UserPreferenceProfile;
import tn.sip.property_service.enums.PropertyStatus;
import tn.sip.property_service.repositories.PropertyLikeRepository;
import tn.sip.property_service.repositories.PropertyRepository;
import tn.sip.property_service.services.LikeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PropertyRepository propertyRepository;
    private final PropertyLikeRepository propertyLikeRepository;

    @Override
    @Transactional
    public void likeProperty(Long propertyId, Long userId) {
        if (!propertyLikeRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            PropertyLike like = new PropertyLike();
            like.setUserId(userId);
            like.setProperty(property);

            propertyLikeRepository.save(like);

            property.setLikeCount(property.getLikeCount() + 1);
            propertyRepository.save(property);
        }
    }

    @Override
    @Transactional
    public void unlikeProperty(Long propertyId, Long userId) {
        if (propertyLikeRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            propertyLikeRepository.deleteByUserIdAndPropertyId(userId, propertyId);

            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            property.setLikeCount(Math.max(0, property.getLikeCount() - 1));
            propertyRepository.save(property);
        }
    }

    @Override
    public boolean isLikedByUser(Long propertyId, Long userId) {
        return propertyLikeRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }

    @Override
    public long getLikesCount(Long propertyId) {
        return propertyLikeRepository.countByPropertyId(propertyId);
    }

    @Override
    public List<Property> getFavoritesByUser(Long userId) {
        List<PropertyLike> likes = propertyLikeRepository.findPropertiesLikedByUser(userId);
        return likes.stream()
                .map(PropertyLike::getProperty)
                .collect(Collectors.toList());
    }

    @Override
    public UserPreferenceProfile buildUserPreferenceProfile(List<Property> likedProperties) {
        UserPreferenceProfile profile = new UserPreferenceProfile();

        if (likedProperties.isEmpty()) return profile;

        profile.setMostCommonLocation(
                likedProperties.stream()
                        .collect(Collectors.groupingBy(Property::getLocation, Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey()
        );

        profile.setMostLikedType(
                likedProperties.stream()
                        .collect(Collectors.groupingBy(Property::getType, Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey()
        );

        profile.setAvgPrice(
                likedProperties.stream().mapToDouble(Property::getPrice).average().orElse(0)
        );

        profile.setAvgArea(
                likedProperties.stream().mapToDouble(Property::getArea).average().orElse(0)
        );

        profile.setAvgPieces(
                (int) likedProperties.stream().mapToInt(Property::getPiecesNumb).average().orElse(0)
        );

        return profile;
    }

    @Override
    public List<Property> getRecommendedPropertiesForUser(Long userId) {
        List<PropertyLike> likes = propertyLikeRepository.findPropertiesLikedByUser(userId);
        List<Property> likedProperties = likes.stream()
                .map(PropertyLike::getProperty)
                .toList();

        if (likedProperties.isEmpty()) {
            return new ArrayList<>();
        }

        UserPreferenceProfile profile = buildUserPreferenceProfile(likedProperties);

        Set<Long> likedPropertyIds = likedProperties.stream()
                .map(Property::getId)
                .collect(Collectors.toSet());

        List<Property> recommended = propertyRepository.findAll().stream()
                .filter(p -> !likedPropertyIds.contains(p.getId()))
                .filter(p -> p.getPropertyStatus() == PropertyStatus.AVAILABLE)

                // 🔥 Remplace equals par contient (plus souple)
                .filter(p -> p.getLocation() != null &&
                        p.getLocation().toLowerCase().contains(profile.getMostCommonLocation().split(",")[0].toLowerCase()))

                .filter(p -> p.getType() == profile.getMostLikedType())

                // 🔄 élargir les marges de tolérance
                .filter(p -> Math.abs(p.getPrice() - profile.getAvgPrice()) <= 1000)
                .filter(p -> Math.abs(p.getArea() - profile.getAvgArea()) <= 50)
                .filter(p -> Math.abs(p.getPiecesNumb() - profile.getAvgPieces()) <= 2)

                .limit(10)
                .toList();

        // 🔁 Fallback
        if (recommended.isEmpty()) {
            recommended = propertyRepository.findAll().stream()
                    .filter(p -> !likedPropertyIds.contains(p.getId()))
                    .filter(p -> p.getPropertyStatus() == PropertyStatus.AVAILABLE)
                    .limit(5)
                    .toList();
        }

        return recommended;
    }




}
