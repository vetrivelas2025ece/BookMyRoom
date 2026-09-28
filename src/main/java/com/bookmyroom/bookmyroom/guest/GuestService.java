
package com.bookmyroom.bookmyroom.guest;

import com.bookmyroom.bookmyroom.booking.BookingRepository;

import com.bookmyroom.bookmyroom.exceptions.BookingConflictException;
import com.bookmyroom.bookmyroom.exceptions.DuplicateResourceException;
import com.bookmyroom.bookmyroom.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;

    // 1. Add a new guest
    public Guest addGuest(Guest guest) {

        if (guestRepository.existsByEmail(guest.getEmail())) {

            throw new DuplicateResourceException(
                    "Guest email already exists: "
                            + guest.getEmail()
            );
        }

        return guestRepository.save(guest);
    }

    // 2. Get all guests
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    // 3. Get a guest by ID
    public Guest getGuestById(Long id) {

        return guestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Guest not found: " + id
                        )
                );
    }

    // 4. Get a guest by email
    public Guest getGuestByEmail(String email) {

        return guestRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Guest not found with email: " + email
                        )
                );
    }

    // 5. Update guest details
    public Guest updateGuest(Long id, Guest updatedGuest) {

        Guest existingGuest = guestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Guest not found: " + id
                        )
                );

        // Check whether the email is changing
        if (!existingGuest.getEmail()
                .equals(updatedGuest.getEmail())) {

            if (guestRepository.existsByEmail(
                    updatedGuest.getEmail())) {

                throw new DuplicateResourceException(
                        "Guest email already exists: "
                                + updatedGuest.getEmail()
                );
            }
        }

        existingGuest.setGuestName(
                updatedGuest.getGuestName()
        );

        existingGuest.setEmail(
                updatedGuest.getEmail()
        );

        existingGuest.setPhoneNumber(
                updatedGuest.getPhoneNumber()
        );

        return guestRepository.save(existingGuest);
    }

    // 6. Delete a guest
    @Transactional
    public void deleteGuest(Long id) {

        Guest guest = guestRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Guest not found: " + id
                        )
                );

        // Preserve all booking history
        if (bookingRepository.existsByGuestGuestId(id)) {

            throw new BookingConflictException(
                    "Cannot delete guest "
                            + guest.getGuestName()
                            + " because they have booking history"
            );
        }

        guestRepository.delete(guest);
    }

    // 7. Count total guests
    public long countGuests() {
        return guestRepository.count();
    }
}