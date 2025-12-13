package com.example;

import java.util.List;

public class MoonMissionRepositoryImpl implements MoonMissionRepository {

    private final JdbcDataSource dataSource;

    public MoonMissionRepositoryImpl(JdbcDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> listAllMoonMissions() {

        return List.of();
    }

    @Override
    public String getMoonMissionByID(int moonMissionId) {
        return "";
    }

    @Override
    public int countMissionsByYear(int year) {
        return 0;
    }
}
