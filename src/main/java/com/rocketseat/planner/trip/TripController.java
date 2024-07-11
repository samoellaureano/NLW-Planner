package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.*;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.ParticipantCreateResponse;
import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
public class TripController
{
	@Autowired
	private TripService tripService;

	@Autowired
	private ParticipantService participantService;

	@Autowired
	private TripRepository repository;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private LinkService linkService;

	//	TRIPS
	@PostMapping
	public ResponseEntity<Object> createTrip(@RequestBody TripRequestPayload payload)
	{
		Trip newTrip = new Trip(payload);
		boolean dataIsRigth = this.tripService.validateEndDate(newTrip.getStartAt(), newTrip.getEndAt());
		if (!dataIsRigth)
		{
			return ResponseEntity.badRequest()
				.body(new TripErrorResponse("End date must be after start date."));
		}
		this.repository.save(newTrip);
		this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

		return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id)
	{
		Optional<Trip> trip = this.repository.findById(id);
		return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Trip> updateTrip(@PathVariable UUID id,
		@RequestBody TripRequestPayload payload)
	{
		Optional<Trip> trip = this.repository.findById(id);
		if (trip.isPresent())
		{
			Trip rawTrip = trip.get();
			rawTrip.setDestination(payload.destination());
			rawTrip.setStartAt(LocalDateTime.parse(payload.starts_at()));
			rawTrip.setEndAt(LocalDateTime.parse(payload.ends_at()));
			this.repository.save(rawTrip);
		}

		return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/confirm")
	public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id)
	{
		Optional<Trip> trip = this.repository.findById(id);
		if (trip.isPresent())
		{
			Trip rawTrip = trip.get();
			rawTrip.setIsConfirmed(true);
			this.participantService.triggerConfirmationEmailToParticipants(rawTrip.getId());
			this.repository.save(rawTrip);
		}

		return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	//	PARTICIPANTS
	@PostMapping("/{id}/invite")
	public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,
		@RequestBody ParticipantRequestPayload payload)
	{
		Optional<Trip> trip = this.repository.findById(id);
		if (trip.isPresent())
		{
			Trip rawTrip = trip.get();
			ParticipantCreateResponse participantCreateResponse = this.participantService.registerParticipantToEvent(
				payload.email(), rawTrip);

			if (rawTrip.getIsConfirmed())
			{
				this.participantService.triggerConfirmationEmailToParticipant(payload.email());
			}

			return ResponseEntity.ok(participantCreateResponse);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/participants")
	public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id)
	{
		List<ParticipantData> participantsList = this.participantService.getAllParticipantsFromEvent(id);

		return ResponseEntity.ok(participantsList);
	}

	//	ACTIVITIES
	@PostMapping("/{id}/activities")
	public ResponseEntity<Object> registerActivity(@PathVariable UUID id,
		@RequestBody ActivityRequestPayload payload)
	{
		Optional<Trip> trip = this.repository.findById(id);
		if (trip.isPresent())
		{
			Trip rawTrip = trip.get();
			try
			{
				ActivityResponse activityResponse = this.activityService.registerActivity(payload,
					rawTrip);
				return ResponseEntity.ok(activityResponse);
			}
			catch (Exception e)
			{
				return ResponseEntity.badRequest().body(new ActivityErrorResponse(e.getMessage()));
			}
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/activities")
	public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id)
	{
		List<ActivityData> activityDataList = this.activityService.getAllActivitiesFromId(id);

		return ResponseEntity.ok(activityDataList);
	}

	//	LINKS
	@PostMapping("/{id}/links")
	public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id,
		@RequestBody LinkRequestPayload payload)
	{
		Optional<Trip> trip = this.repository.findById(id);
		if (trip.isPresent())
		{
			Trip rawTrip = trip.get();
			LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);
			return ResponseEntity.ok(linkResponse);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/links")
	public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id)
	{
		List<LinkData> linkDataList = this.linkService.getAllLinksFromId(id);

		return ResponseEntity.ok(linkDataList);
	}
}