package com.techelevator.dao;

import com.techelevator.model.Campground;
import com.techelevator.model.Reservation;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {

        List<Site> sites = new ArrayList<>();

        String sql = "SELECT * FROM site JOIN campground c ON c.campground_id = site.campground_id WHERE c.park_id = ? AND site.max_rv_length > 0;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, parkId);
        while (result.next()) {
            sites.add(mapRowToSite(result));
        }

        return sites;
    }

    public List<Site> getSitesWithoutReservations(int parkId) {

        List<Site> sitesWithoutReservation = new ArrayList<>();

        String sql = "SELECT * FROM site s JOIN campground c ON c.campground_id = s.campground_id WHERE s.site_id NOT IN (SELECT site_id FROM reservation) AND c.park_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, parkId);
        while (result.next()) {
            sitesWithoutReservation.add(mapRowToSite(result));
        }
        return sitesWithoutReservation;

    }

    public List<Site> getAvailableSites(LocalDate fromDate, LocalDate toDate, int parkId) {

        List<Site> sitesAvailable = new ArrayList<>();

        String sql = "SELECT * FROM site s JOIN campground c ON c.campground_id = s.campground_id JOIN reservation r ON r.site_id = s.site_id WHERE ? NOT BETWEEN r.from_date AND r.to_date AND ? NOT BETWEEN r.from_date AND r.to_date AND c.park_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, fromDate, toDate, parkId);
        while (result.next()) {
            sitesAvailable.add(mapRowToSite(result));
        }
        return sitesAvailable;

    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
