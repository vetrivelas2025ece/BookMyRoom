
package com.bookmyroom.bookmyroom.room;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // 1. Add a room
    @PostMapping
    public ResponseEntity<Room> addRoom(
            @Valid @RequestBody Room room) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomService.addRoom(room));
    }

    // 2. Get all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {

        return ResponseEntity.ok(
                roomService.getAllRooms());
    }

    // 3. Get room by ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                roomService.getRoomById(id));
    }

    // 4. Update room
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody Room room) {

        return ResponseEntity.ok(
                roomService.updateRoom(id, room));
    }

    // 5. Delete room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long id) {

        roomService.deleteRoom(id);

        return ResponseEntity.noContent().build();
    }

    // 6. Count rooms
    @GetMapping("/count")
    public ResponseEntity<Long> countRooms() {

        return ResponseEntity.ok(
                roomService.countRooms());
    }
}