package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MoonMissionRepositoryImpl implements MoonMissionRepository {

    private final JdbcDataSource dataSource;

    public MoonMissionRepositoryImpl(JdbcDataSource dataSource) {
        this.dataSource = dataSource;
    }


    // Menu Option 1 : List Moon Missions
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

    //Menu Option 2 : Get a moon mission by mission_id
    @Override
    public String getMoonMissionByID(int moonMissionId) {
            String query = "select * from moon_mission WHERE mission_id = ?";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, moonMissionId);

                try (ResultSet missionIdResult = statement.executeQuery()) {
                    if (!missionIdResult.next()) {
                        return null;
                    }

                     {
                        String missionType = missionIdResult.getString("mission_type");
                        String spacecraft = missionIdResult.getString("spacecraft");
                        String launchdate = missionIdResult.getString("launch_date");
                        String outcome = missionIdResult.getString("outcome");

                        return "Mission type: " + missionType + "\n" +
                                "Launch date: " + launchdate + "\n" +
                                "Outcome: " + outcome + "\n" +
                                "Spacecraft: " + spacecraft;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    //Menu Option 3 : Count missions for a given year
    @Override
    public int countMissionsByYear(int year) {

        String query = "SELECT count(*) FROM moon_mission WHERE YEAR(launch_date) = ?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, year);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()){
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
    }
    }
}
