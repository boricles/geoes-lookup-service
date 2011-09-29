package util;

import javax.persistence.Persistence;

public final class EntityManagerUtil {
    private static final javax.persistence.EntityManagerFactory emfInstance =
        Persistence.createEntityManagerFactory("lookupserviceOEG");

    private EntityManagerUtil() {}

    public static javax.persistence.EntityManagerFactory get() {
        return emfInstance;
    }
}
