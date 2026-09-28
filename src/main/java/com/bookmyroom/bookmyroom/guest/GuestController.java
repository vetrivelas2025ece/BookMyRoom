
package com.bookmyroom.bookmyroom.guest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    // 1. Register a guest
    @PostMapping
    public ResponseEntity<Guest> addGuest(
            @Valid @RequestBody Guest guest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guestService.addGuest(guest));
    }

    // 2. Retrieve all guests
    @GetMapping
    public ResponseEntity<List<Guest>> getAllGuests() {

        return ResponseEntity.ok(
                guestService.getAllGuests());
    }

    // 3. Retrieve guest by ID
    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                guestService.getGuestById(id));
    }

    // 4. Find guest by email
    @GetMapping("/email")
    public ResponseEntity<Guest> getGuestByEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(
                guestService.getGuestByEmail(email));
    }

    // 5. Update guest details
    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(
            @PathVariable Long id,
            @Valid @RequestBody Guest guest) {

        return ResponseEntity.ok(
                guestService.updateGuest(id, guest));
    }

    // 6. Delete a guest
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(
            @PathVariable Long id) {

        guestService.deleteGuest(id);

        return ResponseEntity.noContent().build();
    }

    // 7. Count registered guests
    @GetMapping("/count")
    public ResponseEntity<Long> countGuests() {

        return ResponseEntity.ok(
                guestService.countGuests());
    }
}