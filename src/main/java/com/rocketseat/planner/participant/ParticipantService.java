package com.rocketseat.planner.participant;

import com.rocketseat.planner.trip.Trip;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService
{
	private final Logger logger = LoggerFactory.getLogger(ParticipantService.class);
	@Autowired
	private ParticipantRepository repository;

	public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip)
	{
		List<Participant> participants = participantsToInvite.stream()
			.map(email -> new Participant(email, trip)).toList();

		this.repository.saveAll(participants);
		participants.forEach(
			participant -> logger.debug("Participant registered: {} Email: {}", participant.getId(),
				participant.getEmail()));
	}

	public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip)
	{
		Participant participant = new Participant(email, trip);
		this.repository.save(participant);
		logger.debug("Participant registered: {} Email: {}", participant.getId(),
			participant.getEmail());
		return new ParticipantCreateResponse(participant.getId());
	}

	public List<ParticipantData> getAllParticipantsFromEvent(UUID tripId)
	{
		return this.repository.findByTripId(tripId).stream().map(
			participant -> new ParticipantData(participant.getId(), participant.getName(),
				participant.getEmail(), participant.getIsConfirmed())).toList();
	}

	public void triggerConfirmationEmailToParticipants(UUID tripId)
	{

	}

	public void triggerConfirmationEmailToParticipant(String email)
	{

	}
}