package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService
{
	@Autowired
	private TripRepository repository;

	public boolean validateEndDate(LocalDateTime startDate, LocalDateTime endDate)
	{
		return startDate.isBefore(endDate);
	}
}
