package stelnet.board.exploration;

import java.util.Arrays;
import java.util.List;
import stelnet.util.L10n;

public enum Banks implements ButtonAware {
    BANK_ANY_CACHE,
    BANK_DEBRIS_FIELD,
    BANK_DERELICT_SHIP,
    BANK_DOMAIN_ERA_ENTITY,
    BANK_ORBITAL_HABITAT,
    BANK_OTHER,
    BANK_RUINS_LOCATION,
    BANK_SURVEY_DATA;

    public static List<ButtonAware> getAll() {
        final ButtonAware[] bankTypes = {
            Banks.BANK_ANY_CACHE,
            Banks.BANK_DEBRIS_FIELD,
            Banks.BANK_DERELICT_SHIP,
            Banks.BANK_DOMAIN_ERA_ENTITY,
            Banks.BANK_ORBITAL_HABITAT,
            Banks.BANK_RUINS_LOCATION,
            Banks.BANK_SURVEY_DATA,
            Banks.BANK_OTHER,
        };
        return Arrays.asList(bankTypes);
    }

    @Override
    public String getId() {
        return name();
    }

    @Override
    public String getTitle() {
        return L10n.exploration(name());
    }
}
