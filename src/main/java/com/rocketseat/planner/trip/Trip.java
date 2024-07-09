package com.rocketseat.planner.trip;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(nullable = false)
	private String destination;

	@Column(name = "starts_at", nullable = false)
	private LocalDateTime startAt;

	@Column(name = "ends_at", nullable = false)
	private LocalDateTime endAt;

	@Column(name = "is_confirmed", nullable = false)
	private Boolean isConfirmed;

	@Column(name = "owner_name", nullable = false)
	private String ownerName;

	@Column(name = "owner_email", nullable = false)
	private String ownerEmail;

	public Trip(TripRequestPayload data)
	{
		this.destination = data.destination();
		this.startAt = LocalDateTime.parse(data.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
		this.endAt = LocalDateTime.parse(data.ends_at(), DateTimeFormatter.ISO_DATE_TIME);
		this.isConfirmed = false;
		this.ownerName = data.owner_name();
		this.ownerEmail = data.owner_email();
	}
}