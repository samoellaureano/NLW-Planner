package com.rocketseat.planner.activity;

import com.rocketseat.planner.trip.Trip;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityService
{
	@Autowired
	private ActivityRepository repository;

	public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip)
		throws ActivityException
	{
		Activity newActivity = new Activity(payload.title(), payload.occurs_at(), trip);
		boolean dataIsRigth = this.validateActivityDate(trip.getStartAt(), trip.getEndAt(), newActivity.getOccursAt());
		if (!dataIsRigth)
		{
			throw new ActivityException("Activity date must be between trip start and end date.");
		}
		this.repository.save(newActivity);

		return new ActivityResponse(newActivity.getId());
	}

	public List<ActivityData> getAllActivitiesFromId(UUID tripId)
	{
		return this.repository.findByTripId(tripId).stream().map(
				activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt()))
			.toList();
	}

	public boolean validateActivityDate(LocalDateTime startAt, LocalDateTime endAt, LocalDateTime occursAt)
	{
		return occursAt.isAfter(startAt) && occursAt.isBefore(endAt);
	}
}
