package org.app.model;

import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;

import java.util.*;
import java.time.LocalDate;

public class MoonPhasesImpl implements MoonPhases {

    private final String SITE_URL = "https://www.moongiant.com/it/fase-lunare/";
    private final String BASE_IMG_URL = "https://www.moongiant.com/images/today_phase/";
    private final Map<String, String> MOON_INFO = new HashMap<>();
    private final Map<String, Map<String, String>> CACHE = new HashMap<>();
    private String date = "";

    public MoonPhasesImpl() {
        /* empty constructor */
    }

    public MoonPhasesImpl(final int year, final int month, final int day) {
        this.setDate(year, month, day);
    }

    @Override
    public void setDate(final int year, final int month, final int day) {
        final var builder = new StringBuilder();
        if (day < 10) builder.append("0");
        builder.append(day);
        builder.append("/");
        if (month < 10) builder.append("0");
        builder.append(month);
        builder.append("/");
        builder.append(year);
        this.date = builder.toString();
        this.MOON_INFO.put("date", this.date);
    }

    @Override
    public Optional<Map<String, String>> getMoonInfo() {
        if (this.date.isEmpty()) {
            final LocalDate now = LocalDate.now();
            final int year = now.getYear();
            final int month = now.getMonthValue();
            final int day = now.getDayOfMonth();
            this.setDate(year, month, day);
        }
        if (this.CACHE.containsKey(this.date)) {
            return Optional.of(this.CACHE.get(this.date));
        }
        if (!this.retrieveMoonPhaseInfo()) {
            return Optional.empty();
        }
        this.CACHE.put(this.date, MOON_INFO);
        return Optional.of(this.MOON_INFO);
    }

    @Override
    public String getDate() {
        return this.date;
    }

    @Override
    public String getImageURL(final String image_name) {
        return BASE_IMG_URL + image_name;
    }

    /* PRIVATES METHODS */

    private boolean retrieveMoonPhaseInfo() {
        try {
            final Document DOC = Jsoup.connect(this.SITE_URL + this.date).get();
            final Element all_info = DOC.getElementById("today_");
            if (all_info == null) {
                return false;
            }
            final String raw_text = all_info.html().trim();
            final String[] lines = raw_text.split("<br>");
            this.MOON_INFO.put("state", lines[lines.length - 2].trim());
            final String percIllumination = all_info.getElementsByTag("span")
                    .get(0).text().trim();
            this.MOON_INFO.put("illumination", percIllumination);
            final Element img = all_info.getElementsByTag("img").get(0);
            final String[] imgNameSplitted = img.attr("src")
                    .trim().split("/");
            this.MOON_INFO.put("image_name", imgNameSplitted[imgNameSplitted.length - 1]);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
