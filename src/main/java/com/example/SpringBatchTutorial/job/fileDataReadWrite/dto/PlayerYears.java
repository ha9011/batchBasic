package com.example.SpringBatchTutorial.job.fileDataReadWrite.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Year;

@Data
public class PlayerYears {
    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
    private int yearsExperience;

    @Builder
    public PlayerYears(String ID, String lastName, String firstName, String position, int birthYear, int debutYear, int yearsExperience) {
        this.ID = ID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.birthYear = birthYear;
        this.debutYear = debutYear;
        this.yearsExperience = yearsExperience;
    }

    public static PlayerYears create(Player player){

        return PlayerYears.builder()
                .ID(player.getID())
                .lastName(player.getLastName())
                .firstName(player.getFirstName())
                .position(player.getPosition())
                .birthYear(player.getBirthYear())
                .debutYear(player.getDebutYear())
                .yearsExperience(Year.now().getValue() - player.getDebutYear())
                .build();
    }
}
