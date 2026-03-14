/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author iikan
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDateDTO {
    private LocalDate date;
    private List<LocalTime> availableTimes;
}
