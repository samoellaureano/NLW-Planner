package com.rocketseat.planner.participant;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participants")
public class ParticipantController
{
	@Autowired
	private ParticipantRepository participantRepository;

	@PostMapping("/{id}/confirm")
	public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id,
		@RequestBody ParticipantRequestPayload payload)
	{
		Optional<Participant> participant = participantRepository.findById(id);
		if (participant.isPresent())
		{
			Participant rawParticipant = participant.get();
			rawParticipant.setIsConfirmed(true);
			rawParticipant.setName(payload.name());

			this.participantRepository.save(rawParticipant);
		}
		return participant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}