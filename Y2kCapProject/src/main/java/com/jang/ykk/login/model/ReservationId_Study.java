package com.jang.ykk.login.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ReservationId_Study implements Serializable {
	
    private String location;
    private LocalDate reservationDate;
    private int seatNumber;
    private LocalTime entryTime;  // 엔티티와 동일하게 맞추기
    private LocalTime exitTime;

/*    public ReservationId_Study(String location, LocalDate reservationDate, LocalTime entryTime, int seatNumber) {
        this.location = location;
        this.reservationDate = reservationDate;
        this.entryTime = entryTime;
        this.seatNumber = seatNumber;
    }
*/
    // Getters, Setters, equals, and hashCode methods
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationId_Study that = (ReservationId_Study) o;
        return Objects.equals(location, that.location) &&
               Objects.equals(reservationDate, that.reservationDate) &&
               Objects.equals(entryTime, that.entryTime) &&
               seatNumber == that.seatNumber;
    }

    public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDate getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(LocalDate reservationDate) {
		this.reservationDate = reservationDate;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}

	public LocalTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalTime entryTime) {
		this.entryTime = entryTime;
	}

	public LocalTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalTime exitTime) {
		this.exitTime = exitTime;
	}

	@Override
    public int hashCode() {
        return Objects.hash(location, reservationDate, entryTime, seatNumber);
    }
}
