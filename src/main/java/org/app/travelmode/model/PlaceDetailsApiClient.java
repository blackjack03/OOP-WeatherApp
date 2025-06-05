package org.app.travelmode.model;

import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;

import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Client implementation for Google Places Details API that retrieves specific
 * details about places, focusing on timezone information.
 */
public class PlaceDetailsApiClient extends AbstractGoogleApiClient implements PlaceDetails {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    /**
     * Constructs a new PlaceDetailsApiClient with the specified API key.
     *
     * @param apiKey the Google API key to use for requests.
     */
    public PlaceDetailsApiClient(final String apiKey) {
        super(BASE_URL, apiKey);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation:
     * <ul>
     *     <li>Requests only the UTC offset field to optimize response size</li>
     *     <li>Converts the UTC offset to a proper ZoneId</li>
     * </ul>
     */
    @Override
    public ZoneId getTimezone(final String placeId) {
        final GoogleApiRequestBuilder requestBuilder = new GoogleApiRequestBuilderImpl(BASE_URL, this.getApiKey());
        ZoneId zoneId = null;
        final String url = requestBuilder.addParameter("fields", "utc_offset")
                .addParameter("place_id", placeId)
                .build();
        try {
            final AdvancedJsonReader jsonReader = new AdvancedJsonReaderImpl(url);
            int utcOffset = jsonReader.getInt("result.utc_offset");
            zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(utcOffset * 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(zoneId);
        return zoneId;
    }
}
