package stelnet.board.exploration;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.AnalyzeEntityMissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.SurveyPlanetMissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.BaseHistorianOffer;
import com.fs.starfarer.api.impl.campaign.intel.bases.LuddicPathBaseIntel;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseIntel;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import stelnet.filters.Filter;
import stelnet.filters.LogicalAnd;
import stelnet.filters.LogicalNot;
import stelnet.filters.LogicalOr;
import stelnet.filters.intel.IntelContainsTitle;
import stelnet.filters.intel.IntelFilter;
import stelnet.filters.intel.IntelIsClass;
import stelnet.filters.intel.IntelIsFaction;
import stelnet.filters.intel.IntelLocationHasMemory;
import stelnet.settings.CaptainsLogSettings;
import stelnet.util.ModConstants;

public class FilterFactory {

    private final Map<Banks, Filter<IntelInfoPlugin>> bankMap = new LinkedHashMap<>();
    private final Map<Types, Filter<IntelInfoPlugin>> typeMap = new LinkedHashMap<>();

    public FilterFactory() {
        final IntelFilter bankFilter = new IntelIsClass(BreadcrumbIntel.class);
        final IntelFilter captainsLogFilter = new IntelLocationHasMemory(ModConstants.CAPTAINS_LOG_INTEL);
        addTypes(bankFilter, captainsLogFilter);
        addBanks(bankFilter);
    }

    public Set<Banks> banks() {
        return bankMap.keySet();
    }

    public Set<Types> types() {
        return typeMap.keySet();
    }

    public Filter<IntelInfoPlugin> getFilter(final Banks key) {
        return bankMap.get(key);
    }

    public Filter<IntelInfoPlugin> getFilter(final FactionAPI faction) {
        return new LogicalAnd<IntelInfoPlugin>(
            Arrays.<Filter<IntelInfoPlugin>>asList(getFilter(Types.TYPE_RAIDING_BASE), new IntelIsFaction(faction)),
            "Raiding Faction: " + faction.getDisplayName()
        );
    }

    public Filter<IntelInfoPlugin> getFilter(final Types key) {
        return typeMap.get(key);
    }

    private void addTypes(final IntelFilter bankFilter, final IntelFilter captainsLogFilter) {
        final Map<Types, Filter<IntelInfoPlugin>> localMap = new LinkedHashMap<>();
        localMap.put(Types.TYPE_ANALYZE_MISSION, new IntelIsClass(AnalyzeEntityMissionIntel.class));
        localMap.put(Types.TYPE_HISTORIAN_OFFER, new IntelIsClass(BaseHistorianOffer.class));
        localMap.put(Types.TYPE_MEMORY_BANK, bankFilter);
        localMap.put(Types.TYPE_RAIDING_BASE, getRaidingBaseFilter());
        localMap.put(Types.TYPE_SURVEY_MISSION, new IntelIsClass(SurveyPlanetMissionIntel.class));
        if (CaptainsLogSettings.COLONY_STRUCTURES.isEnabled()) {
            localMap.put(Types.TYPE_COLONY_STRUCTURE, getTitleFilter(captainsLogFilter, "Structure"));
        }
        if (CaptainsLogSettings.COMM_RELAYS.isEnabled()) {
            localMap.put(Types.TYPE_COMM_RELAY, getTitleFilter(captainsLogFilter, "Comm Relay"));
        }
        if (CaptainsLogSettings.SALVAGEABLE.isEnabled()) {
            localMap.put(Types.TYPE_SALVAGEABLE, getTitleFilter(captainsLogFilter, "Salvageable"));
        }
        if (CaptainsLogSettings.RUINS.isEnabled()) {
            localMap.put(Types.TYPE_ANY_RUINS, getTitleFilter(captainsLogFilter, "Ruins"));
        }
        final Filter<IntelInfoPlugin> otherFilter = getOtherFilter(localMap.values());
        typeMap.put(Types.TYPE_OTHER, otherFilter);
        typeMap.putAll(localMap);
    }

    private void addBanks(final IntelFilter bankFilter) {
        final Map<Banks, Filter<IntelInfoPlugin>> localMap = new LinkedHashMap<>();
        localMap.put(Banks.BANK_ANY_CACHE, getTitleFilter(bankFilter, "Cache"));
        localMap.put(Banks.BANK_DEBRIS_FIELD, getTitleFilter(bankFilter, "Debris Field"));
        localMap.put(Banks.BANK_DERELICT_SHIP, getTitleFilter(bankFilter, "Derelict Ship"));
        localMap.put(Banks.BANK_DOMAIN_ERA_ENTITY, getTitleFilter(bankFilter, "Domain-era"));
        localMap.put(Banks.BANK_ORBITAL_HABITAT, getTitleFilter(bankFilter, "Orbital Habitat"));
        localMap.put(Banks.BANK_RUINS_LOCATION, getTitleFilter(bankFilter, "Ruins Location"));
        localMap.put(Banks.BANK_SURVEY_DATA, getTitleFilter(bankFilter, "Survey Data for"));
        final Filter<IntelInfoPlugin> otherFilter = new LogicalAnd<IntelInfoPlugin>(
            Arrays.asList(typeMap.get(Types.TYPE_MEMORY_BANK), getOtherFilter(localMap.values())),
            "Other Banks"
        );
        bankMap.put(Banks.BANK_OTHER, otherFilter);
        bankMap.putAll(localMap);
    }

    private Filter<IntelInfoPlugin> getTitleFilter(final IntelFilter bankFilter, final String title) {
        return new LogicalAnd<IntelInfoPlugin>(
            Arrays.<Filter<IntelInfoPlugin>>asList(bankFilter, new IntelContainsTitle(title)),
            "Compound Filter: " + title
        );
    }

    private Filter<IntelInfoPlugin> getOtherFilter(final Collection<Filter<IntelInfoPlugin>> filters) {
        return new LogicalNot<IntelInfoPlugin>(
            new LogicalOr<IntelInfoPlugin>(filters, "Compound Filter: Everything Else")
        );
    }

    private Filter<IntelInfoPlugin> getRaidingBaseFilter() {
        return new LogicalOr<IntelInfoPlugin>(
            Arrays.<Filter<IntelInfoPlugin>>asList(
                new IntelIsClass(LuddicPathBaseIntel.class),
                new IntelIsClass(PirateBaseIntel.class)
            ),
            "Compound Filter: Raiding Bases"
        );
    }
}
