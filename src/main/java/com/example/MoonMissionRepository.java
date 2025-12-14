package com.example;

import java.util.List;

public interface MoonMissionRepository {

    List<String> listAllMoonMissions();
    String getMoonMissionByID(int moonMissionId);
    int countMissionsByYear(int year);
}
