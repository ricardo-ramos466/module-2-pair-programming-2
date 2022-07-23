package com.techelevator.dao;

import com.techelevator.model.Site;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JdbcSiteDaoTests extends BaseDaoTests {

    private SiteDao dao;

    @Before
    public void setup() {
        dao = new JdbcSiteDao(dataSource);
    }

    @Test
    public void getSitesThatAllowRVs_Should_ReturnSites() {
        List<Site> sites = dao.getSitesThatAllowRVs(1);

        assertEquals(2,sites.size());
    }
    @Test
    public void getAvailableSites_Should_ReturnSites() {
        JdbcSiteDao site = new JdbcSiteDao(dataSource);
        Assert.assertEquals(2, site.getSitesWithoutReservations(1).size());
    }
    @Test
    public void getAvailableSitesDateRange_Should_ReturnSites() {
        JdbcSiteDao site = new JdbcSiteDao(dataSource);
        Assert.assertEquals(2, site.getAvailableSites(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), 1).size());
    }
}
