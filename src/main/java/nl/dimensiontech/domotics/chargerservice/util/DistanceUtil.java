package nl.dimensiontech.domotics.chargerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DistanceUtil {

    private static final double EARTH_RADIUS_IN_METERS = 6371007.177356707;

    public static double distanceBetween(double lat1, double lng1, double lat2, double lng2) {

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS_IN_METERS * c;
    }

}
