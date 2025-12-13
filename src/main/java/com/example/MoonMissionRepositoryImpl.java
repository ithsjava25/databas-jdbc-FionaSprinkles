package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoonMissionRepositoryImpl implements MoonMissionRepository {

    private final JdbcDataSource dataSource;

    public MoonMissionRepositoryImpl(JdbcDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> listAllMoonMissions() {
        List<String> list = new ArrayList<>();
        String query = "select spacecraft from moon_mission";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()) {
                list.add(resultSet.getString("spacecraft"));
            }
            }
            catch (SQLException e){
                throw new RuntimeException(e);
            }
            return list;
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
