package com.techelevator.dao;

import com.techelevator.model.Reservation;
import com.techelevator.model.Site;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Locale;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        Reservation r = new Reservation();

        KeyHolder keyholder = new GeneratedKeyHolder();

        String sql = "INSERT INTO reservation (site_id, name, from_date, to_date) VALUES (?, ?, ?, ?)";
        String sql2 = "SELECT * FROM reservation WHERE site_id = ? AND name = ? AND from_date = ? AND to_date = ?";
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(sql, new String[]{"reservation_id"});
                    ps.setInt(1, siteId);
                    ps.setString(2, name);
                    ps.setDate(3, Date.valueOf(fromDate));
                    ps.setDate(4, Date.valueOf(toDate));
                    return ps;
                }, keyholder);
//        SqlRowSet reservationInformation = jdbcTemplate.queryForRowSet(sql2, siteId, name, fromDate, toDate);
//        r = mapRowToReservation(reservationInformation);
//
//        int confirmationNumber = keyholder.getKey().intValue();
//        return confirmationNumber;
        return keyholder.getKey().intValue();
    }

    public List<Reservation> upcomingReservations(int parkId){
        List<Reservation> reservationsNextThirty = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        LocalDate thirtyOut = localDate.plusDays(30);
        String sql = "SELECT * FROM reservation r JOIN site s ON s.site_id = r.site_id JOIN campground c ON c.campground_id = s.campground_id WHERE c.park_id = ? AND r.from_date BETWEEN ? AND ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, parkId, localDate, thirtyOut);
        while (result.next()) {
            reservationsNextThirty.add(mapRowToReservation(result));
        }
        return reservationsNextThirty;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
